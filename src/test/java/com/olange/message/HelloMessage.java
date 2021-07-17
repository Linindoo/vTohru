package com.olange.message;

import cn.olange.vboot.message.MessageAddress;
import cn.olange.vboot.message.MessageListener;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@MessageListener
public class HelloMessage {
    @MessageAddress("hello")
    public void hello(String name, Handler<AsyncResult<String>> handler) {
        System.out.println("recieve:" + name);
        handler.handle(Future.succeededFuture("hello:" + name));
    }

    @MessageAddress("bye")
    public void bye(String name, Handler<AsyncResult<String>> handler) {
        System.out.println("recieve:" + name);
        handler.handle(Future.succeededFuture("bye:" + name));
    }
}
