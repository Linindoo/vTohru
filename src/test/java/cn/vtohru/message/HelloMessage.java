package cn.vtohru.message;

import cn.vtohru.message.annotation.MessageAddress;
import cn.vtohru.message.annotation.MessageListener;
import cn.vtohru.message.annotation.MessageType;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import javax.ws.rs.QueryParam;

@MessageListener
public class HelloMessage {

    @MessageAddress(value = "hello",type = MessageType.Type.REQUEST)
    public void hello(String name, Handler<AsyncResult<String>> handler) {
        System.out.println("recieve:" + name);
        handler.handle(Future.succeededFuture("hello:" + name));
    }

    @MessageAddress("bye")
    public void bye(@QueryParam("msg") String name) {
        System.out.println("recieve:" + name);
    }

    @MessageAddress(value = "goodmorning",type = MessageType.Type.P2P)
    public void goodMorning(@QueryParam("msg") String name) {
        System.out.println("morning:" + name);
    }
}
