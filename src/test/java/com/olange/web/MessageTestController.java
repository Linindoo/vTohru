package com.olange.web;

import cn.olange.vboot.annotation.Controller;
import com.olange.message.HelloClient;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Controller
@Path("/message")
public class MessageTestController {
    private HelloClient helloClient;

    public MessageTestController(HelloClient helloClient) {
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
