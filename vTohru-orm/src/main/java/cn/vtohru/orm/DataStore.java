package cn.vtohru.orm;


import io.vertx.core.Future;

public interface DataStore extends ISession{

    Future<Void> start();

    Future<Void> stop();

    <T> Future<T> persist(T model);

    <T> Future<T> insert(T model);

    <T> Future<T> update(T model);

    <T> Future<Void> remove(T model);

    <T> Future<T> fetch(T model);

    <T> Query<T> build(Class<T> clazz);

    <T> Future<T> onTransaction(TransactionFunction<T> transaction);

}
