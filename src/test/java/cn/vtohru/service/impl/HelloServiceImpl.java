package cn.vtohru.service.impl;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.service.HelloService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@Verticle
public class HelloServiceImpl implements HelloService {
    @Override
    public void say(String word, Handler<AsyncResult<String>> handler) {
        System.out.println("get:" + word);
        handler.handle(Future.succeededFuture("yes"));
    }
}
