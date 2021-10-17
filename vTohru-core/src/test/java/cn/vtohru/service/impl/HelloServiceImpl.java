package cn.vtohru.service.impl;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.microservice.AsyncService;
import cn.vtohru.service.HelloService;
import io.micronaut.context.ApplicationContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@Verticle
public class HelloServiceImpl implements HelloService {

    private VerticleApplicationContext context;

    public HelloServiceImpl(ApplicationContext context) {
        this.context = (VerticleApplicationContext) context;
    }

    @Override
    public void say(String word, Handler<AsyncResult<String>> handler) {
        System.out.println("get:" + word);
        AsyncService bean = context.getBean(AsyncService.class);
        handler.handle(Future.succeededFuture("yes"));
    }
}
