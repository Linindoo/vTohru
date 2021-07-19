package cn.vtohru.service;

import cn.vtohru.microservice.annotation.Service;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@Service
public interface HelloService {
    void say(String word, Handler<AsyncResult<String>> handler);
}
