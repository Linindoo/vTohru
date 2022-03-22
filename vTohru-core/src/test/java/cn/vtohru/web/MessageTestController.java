package cn.vtohru.web;

import cn.vtohru.config.DemoConfiguretion;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.message.HelloClient;
import cn.vtohru.web.annotation.Controller;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Controller
@Path("/message")
public class MessageTestController {
    @Value("${vtohru.hello}")
    private String name;
    @Property(name = "vtohru.bye")
    private String property;
    private HelloClient helloClient;
    private VerticleApplicationContext context;
    @Inject
    private DemoConfiguretion demoConfiguretion;

    public MessageTestController(HelloClient helloClient, ApplicationContext context) {
        this.helloClient = helloClient;
        this.context = (VerticleApplicationContext) context;
    }

    @GET
    @Path("/")
    public Future<String> hello() {
        Promise<String> promise = Promise.promise();
        helloClient.hello("olange", x -> {
            if (x.succeeded()) {
                promise.complete(x.result() + "：" + name);
            } else {
                promise.fail(x.cause());
            }
        });
        return promise.future();
    }

    @GET
    @Path("/luck")
    public Future<String> luck() {
        Promise<String> promise = Promise.promise();
        JsonObject data = new JsonObject();
        data.put("name", "zhanfgsan");
        Future<String> future = helloClient.luck("wang", data);
        future.onComplete(x -> {
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
        promise.complete("bye：" + name);
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
