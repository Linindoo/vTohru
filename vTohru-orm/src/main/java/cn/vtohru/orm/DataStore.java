package cn.vtohru.orm;


import cn.vtohru.orm.exception.OrmException;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public interface DataStore extends ISession{

    Future<Void> start();

    Future<Void> stop();

    <T> Future<T> persist(T model);

    <T> Future<T> insert(T model);

    <T> Future<T> update(T model);

    <T> Future<Void> remove(T model);

    <T> Future<T> fetch(T model);

    <T> Query<T> build(Class<T> clazz);

    default <T> Future<T> onTransaction(TransactionFunction<T> transaction) {
        Promise<T> promise = Promise.promise();
        getSession().onSuccess(x -> {
            x.beginTransaction().onSuccess(y -> {
                transaction.onTransaction(x).onSuccess(t -> {
                    y.commit().onSuccess(c->{
                        promise.complete(t);
                    }).onFailure(promise::fail);
                }).onFailure(e -> {
                    y.rollback().onSuccess(c->{
                        promise.fail(new OrmException("roll back"));
                    }).onFailure(promise::fail);
                });
            }).onFailure(promise::fail);

        });
        return promise.future();
    }

}
