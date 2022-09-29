package cn.vtohru.mongo.impl;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.*;
import cn.vtohru.orm.data.IDataProxy;
import cn.vtohru.orm.entity.EntityManager;
import cn.vtohru.orm.exception.OrmException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoDataStore implements DataStore {
    private VerticleApplicationContext verticleApplicationContext;
    private DataSourceConfiguration dataSourceConfiguration;
    private EntityManager entityManager;
    private MongoClient mongoClient;

    public MongoDataStore(VerticleApplicationContext verticleApplicationContext, DataSourceConfiguration dataSourceConfiguration, EntityManager entityManager) {
        this.verticleApplicationContext = verticleApplicationContext;
        this.dataSourceConfiguration = dataSourceConfiguration;
        this.entityManager = entityManager;
    }

    @Override
    public Future<Void> start() {
        JsonObject config = new JsonObject();
        config.put("connection_string", dataSourceConfiguration.getUrl());
        mongoClient = MongoClient.create(verticleApplicationContext.getVertx(), config);
        return Future.succeededFuture();
    }

    @Override
    public Future<Void> stop() {
        return mongoClient.close();
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
        MongoQuery<T> mongoQuery = new MongoQuery<>(new IDataProxy(this, null), entityManager);
        return mongoQuery.from(clazz);
    }


    @Override
    public Future<DbSession> getSession() {
        return Future.succeededFuture(new MongoSession(mongoClient, entityManager));
    }
}
