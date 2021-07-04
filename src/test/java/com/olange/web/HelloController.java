package com.olange.web;

import cn.olange.vboot.annotation.Controller;
import com.olange.service.HelloService;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Controller
@Path("/hello")
public class HelloController {

    private HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GET
    @Path("/")
    public Future<String> hello() {
        Promise<String> promise = Promise.promise();
        helloService.say("hello", x -> {
            promise.complete("pix");
        });
        return promise.future();
    }
}
