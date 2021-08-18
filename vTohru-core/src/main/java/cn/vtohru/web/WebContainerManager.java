package cn.vtohru.web;

import cn.vtohru.VerticleEvent;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.web.annotation.WebAutoConfigure;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

@Verticle
public class WebContainerManager extends VerticleEvent {
    private static final Logger logger = LoggerFactory.getLogger(WebContainerManager.class);
    private VerticleApplicationContext applicationContext;

    public WebContainerManager(VerticleApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        AnnotationValue<WebAutoConfigure> annotation = beanDefinition.getDeclaredAnnotation(WebAutoConfigure.class);
        if (annotation == null) {
            return Future.succeededFuture();
        }
        int port = annotation.intValue("port").orElse(9099);
        String host = annotation.stringValue("host").orElse("0.0.0.0");
        VerticleRouterHandler routerHandler = applicationContext.getBean(VerticleRouterHandler.class);
        return routerHandler.registerRouter(host, port);
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
//        VerticleRouterHandler routerHandler = applicationContext.getBean(VerticleRouterHandler.class);
        return Future.succeededFuture();
    }
}
