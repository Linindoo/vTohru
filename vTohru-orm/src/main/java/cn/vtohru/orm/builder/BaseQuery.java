package cn.vtohru.orm.builder;

import cn.vtohru.orm.Query;
import cn.vtohru.orm.entity.EntityField;
import cn.vtohru.orm.entity.EntityInfo;
import cn.vtohru.orm.entity.EntityManager;
import cn.vtohru.orm.data.IDataProxy;
import cn.vtohru.orm.data.PageData;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseQuery<T> extends AbstractQuery<T> {
    protected IDataProxy dataProxy;
    protected EntityManager entityManager;
    protected JpqlBuilder jpqlBuilder;
    protected Class<T> entityClass;
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
    public Query<T> from(Class<T> entityClass) {
        this.entityClass = entityClass;
        EntityInfo entity = entityManager.getEntity(entityClass);
        this.jpqlBuilder.setTable(entity.getTableName());
        return this;
    }

    @Override
    public String getJpql() {
        checkColumns();
        return this.jpqlBuilder.toJpql();
    }

    @Override
    public Future<T> first() {
        Promise<T> promise = Promise.promise();
        checkColumns();
        String jpql = this.jpqlBuilder.toJpql();
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(jpql + " limit 1", jpqlBuilder.getParams()).onSuccess(x -> {
                if (x.size() > 0) {
                    JsonObject row = x.getJsonObject(0);
                    T entity = entityManager.convertEntity(row, entityClass);
                    promise.complete(entity);
                } else {
                    promise.fail(new RuntimeException("no value find"));
                }
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<List<T>> all() {
        checkColumns();
        Promise<List<T>> promise = Promise.promise();
        String jpql = this.jpqlBuilder.toJpql();
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(jpql, jpqlBuilder.getParams()).onSuccess(x -> {
                if (x.size() > 0) {
                    List<T> results = new ArrayList<>();
                    for (int i = 0; i < x.size(); i++) {
                        JsonObject row = x.getJsonObject(i);
                        T entity = entityManager.convertEntity(row, entityClass);
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
    public Future<PageData<T>> pagination(int offset, int rowCount) {
        checkColumns();
        Promise<PageData<T>> promise = Promise.promise();
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(this.jpqlBuilder.toCountJpql(), this.jpqlBuilder.getParams()).onSuccess(x -> {
                PageData<T> pageData = new PageData<>();
                if (x.size() > 0) {
                    JsonObject row = x.getJsonObject(0);
                    Long count = row.getLong("count");
                    pageData.setTotal(count);
                    if (count > 0) {
                        session.execute(this.jpqlBuilder.toJpql() + " limit " + offset + "," + rowCount, this.jpqlBuilder.getParams()).onSuccess(y -> {
                            List<T> results = new ArrayList<>();
                            for (int i = 0; i < y.size(); i++) {
                                JsonObject record = y.getJsonObject(i);
                                T entity = entityManager.convertEntity(record, entityClass);
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
    public Query<T> select(String... params) {
        this.jpqlBuilder.select(params);
        this.useColumn = true;
        return this;
    }

    protected void checkColumns() {
        if (!this.useColumn) {
            EntityInfo entity = entityManager.getEntity(this.entityClass);
            String fields = entity.getFieldMap().values().stream().map(EntityField::getFieldName).collect(Collectors.joining(","));
            this.jpqlBuilder.select(fields);
        }
    }
}
