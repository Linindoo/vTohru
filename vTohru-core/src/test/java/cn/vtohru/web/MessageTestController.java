package cn.vtohru.web;

import cn.vtohru.message.HelloClient;
import cn.vtohru.web.annotation.Controller;
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
        System.out.println(this.toString());
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
        System.out.println(this.toString());
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
