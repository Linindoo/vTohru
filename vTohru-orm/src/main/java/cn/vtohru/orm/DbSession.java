package cn.vtohru.orm;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.List;

public interface DbSession {

    <T> Future<T> persist(T model);

    <T> Future<T> insert(T model);

    <T> Future<Long> insertBatch(List<T> model);

    <T> Future<T> update(T model);

    <T> Future<Void> remove(T model);

    <T> Future<T> fetch(T model);

    <T> Query<T> from(Class<T> clazz);

    Future<JsonArray> execute(String jpql, List<Object> params);

    Future<ITransaction> beginTransaction();

}
