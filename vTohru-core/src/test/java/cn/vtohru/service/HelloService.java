package cn.vtohru.service;

import cn.vtohru.microservice.annotation.Service;
import cn.vtohru.model.SimpleModel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@Service
public interface HelloService {
    void say(String word, Handler<AsyncResult<String>> handler);

    Future<String> hello(String name, SimpleModel simpleModel);
}
