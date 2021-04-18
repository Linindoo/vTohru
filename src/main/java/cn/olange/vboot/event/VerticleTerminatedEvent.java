package cn.olange.vboot.event;

import cn.olange.vboot.annotation.Verticle;
import io.micronaut.context.event.ApplicationEvent;
import io.micronaut.core.annotation.NonNull;

public class VerticleTerminatedEvent extends ApplicationEvent {

    public VerticleTerminatedEvent(@NonNull Verticle source) {
        super(source);
    }
    @Override
    @NonNull
    public Verticle getSource() {
        return (Verticle) super.getSource();
    }
}
