package com.olange.web;

import cn.olange.vboot.annotation.Controller;
import com.olange.service.HelloService;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Controller
@Path("/service")
public class ServiceTestController {

    private HelloService helloService;

    public ServiceTestController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GET
    @Path("/hello")
    public Future<String> hello() {
        Promise<String> promise = Promise.promise();
        helloService.say("hello", x -> {
            if (x.succeeded()) {
                promise.complete(x.result());
            } else {
                promise.fail(x.cause().getMessage());
            }
        });
        return promise.future();
    }
}
