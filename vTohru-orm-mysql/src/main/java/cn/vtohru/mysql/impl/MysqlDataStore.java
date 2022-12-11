package cn.vtohru.mysql.impl;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.*;
import cn.vtohru.orm.entity.EntityManager;
import cn.vtohru.orm.data.IDataProxy;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.List;
import java.util.Map;

public class MysqlDataStore implements DataStore {
    private MySQLPool  sqlClient;
    private VerticleApplicationContext verticleApplicationContext;
    private DataSourceConfiguration dataSourceConfiguration;
    private EntityManager entityManager;

    public MysqlDataStore(VerticleApplicationContext verticleApplicationContext, DataSourceConfiguration dataSourceConfiguration, EntityManager entityManager) {
        this.verticleApplicationContext = verticleApplicationContext;
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.entityManager = entityManager;
    }

    @Override
    public Future<Void> start() {
        MySQLConnectOptions mySQLConnectOptions = MySQLConnectOptions.fromUri(dataSourceConfiguration.getUrl());
        Map<String, Object> pool = this.dataSourceConfiguration.getPool();
        JsonObject poolConfig = new JsonObject(pool);
        PoolOptions options = new PoolOptions(poolConfig);
        sqlClient = MySQLPool.pool(this.verticleApplicationContext.getVertx(), mySQLConnectOptions, options);
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stop() {
        return sqlClient.close();
    }

    @Override
    public <T> Future<T> persist(T model) {
        Promise<T> promise = Promise.promise();
        getSession().onSuccess(x -> x.persist(model).onComplete(promise)).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<T> insert(T model) {
        Promise<T> promise = Promise.promise();
        getSession().onSuccess(x -> x.insert(model).onComplete(promise)).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<Long> insertBatch(List<T> model) {
        Promise<Long> promise = Promise.promise();
        getSession().onSuccess(x -> x.insertBatch(model).onComplete(promise)).onFailure(promise::fail);
        return promise.future();    }

    @Override
    public <T> Future<T> update(T model) {
        Promise<T> promise = Promise.promise();
        getSession().onSuccess(x -> x.update(model).onComplete(promise)).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<Void> remove(T model) {
        Promise<Void> promise = Promise.promise();
        getSession().onSuccess(x -> x.remove(model).onComplete(promise)).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<T> fetch(T model) {
        Promise<T> promise = Promise.promise();
        getSession().onSuccess(x -> x.fetch(model).onComplete(promise)).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Query<T> build(Class<T> clazz) {
        MysqlQuery mysqlQuery = new MysqlQuery(new IDataProxy(this, null), this.entityManager);
        return mysqlQuery.from(clazz);
    }

    @Override
    public Future<DbSession> getSession() {
        Promise<DbSession> promise = Promise.promise();
        sqlClient.getConnection().onSuccess(x -> promise.complete(new MysqlSession(x, entityManager))).onFailure(promise::fail);
        return promise.future();
    }

}
