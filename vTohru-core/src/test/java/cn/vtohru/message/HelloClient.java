package cn.vtohru.message;

import cn.vtohru.message.annotation.MessageAddress;
import cn.vtohru.message.annotation.MessageClient;
import cn.vtohru.message.annotation.MessageType;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

@MessageClient
public interface HelloClient {
    @MessageAddress(value = "hello", type = MessageType.Type.REQUEST)
    void hello(String name, Handler<AsyncResult<String>> handler);

    @MessageAddress(value = "bye")
    void bye(String msg);

    @MessageAddress(value = "goodmorning")
    void goodMorning(String msg);

    @MessageAddress(value = "good", type = MessageType.Type.REQUEST)
    Future<String> luck(String name, JsonObject data);
}
