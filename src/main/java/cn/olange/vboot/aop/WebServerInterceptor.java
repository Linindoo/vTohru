package cn.olange.vboot.aop;

import cn.olange.vboot.annotation.WebServer;
import cn.olange.vboot.context.VerticleApplicationContext;
import cn.olange.vboot.router.VerticleAnnotatedMethodRouteBuilder;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Singleton;
import java.lang.reflect.Method;

@Singleton
@InterceptorBean(WebServer.class)
public class WebServerInterceptor implements MethodInterceptor<Object, Object> {
    private static final Logger logger = LoggerFactory.getLogger(WebServerInterceptor.class);
    private VerticleApplicationContext applicationContext;
    private VerticleAnnotatedMethodRouteBuilder builder;

    public WebServerInterceptor(ApplicationContext context, VerticleAnnotatedMethodRouteBuilder builder) {
        this.applicationContext = (VerticleApplicationContext) context;
        this.builder = builder;
    }

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        ExecutableMethod executableMethod = context.getExecutableMethod();
        Method targetMethod = executableMethod.getTargetMethod();
        if ("start".equalsIgnoreCase(targetMethod.getName()) && targetMethod.getParameterCount() == 1) {
            Object verticle = context.getTarget();
            BeanDefinition<?> beanDefinition = applicationContext.getBeanDefinition(verticle.getClass());
            AnnotationValue<WebServer> annotation = beanDefinition.getDeclaredAnnotation(WebServer.class);
            int port = annotation.intValue("port").orElse(9099);
            String host = annotation.stringValue("host").orElse("0.0.0.0");
            applicationContext.getVertx().createHttpServer().requestHandler(builder.getRouter()).listen(port, host).onSuccess(x -> {
                logger.info("start http server success");
            }).onFailure(logger::error);

        }
        return context.proceed();
    }

}
