package cn.vtohru.message;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.message.annotation.MessageAddress;
import cn.vtohru.message.annotation.MessageClient;
import cn.vtohru.message.annotation.MessageType;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.type.Argument;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import javax.inject.Singleton;
import java.util.Map;


@Singleton
@InterceptorBean(MessageClient.class)
public class MessageConsumerIntroductionAdvice implements MethodInterceptor<Object, Object>, AutoCloseable{
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerIntroductionAdvice.class);
    private VerticleApplicationContext applicationContext;

    public MessageConsumerIntroductionAdvice(ApplicationContext applicationContext) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
    }

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        if (context.hasAnnotation(MessageAddress.class)) {
            AnnotationValue<MessageAddress> methodAnnotation = context.getAnnotation(MessageAddress.class);
            if (methodAnnotation == null) {
                throw new IllegalStateException("Invalid method" + context.getMethodName());
            }
            String address = methodAnnotation.stringValue().orElse("");
            MessageType.Type msgType = methodAnnotation.get("type", MessageType.Type.class).orElse(MessageType.Type.PUBLISH);
            Argument[] arguments = context.getExecutableMethod().getArguments();
            Map parameters = context.getParameterValueMap();
            JsonObject _json = new JsonObject();
            if (arguments.length > 0) {
                for (int i = 0; i < arguments.length; i++) {
                    Argument argument = arguments[i];
                    if (!argument.getType().isAssignableFrom(Handler.class)) {
                        _json.put(arguments[i].getName(), parameters.get(arguments[i].getName()));
                    }
                }
            }
            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addHeader("action", context.getMethodName());
            if (msgType == MessageType.Type.PUBLISH) {
                applicationContext.getVertx().eventBus().publish(address, _json, deliveryOptions);
            } else if (msgType == MessageType.Type.P2P) {
                applicationContext.getVertx().eventBus().send(address, _json);
            } else if (msgType == MessageType.Type.REQUEST) {
                if (Future.class.isAssignableFrom(context.getReturnType().getType())) {
                    Promise<Object> promise = Promise.promise();
                    applicationContext.getVertx().eventBus().request(address, _json, deliveryOptions, x -> {
                        if (x.succeeded()) {
                            Message<?> result = x.result();
                            promise.complete(result.body());
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                    return promise.future();
                } else if (arguments.length > 0) {
                    Object lastParam = parameters.get(arguments[arguments.length - 1].getName());
                    if (lastParam instanceof Handler) {
                        Handler handler = (Handler) lastParam;
                        applicationContext.getVertx().eventBus().request(address, _json, deliveryOptions, x -> {
                            if (x.succeeded()) {
                                Message<?> result = x.result();
                                handler.handle(Future.succeededFuture(result.body()));
                            } else {
                                handler.handle(Future.failedFuture(x.cause()));
                            }
                        });
                    } else {
                        throw new IllegalStateException("method last params must be handler or return type is Future");
                    }
                } else {
                    throw new IllegalStateException("method last params must be handler or return type is Future - " + context.getMethodName());
                }
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
