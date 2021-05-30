package cn.olange.vboot.controller;

import cn.olange.vboot.annotation.Controller;
import cn.olange.vboot.demo.ServiceClient;
import cn.olange.vboot.microservice.Client;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Controller
public class TestController {


    @GET
    @Path("/hello")
    public String hello() {
        return "word";
    }
}
