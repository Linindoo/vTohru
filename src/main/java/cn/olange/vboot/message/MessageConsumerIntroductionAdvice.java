package cn.olange.vboot.message;

import cn.olange.vboot.context.VerticleApplicationContext;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.type.Argument;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
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
        if (context.hasAnnotation(MessageClient.class)) {
            AnnotationValue<MessageAddress> methodAnnotation = context.getAnnotation(MessageAddress.class);
            if (methodAnnotation == null) {
                throw new IllegalStateException("Invalid method" + context.getMethodName());
            }
            String address = methodAnnotation.stringValue().orElse("");
            MessageType.Type msgType = methodAnnotation.getValue(MessageType.Type.class).orElse(MessageType.Type.PUBLISH);
            Argument[] arguments = context.getExecutableMethod().getArguments();
            if (arguments.length == 0) {
                throw new IllegalStateException("Invalid method" + context.getMethodName());
            }
            Map parameters = context.getParameterValueMap();
            JsonObject _json = new JsonObject();
            if (arguments.length > 1) {
                for (int i = 0; i < arguments.length - 1; i++) {
                    _json.put(arguments[i].getName(), parameters.get(arguments[i].getName()));
                }
            }
            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addHeader("action", context.getMethodName());
            if (msgType == MessageType.Type.PUBLISH) {
                applicationContext.getVertx().eventBus().publish(address, _json, deliveryOptions);
            } else if (msgType == MessageType.Type.P2P) {
                applicationContext.getVertx().eventBus().send(address, _json);
            } else if (msgType == MessageType.Type.REQUEST) {
                Handler handler = (Handler) parameters.get(arguments[arguments.length - 1].getName());
                applicationContext.getVertx().eventBus().<String>request(address, _json, deliveryOptions, handler);
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
