package cn.olange.vboot.verticle;

import cn.olange.vboot.controller.TestController;
import io.vertx.core.AbstractVerticle;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DemoVerticle extends AbstractVerticle {
    @Inject
    private TestController testController;
    @Override
    public void start() throws Exception {
        System.out.println("start");
        testController.hello();
    }
}
