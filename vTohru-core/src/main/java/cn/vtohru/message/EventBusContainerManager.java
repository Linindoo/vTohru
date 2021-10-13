package cn.vtohru.message;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.message.annotation.MessageAutoConfigure;
import cn.vtohru.VerticleEvent;
import cn.vtohru.annotation.Verticle;
import io.micronaut.inject.BeanDefinition;
import io.vertx.core.Future;

@Verticle
@GlobalScope
public class EventBusContainerManager extends VerticleEvent {
    private EventBusMessageAnnotatedBuilder eventBusMessageAnnotatedBuilder;

    public EventBusContainerManager(EventBusMessageAnnotatedBuilder eventBusMessageAnnotatedBuilder) {
        this.eventBusMessageAnnotatedBuilder = eventBusMessageAnnotatedBuilder;
    }

    @Override
    public Future<Void> start(BeanDefinition<?> beanDefinition) {
        if (beanDefinition.hasAnnotation(MessageAutoConfigure.class)) {
            eventBusMessageAnnotatedBuilder.register();
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stop(BeanDefinition<?> beanDefinition) {
        eventBusMessageAnnotatedBuilder.unregister();
        return Future.succeededFuture();
    }
}
