package cn.vtohru.microservice;

import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.HelperUtils;
import io.vertx.serviceproxy.ProxyHandler;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;

public class ServiceProxyHandler<T> extends ProxyHandler {
    private BeanDefinition<T> beanDefinition;
    public static final long DEFAULT_CONNECTION_TIMEOUT = 5 * 60; // 5 minutes
    private VerticleApplicationContext context;
    private final long timerID;
    private long lastAccessed;
    private final long timeoutSeconds;
    private final boolean includeDebugInfo;

    public ServiceProxyHandler(VerticleApplicationContext context, BeanDefinition<T> beanDefinition){
        this(context, beanDefinition, DEFAULT_CONNECTION_TIMEOUT);
    }

    public ServiceProxyHandler(VerticleApplicationContext context, BeanDefinition<T>  beanDefinition, long timeoutInSecond){
        this(context, beanDefinition, true, timeoutInSecond);
    }

    public ServiceProxyHandler(VerticleApplicationContext context, BeanDefinition<T> beanDefinition, boolean topLevel, long timeoutInSecond){
        this(context, beanDefinition, true, timeoutInSecond, false);
    }

    public ServiceProxyHandler(VerticleApplicationContext context, BeanDefinition<T>  beanDefinition, boolean topLevel, long timeoutSeconds, boolean includeDebugInfo) {
        this.context = context;
        this.beanDefinition = beanDefinition;
        this.includeDebugInfo = includeDebugInfo;
        this.timeoutSeconds = timeoutSeconds;
        try {
            this.context.getVertx().eventBus().registerDefaultCodec(ServiceException.class,
                    new ServiceExceptionMessageCodec());
        } catch (IllegalStateException ex) {}
        if (timeoutSeconds != -1 && !topLevel) {
            long period = timeoutSeconds * 1000 / 2;
            if (period > 10000) {
                period = 10000;
            }
            this.timerID = this.context.getVertx().setPeriodic(period, this::checkTimedOut);
        } else {
            this.timerID = -1;
        }
        accessed();
    }


    private void checkTimedOut(long id) {
        long now = System.nanoTime();
        if (now - lastAccessed > timeoutSeconds * 1000000000) {
            close();
        }
    }

    @Override
    public void close() {
        if (timerID != -1) {
            this.context.getVertx().cancelTimer(timerID);
        }
        super.close();
    }

    private void accessed() {
        this.lastAccessed = System.nanoTime();
    }

    public void handle(Message<JsonObject> msg) {
        try{
            String action = msg.headers().get("action");
            if (action == null) throw new IllegalStateException("action not specified");
            accessed();
            ExecutableMethod<T, Object> executableMethod = context.getExecutableMethod(beanDefinition.getBeanType(), action);
            if (executableMethod == null) {
                throw new IllegalStateException("Invalid action: " + action);
            }
            JsonObject body = msg.body();
            Argument[] arguments = executableMethod.getArguments();
            Object[] params = new Object[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                Argument argument = arguments[i];
                if (argument.getType().isAssignableFrom(Handler.class)) {
                    params[i] = HelperUtils.createHandler(msg, this.includeDebugInfo);
                } else {
                    params[i] = body.getMap().get(argument.getName());
                }
            }
            executableMethod.invoke(context.getBean(beanDefinition), params);
        } catch (Throwable t) {
            if (includeDebugInfo) msg.reply(new ServiceException(500, t.getMessage(), HelperUtils.generateDebugInfo(t)));
            else msg.reply(new ServiceException(500, t.getMessage()));
        }
    }
}
