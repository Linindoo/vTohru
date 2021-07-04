package com.olange.service;

import cn.olange.vboot.microservice.Service;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@Service
public interface HelloService {
    void say(String word, Handler<AsyncResult<String>> handler);
}
