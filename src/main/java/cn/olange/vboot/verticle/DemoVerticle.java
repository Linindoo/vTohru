package cn.olange.vboot.verticle;

import io.vertx.core.AbstractVerticle;

import javax.inject.Singleton;

@Singleton
public class DemoVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        System.out.println("start");
    }
}
