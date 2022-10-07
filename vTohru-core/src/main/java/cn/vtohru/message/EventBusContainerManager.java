package cn.vtohru.message;

import cn.vtohru.VerticleEvent;
import cn.vtohru.message.annotation.MessageAutoConfigure;
import cn.vtohru.plugin.VerticleInfo;
import io.micronaut.core.annotation.Indexed;
import io.vertx.core.Future;

import javax.inject.Singleton;

@Singleton
@Indexed(VerticleEvent.class)
public class EventBusContainerManager extends VerticleEvent {
    private EventBusMessageAnnotatedBuilder eventBusMessageAnnotatedBuilder;

    public EventBusContainerManager(EventBusMessageAnnotatedBuilder eventBusMessageAnnotatedBuilder) {
        this.eventBusMessageAnnotatedBuilder = eventBusMessageAnnotatedBuilder;
    }

    @Override
    public Future<Void> start(VerticleInfo verticleInfo) {
        if (verticleInfo.getType().getAnnotation(MessageAutoConfigure.class) != null) {
            eventBusMessageAnnotatedBuilder.register();
        }
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stop(VerticleInfo beanDefinition) {
        eventBusMessageAnnotatedBuilder.unregister();
        return Future.succeededFuture();
    }
}
