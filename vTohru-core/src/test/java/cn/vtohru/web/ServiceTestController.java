package cn.vtohru.web;

import cn.vtohru.model.SimpleModel;
import cn.vtohru.web.annotation.Controller;
import cn.vtohru.service.HelloService;
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
    @Priv(login = true)
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

    @GET
    @Path("/good")
    public Future<String> goodHello() {
        Promise<String> promise = Promise.promise();
        SimpleModel simpleModel = new SimpleModel();
        simpleModel.setName("jdsiofsofd");
        helloService.hello("zhansdfd", simpleModel).onComplete(x -> {
            if (x.succeeded()) {
                promise.complete(x.result());
            } else {
                promise.fail(x.cause().getMessage());
            }
        });
        return promise.future();
    }
}
