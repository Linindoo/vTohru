package cn.vtohru.microservice;

import cn.vtohru.VerticleEvent;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.microservice.annotation.ServiceAutoConfigure;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;


@Verticle
public class ServiceContainerManager extends VerticleEvent {
    private ServiceAnnotatedBuilder serviceAnnotatedBuilder;
    private MicroServiceRegister serviceRegister;

    public ServiceContainerManager(ServiceAnnotatedBuilder serviceAnnotatedBuilder, MicroServiceRegister serviceRegister) {
        this.serviceAnnotatedBuilder = serviceAnnotatedBuilder;
        this.serviceRegister = serviceRegister;
    }

    @Override
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        if (beanDefinition.hasAnnotation(ServiceAutoConfigure.class)) {
            serviceAnnotatedBuilder.registerService();
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        return null;
    }
}
