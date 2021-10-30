package cn.vtohru.task.runtime;

import cn.vtohru.task.annotation.Delay;
import cn.vtohru.task.annotation.Periodic;
import cn.vtohru.task.annotation.Task;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import javax.ws.rs.core.Context;

@Task
public class TestTask {

    @Delay(delay = 1000)
    public void task1() {
        System.out.println("task1");
    }

    @Periodic(delay = 1000)
    public Future<Void> task2(@Context long taskID, @Context Vertx vertx) {
        System.out.println("task2:" + taskID);
        return Future.succeededFuture();
    }
}
