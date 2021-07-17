package com.olange.message;

import cn.olange.vboot.message.MessageAddress;
import cn.olange.vboot.message.MessageClient;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@MessageClient
public interface HelloClient {
    @MessageAddress("hello")
    void hello(String name, Handler<AsyncResult<String>> handler);
}
