package cn.olange.vboot.microservice;

import cn.olange.vboot.VerticleEvent;
import cn.olange.vboot.annotation.Verticle;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;

import java.util.List;
import java.util.Map;

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
        Map<Class<?>, List<BeanDefinition<?>>> routerMap = serviceAnnotatedBuilder.getRouterMap();
        if (routerMap != null) {
            for (Map.Entry<Class<?>, List<BeanDefinition<?>>> entry : routerMap.entrySet()) {
                for (BeanDefinition<?> definition : entry.getValue()) {
                    this.serviceRegister.registerService(definition);
                }
            }
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        return null;
    }
}
