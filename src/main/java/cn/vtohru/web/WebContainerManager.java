package cn.vtohru.web;

import cn.vtohru.VerticleEvent;
import cn.vtohru.web.annotation.WebAutoConfigure;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class WebContainerManager extends VerticleEvent {
    private static final Logger logger = LoggerFactory.getLogger(WebContainerManager.class);
    private VerticleApplicationContext applicationContext;
    private VerticleAnnotatedMethodRouteBuilder builder;
    private HttpServer httpServer;

    public WebContainerManager(ApplicationContext applicationContext,VerticleAnnotatedMethodRouteBuilder builder) {
        this.applicationContext = (VerticleApplicationContext) applicationContext;
        this.builder = builder;
    }

    @Override
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        AnnotationValue<WebAutoConfigure> annotation = beanDefinition.getDeclaredAnnotation(WebAutoConfigure.class);
        if (annotation == null) {
            return Future.succeededFuture();
        }
        int port = annotation.intValue("port").orElse(9099);
        String host = annotation.stringValue("host").orElse("0.0.0.0");
        return applicationContext.getVertx().createHttpServer().requestHandler(builder.getRouter()).listen(port, host).compose(x -> {
            this.httpServer = x;
            logger.info("start http server success");
            return Future.succeededFuture();
        }, e -> {
            logger.error(e);
            return Future.failedFuture(e);
        });
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        return null;
    }
}
