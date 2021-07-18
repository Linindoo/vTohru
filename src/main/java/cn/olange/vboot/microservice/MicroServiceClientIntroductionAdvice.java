package cn.olange.vboot.microservice;

import cn.olange.vboot.context.VerticleApplicationContext;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

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
            Argument[] arguments = context.getExecutableMethod().getArguments();
            if (arguments.length == 0) {
                throw new IllegalStateException("Invalid method" + context.getMethodName());
            }
            Map parameters = context.getParameterValueMap();
            Object handlerObj = parameters.get(arguments[arguments.length - 1].getName());
            if (handlerObj instanceof Handler) {
                JsonObject _json = new JsonObject();
                if (arguments.length > 1) {
                    for (int i = 0; i < arguments.length - 1; i++) {
                        _json.put(arguments[i].getName(), parameters.get(arguments[i].getName()));
                    }
                }
                Handler handler = (Handler) handlerObj;
                DeliveryOptions _deliveryOptions =  new DeliveryOptions();
                _deliveryOptions.addHeader("action", context.getMethodName());
                applicationContext.getVertx().eventBus().<String>request(context.getTarget().getClass().getName(), _json, _deliveryOptions, handler);
                return null;
            } else {
                throw new IllegalStateException("method last params must be handler");
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
