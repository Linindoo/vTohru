package cn.olange.vboot.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;


@Controller
public class TestController {

    @Get("/hello")
    public String hello() {
        return "world";
    }
}
