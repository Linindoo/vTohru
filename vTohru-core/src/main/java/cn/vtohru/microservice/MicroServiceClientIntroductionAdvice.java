package cn.vtohru.microservice;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.microservice.annotation.Service;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.impl.JsonUtil;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
@InterceptorBean(Service.class)
public class MicroServiceClientIntroductionAdvice implements MethodInterceptor<Object, Object>, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(MicroServiceClientIntroductionAdvice.class);
    private MicroServiceDiscovery serviceDiscovery;
    private VerticleApplicationContext applicationContext;

    public MicroServiceClientIntroductionAdvice(MicroServiceDiscovery serviceDiscovery, ApplicationContext context) {
        this.serviceDiscovery = serviceDiscovery;
        this.applicationContext = (VerticleApplicationContext) context;
    }

    @Override
    public Object intercept(MethodInvocationContext context) {
        if (context.hasAnnotation(Service.class)) {
            ExecutableMethod executableMethod = context.getExecutableMethod();
            Argument[] arguments = executableMethod.getArguments();
            Map parameters = context.getParameterValueMap();
            JsonObject _json = new JsonObject();
            for (int i = 0; i < arguments.length; i++) {
                Argument argument = arguments[i];
                if (!argument.getType().isAssignableFrom(Handler.class)) {
                    _json.put(arguments[i].getName(), parameters.get(arguments[i].getName()));
                }
            }
            DeliveryOptions _deliveryOptions =  new DeliveryOptions();
            _deliveryOptions.addHeader("action", context.getMethodName());
            String address = executableMethod.getDeclaringType().getName();
            if (Future.class.isAssignableFrom(context.getReturnType().getType())) {
                Promise<Object> promise = Promise.promise();
                try {
                    applicationContext.getVertx().eventBus().request(address, _json, _deliveryOptions, x -> {
                        if (x.succeeded()) {
                            Message<?> result = x.result();
                            promise.complete(result.body());
                        } else {
                            promise.fail(x.cause());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return promise.future();
            } else if (arguments.length > 0) {
                Object lastParam = parameters.get(arguments[arguments.length - 1].getName());
                if (lastParam instanceof Handler) {
                    Handler handler = (Handler) lastParam;
                    applicationContext.getVertx().eventBus().request(address, _json, _deliveryOptions, x -> {
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
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
