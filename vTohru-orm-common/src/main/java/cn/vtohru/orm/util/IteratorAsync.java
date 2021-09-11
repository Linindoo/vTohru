package cn.vtohru.orm.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public interface  IteratorAsync<E> {
    boolean hasNext();

    void next(Handler<AsyncResult<E>> handler);

    void result(Handler<AsyncResult<List<E>>> handler);
}
