package cn.vtohru.orm;

import io.vertx.core.Future;

public interface ITransaction {
    Future<Void> commit();

    Future<Void> rollback();
}
