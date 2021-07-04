package cn.olange.vboot.context;

import cn.olange.vboot.runtime.VertBoot;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextConfiguration;
import io.micronaut.context.DefaultApplicationContext;
import io.micronaut.core.annotation.NonNull;
import io.vertx.core.Vertx;

public class VerticleApplicationContext extends DefaultApplicationContext {

    private Vertx vertx;


    public VerticleApplicationContext(ApplicationContextConfiguration configuration) {
        super(configuration);
        this.vertx = Vertx.vertx();
    }


    @Override
    public synchronized VerticleApplicationContext start() {
        super.start();
        return this;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }


    @NonNull
    public static VerticleApplicationContext run() {
        return new VertBoot().start();
    }

}
