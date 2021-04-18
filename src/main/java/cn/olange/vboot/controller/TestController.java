package cn.olange.vboot.controller;

import cn.olange.vboot.annotation.Verticle;
import cn.olange.vboot.verticle.DemoVerticle;


@Verticle(bind = DemoVerticle.class)
public class TestController {

    public void hello() {
        System.out.println("hello");
    }
}
