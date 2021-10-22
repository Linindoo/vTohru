package cn.vtohru.microservice;

import cn.vtohru.VerticleEvent;
import cn.vtohru.microservice.annotation.ServiceAutoConfigure;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Indexed;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;

import javax.inject.Singleton;


@Singleton
@Indexed(VerticleEvent.class)
public class ServiceContainerManager extends VerticleEvent {
    private ApplicationContext context;
    public ServiceContainerManager(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        ServiceAnnotatedBuilder serviceAnnotatedBuilder = context.getBean(ServiceAnnotatedBuilder.class);
        if (beanDefinition.hasAnnotation(ServiceAutoConfigure.class)) {
            serviceAnnotatedBuilder.registerService();
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        return Future.succeededFuture();
    }
}
