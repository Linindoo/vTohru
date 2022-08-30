package cn.vtohru.orm;

import io.vertx.core.Future;

@FunctionalInterface
public interface TransactionFunction<T> {
     Future<T> onTransaction(ClientSession clientSession);
}
