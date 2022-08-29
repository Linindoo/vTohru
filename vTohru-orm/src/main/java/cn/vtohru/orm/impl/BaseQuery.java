package cn.vtohru.orm.impl;

import cn.vtohru.orm.AbstractQuery;
import cn.vtohru.orm.JpqlBuilder;
import cn.vtohru.orm.Query;
import cn.vtohru.orm.entity.EntityInfo;
import cn.vtohru.orm.entity.EntityManager;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseQuery extends AbstractQuery {
    protected IDataProxy dataProxy;
    protected EntityManager entityManager;
    protected JpqlBuilder jpqlBuilder;
    protected Class entityClass;
    private boolean useColumn = false;

    public BaseQuery(IDataProxy dataProxy, EntityManager entityManager, JpqlBuilder jpqlBuilder) {
        this.dataProxy = dataProxy;
        this.entityManager = entityManager;
        this.jpqlBuilder = jpqlBuilder;
    }

    public enum ConditionType {
        EQ,NOT_EQ,LE,LT,GE,GT,LIKE
    }

    @Override
    public Query from(Class entityClass) {
        this.entityClass = entityClass;
        EntityInfo entity = entityManager.getEntity(entityClass);
        this.jpqlBuilder.setTable(entity.getTableName());
        return this;
    }

    @Override
    public Future<?> first() {
        Promise<Object> promise = Promise.promise();
        checkColumns();
        String jpql = this.jpqlBuilder.toJpql();
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(jpql + " limit 1", jpqlBuilder.getParams()).onSuccess(x -> {
                if (x.size() > 0) {
                    JsonObject row = x.getJsonObject(0);
                    Object entity = entityManager.convertEntity(row, entityClass);
                    promise.complete(entity);
                } else {
                    promise.complete();
                }
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<List<?>> all() {
        checkColumns();
        Promise<List<?>> promise = Promise.promise();
        String jpql = this.jpqlBuilder.toJpql();
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(jpql, jpqlBuilder.getParams()).onSuccess(x -> {
                if (x.size() > 0) {
                    List<Object> results = new ArrayList<>();
                    for (int i = 0; i < x.size(); i++) {
                        JsonObject row = x.getJsonObject(i);
                        Object entity = entityManager.convertEntity(row, entityClass);
                        results.add(entity);
                    }
                    promise.complete(results);
                } else {
                    promise.complete();
                }
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<PageData> pagination(int offset, int rowCount) {
        checkColumns();
        Promise<PageData> promise = Promise.promise();
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(this.jpqlBuilder.toCountJpql(), this.jpqlBuilder.getParams()).onSuccess(x -> {
                PageData<?> pageData = new PageData<>();
                if (x.size() > 0) {
                    JsonObject row = x.getJsonObject(0);
                    Long count = row.getLong("count");
                    pageData.setTotal(count);
                    if (count > 0) {
                        session.execute(this.jpqlBuilder.toJpql() + " limit " + offset + "," + rowCount, this.jpqlBuilder.getParams()).onSuccess(y -> {
                            List results = new ArrayList<>();
                            for (int i = 0; i < y.size(); i++) {
                                JsonObject record = y.getJsonObject(i);
                                Object entity = entityManager.convertEntity(record, entityClass);
                                results.add(entity);
                            }
                            pageData.setRecords(results);
                            promise.complete(pageData);

                        }).onFailure(promise::fail);
                    }
                } else {
                    pageData.setRecords(new ArrayList<>());
                    promise.complete(pageData);
                }
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public String getSegment() {
        return this.jpqlBuilder.toSegment();
    }

    @Override
    public List<Object> getParams() {
        return this.jpqlBuilder.getParams();
    }

    @Override
    public Query select(String... params) {
        this.jpqlBuilder.select(params);
        this.useColumn = true;
        return this;
    }

    private void checkColumns() {
        if (!this.useColumn) {
            EntityInfo entity = entityManager.getEntity(this.entityClass);
            String feilds = entity.getFieldMap().entrySet().stream().map(x -> x.getValue().getFieldName()).collect(Collectors.joining());
            this.jpqlBuilder.select(feilds);
        }
    }
}
