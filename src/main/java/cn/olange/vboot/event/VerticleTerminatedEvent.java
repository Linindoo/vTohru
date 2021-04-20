package cn.olange.vboot.event;

import io.micronaut.context.event.ApplicationEvent;
import io.micronaut.core.annotation.NonNull;
import io.vertx.core.Context;

public class VerticleTerminatedEvent extends ApplicationEvent {

    public VerticleTerminatedEvent(@NonNull Context source) {
        super(source);
    }
    @Override
    @NonNull
    public Context getSource() {
        return (Context) super.getSource();
    }
}
