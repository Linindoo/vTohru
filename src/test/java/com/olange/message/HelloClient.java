package com.olange.message;

import cn.olange.vboot.message.MessageAddress;
import cn.olange.vboot.message.MessageClient;
import cn.olange.vboot.message.MessageType;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@MessageClient
public interface HelloClient {
    @MessageAddress(value = "hello", type = MessageType.Type.REQUEST)
    void hello(String name, Handler<AsyncResult<String>> handler);

    @MessageAddress(value = "bye")
    void bye(String msg);

    @MessageAddress(value = "goodmorning")
    void goodMorning(String msg);
}
