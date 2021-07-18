package com.olange.web;

import cn.olange.vboot.annotation.Controller;
import com.olange.message.HelloClient;
import com.olange.service.HelloService;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Controller
@Path("/hello")
public class HelloController {
    private HelloClient helloClient;

    private HelloService helloService;

    public HelloController(HelloService helloService, HelloClient helloClient) {
        this.helloService = helloService;
        this.helloClient = helloClient;
    }

    @GET
    @Path("/")
    public Future<String> hello() {
        Promise<String> promise = Promise.promise();
        helloClient.hello("olange", x -> {
            if (x.succeeded()) {
                promise.complete(x.result());
            } else {
                promise.fail(x.cause());
            }
        });
        return promise.future();
    }

    @GET
    @Path("/bye")
    public Future<String> bye() {
        Promise<String> promise = Promise.promise();
        helloClient.bye("olange");
        promise.complete("bye");
        return promise.future();
    }

    @GET
    @Path("/morning")
    public Future<String> morning() {
        Promise<String> promise = Promise.promise();
        helloClient.goodMorning("olange");
        promise.complete("morning");
        return promise.future();
    }
}
