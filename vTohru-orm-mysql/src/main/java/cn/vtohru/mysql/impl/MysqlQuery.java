package cn.vtohru.mysql.impl;

import cn.vtohru.orm.Condition;
import cn.vtohru.orm.builder.*;
import cn.vtohru.orm.Query;
import cn.vtohru.orm.data.PageData;
import cn.vtohru.orm.entity.EntityManager;
import cn.vtohru.orm.data.IDataProxy;
import cn.vtohru.orm.exception.OrmException;
import io.micronaut.core.util.CollectionUtils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class MysqlQuery<T> extends BaseQuery<T> {

    public MysqlQuery(IDataProxy dataProxy, EntityManager entityManager) {
        this(dataProxy, entityManager, new GenericJpqlBuilder());
    }
    public MysqlQuery(IDataProxy dataProxy, EntityManager entityManager, JpqlBuilder jpqlBuilder) {
        super(dataProxy, entityManager, jpqlBuilder);
    }

    @Override
    public Query where(String column, Object params) {
        return appendCondition(true, column, "=", params);
    }



    @Override
    public Query eq(String column, Object param) {
        return appendCondition(true,column, "=", param);
    }

    @Override
    public Query ne(String column, Object param) {
        return appendCondition(true,column, "!=", param);
    }

    @Override
    public Query le(String column, Object param) {
        return appendCondition(true,column, ">=", param);
    }

    @Override
    public Query lt(String column, Object param) {
        return appendCondition(true,column, ">", param);
    }

    @Override
    public Query<T> in(String column, Collection<Object> params) {
        return appendCondition(true, column, "in", params);
    }

    @Override
    public Query ge(String column, Object param) {
        return appendCondition(true,column,  "<=", param);
    }

    @Override
    public Query gt(String column, Object param) {
        return appendCondition(true,column, "<", param);
    }

    @Override
    public Query like(String column, Object param) {
        return appendCondition(true,column, "like", param);
    }

    @Override
    public Future<T> first() {
        return first(false);
    }

    @Override
    public Future<T> first(boolean errorOnNull) {
        Promise<T> promise = Promise.promise();
        checkColumns();
        String jpql = getSql();
        System.out.println(jpql);
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(jpql + " limit 1", getParams()).onSuccess(x -> {
                if (x != null && x.size() > 0) {
                    JsonObject row = x.getJsonObject(0);
                    T entity = entityManager.convertEntity(row, entityClass);
                    promise.complete(entity);
                } else if (errorOnNull) {
                    promise.fail(new OrmException("no value find"));
                } else {
                    promise.complete();
                }
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Long> count() {
        Promise<Long> promise = Promise.promise();
        StringBuilder countBuilder = new StringBuilder();
        countBuilder.append("select count(1) ").append(" from ").append(getTableName()).append(" where ").append(getWhereSegment());
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(countBuilder.toString(), getParams()).onSuccess(x -> {
                if (x.size() > 0) {
                    JsonObject row = x.getJsonObject(0);
                    Long count = row.getLong("count");
                    promise.complete(count);
                } else {
                    promise.fail(new RuntimeException("no value find"));
                }
            }).onFailure(promise::fail);

        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Void> delete() {
        Promise<Void> promise = Promise.promise();
        StringBuilder countBuilder = new StringBuilder();
        countBuilder.append("delete ").append(" from ").append(getTableName()).append(" where ").append(getWhereSegment());
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(countBuilder.toString(), getParams()).onSuccess(x -> {
                promise.complete();
            }).onFailure(promise::fail);

        }).onFailure(promise::fail);
        return promise.future();
    }


    @Override
    public Future<List<T>> all() {
        checkColumns();
        Promise<List<T>> promise = Promise.promise();
        String jpql = getSql();
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(jpql, getParams()).onSuccess(x -> {
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
        StringBuilder countBuilder = new StringBuilder();
        countBuilder.append("select count(1) ").append(" from ").append(getTableName()).append(" where ").append(getWhereSegment());
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute(countBuilder.toString(), getParams()).onSuccess(x -> {
                PageData<T> pageData = new PageData<>();
                if (x.size() > 0) {
                    JsonObject row = x.getJsonObject(0);
                    Long count = row.getLong("count");
                    pageData.setTotal(count);
                    if (count > 0) {
                        session.execute(getSql() + " limit " + offset + "," + rowCount, getParams()).onSuccess(y -> {
                            List<T> results = new ArrayList<>();
                            for (int i = 0; i < y.size(); i++) {
                                JsonObject record = y.getJsonObject(i);
                                T entity = entityManager.convertEntity(record, entityClass);
                                results.add(entity);
                            }
                            pageData.setRecords(results);
                            promise.complete(pageData);

                        }).onFailure(promise::fail);
                    } else {
                        promise.complete(pageData);
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
    public AbstractQuery instance() {
        return new MysqlQuery(this.dataProxy, this.entityManager);
    }
    @Override
    public Query appendChild(Consumer consumer) {
        Query instance = instance();
        consumer.accept(instance);
        if ("or".equalsIgnoreCase(this.preCondition)) {
            this.jpqlBuilder.appendChild(false, instance.geBuilder());
        } else {
            this.jpqlBuilder.appendChild(true, instance.geBuilder());
        }
        return this;
    }

    public String getSql() {
        StringBuilder pqlBuilder = new StringBuilder();
        pqlBuilder.append("select").append(" ").append(String.join(",", this.jpqlBuilder.columns())).append(" from ").append(getTableName()).append(" where ").append(getWhereSegment());
        if (CollectionUtils.isNotEmpty(this.jpqlBuilder.orders())) {
            pqlBuilder.append("order by ").append(this.jpqlBuilder.orders().stream().map(x->x.getOrder() + (x.isReverse()?" ":" desc ")).collect(Collectors.joining(",")));
        }
        return pqlBuilder.toString();
    }



    private String getWhereSegment() {
        StringBuilder segment = new StringBuilder();
        List<Condition> conditions = this.jpqlBuilder.getCondition();
        for (int i = 0; i < conditions.size(); i++) {
            Condition condition = conditions.get(i);
            if (i != 0) {
                segment.append(" ").append(condition.isAnd() ? "and" : "or").append(" ");
            }
            segment.append(" ").append(getConditionSegment(condition));
        }
        return segment.toString();
    }

    private String getConditionSegment(Condition condition) {
        if (condition instanceof AggregateCondition) {
            AggregateCondition aggregateCondition = (AggregateCondition) condition;
            StringBuilder segment = new StringBuilder();
            segment.append("(");
            for (int i = 0; i < aggregateCondition.getConditions().size(); i++) {
                SingleCondition child = aggregateCondition.getConditions().get(i);
                if (i == 0) {
                    segment.append(" ").append(child.getColumn()).append(" ").append(child.getCondition()).append(" ?");
                } else {
                    segment.append(" ").append(child.isAnd() ? "and" : "or").append(" ").append(child.getColumn()).append(" ").append(child.getCondition()).append(" ?");
                }
            }
            segment.append(")");
            return segment.toString();
        } else if (condition instanceof SingleCondition) {
            SingleCondition singleCondition = (SingleCondition) condition;
            return singleCondition.getColumn() + " " + singleCondition.getCondition() + " ?";
        }
        return " ";

    }

}
