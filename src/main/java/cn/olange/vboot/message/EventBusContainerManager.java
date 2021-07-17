package cn.olange.vboot.message;

import cn.olange.vboot.VerticleEvent;
import cn.olange.vboot.annotation.Verticle;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;

@Verticle
public class EventBusContainerManager extends VerticleEvent {
    private EventBusMessageAnnotatedBuilder eventBusMessageAnnotatedBuilder;

    public EventBusContainerManager(EventBusMessageAnnotatedBuilder eventBusMessageAnnotatedBuilder) {
        this.eventBusMessageAnnotatedBuilder = eventBusMessageAnnotatedBuilder;
    }

    @Override
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        eventBusMessageAnnotatedBuilder.register();
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        eventBusMessageAnnotatedBuilder.unregister();
        return Future.succeededFuture();
    }
}
