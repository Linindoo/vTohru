package cn.olange.vboot.controller;

import cn.olange.vboot.annotation.Verticle;


@Verticle
public class TestController {
    public TestController() {
    }

    public void hello() {
        System.out.println("hello");
    }
}
