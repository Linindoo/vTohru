package cn.olange.vboot.aop;

import cn.olange.vboot.annotation.WebServer;
import io.micronaut.aop.*;
import io.micronaut.context.ApplicationContext;
import io.vertx.core.AbstractVerticle;

import javax.inject.Singleton;

@Singleton
@InterceptorBean(WebServer.class)
public class WebServerInterceptor implements Interceptor {
    private ApplicationContext applicationContext;

    public WebServerInterceptor(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    public Object intercept(InvocationContext context) {
        AbstractVerticle verticle = (AbstractVerticle) context.getTarget();
        return context.proceed();
    }
}
