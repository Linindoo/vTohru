package cn.vtohru.service.impl;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.model.SimpleModel;
import cn.vtohru.service.HelloService;
import io.micronaut.context.ApplicationContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;

@Verticle
public class HelloServiceImpl implements HelloService {

    private VerticleApplicationContext context;

    public HelloServiceImpl(ApplicationContext context) {
        this.context = (VerticleApplicationContext) context;
    }

    @Override
    public void say(String word, Handler<AsyncResult<String>> handler) {
        System.out.println("get:" + word);
        handler.handle(Future.succeededFuture("yes"));
    }

    @Override
    public Future<String> hello(String name, SimpleModel simpleModel) {
        Promise<String> promise = Promise.promise();
        System.out.println("hello:" + name);
        promise.complete("bye");
        return promise.future();
    }

}
