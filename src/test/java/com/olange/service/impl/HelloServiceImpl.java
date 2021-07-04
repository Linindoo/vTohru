package com.olange.service.impl;

import cn.olange.vboot.annotation.Verticle;
import com.olange.service.HelloService;
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
