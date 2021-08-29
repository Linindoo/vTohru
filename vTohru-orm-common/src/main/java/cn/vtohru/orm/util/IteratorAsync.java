package cn.vtohru.orm.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface  IteratorAsync<E> {
    boolean hasNext();

    void next(Handler<AsyncResult<E>> handler);
}
