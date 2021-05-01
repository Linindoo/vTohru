package cn.olange.vboot.controller;

import cn.olange.vboot.annotation.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Controller
public class TestController {

    @GET
    @Path("/hello")
    public String hello() {
        return "world";
    }
}
