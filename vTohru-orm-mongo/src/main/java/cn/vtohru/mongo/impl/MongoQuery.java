package cn.vtohru.mongo.impl;

import cn.vtohru.orm.Condition;
import cn.vtohru.orm.Query;
import cn.vtohru.orm.builder.AggregateCondition;
import cn.vtohru.orm.builder.BaseQuery;
import cn.vtohru.orm.builder.GenericJpqlBuilder;
import cn.vtohru.orm.builder.SingleCondition;
import cn.vtohru.orm.data.IDataProxy;
import cn.vtohru.orm.data.PageData;
import cn.vtohru.orm.entity.EntityManager;
import cn.vtohru.orm.exception.OrmException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

public class MongoQuery<T> extends BaseQuery<T> {

    public MongoQuery(IDataProxy dataProxy, EntityManager entityManager) {
        super(dataProxy, entityManager, new GenericJpqlBuilder());
    }


    @Override
    public Query where(String column, Object params) {
        return appendCondition(true, column, "$eq", params);
    }


    @Override
    public Query<T> eq(String column, Object param) {
        return appendCondition(true,column, "$eq", param);
    }

    @Override
    public Query<T> ne(String column, Object param) {
        return appendCondition(true,column, "$ne", param);
    }

    @Override
    public Query<T> le(String column, Object param) {
        return appendCondition(true,column, "$lte", param);
    }

    @Override
    public Query<T> lt(String column, Object param) {
        return appendCondition(true,column, "$lt", param);
    }

    @Override
    public Query<T> in(String column, Collection<Object> params) {
        return appendCondition(true,column, "$in", params);
    }

    @Override
    public Query<T> ge(String column, Object param) {
        return appendCondition(true,column, "$gte", param);
    }

    @Override
    public Query<T> gt(String column, Object param) {
        return appendCondition(true,column, "$gt", param);
    }

    @Override
    public Query<T> like(String column, Object param) {
        return appendCondition(true,column, "$regex", param);
    }


    @Override
    public List<Object> getParams() {
        return null;
    }

    @Override
    public Query<T> instance() {
        return new MongoQuery<>(this.dataProxy, this.entityManager);
    }

    @Override
    public Future<T> first() {
        return first(false);
    }

    @Override
    public Future<T> first(boolean errorOnNull) {
        Promise<T> promise = Promise.promise();
        checkColumns();
        JsonArray pipeline = new JsonArray();
        JsonObject command = new JsonObject()
                .put("aggregate", getTableName())
                .put("pipeline", pipeline)
                .put("cursor", new JsonObject());
        pipeline.add(getMatch());
        pipeline.add(new JsonObject().put("$skip", 0));
        pipeline.add(new JsonObject().put("$limit", 1));
        JsonObject field = new JsonObject();
        pipeline.add(new JsonObject().put("$project", field));
        List<String> columns = jpqlBuilder.columns();

        for (String column : columns) {
            field.put(column, 1);
        }
        List<Object> list = new ArrayList<>();
        list.add(command);
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute("aggregate", list).onSuccess(x -> {
                if (x != null && x.size() > 0) {
                    JsonObject row = x.getJsonObject(0);
                    T entity = entityManager.convertEntity(row, entityClass);
                    promise.complete(entity);
                } else if (errorOnNull) {
                    promise.fail(new OrmException("no match record"));
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
        checkColumns();
        JsonArray pipeline = new JsonArray();
        JsonObject command = new JsonObject()
                .put("aggregate", getTableName())
                .put("pipeline", pipeline)
                .put("cursor", new JsonObject());
        JsonObject match = getMatch();
        pipeline.add(match);
        pipeline.add(new JsonObject().put("$count", "total"));
        List<Object> list = new ArrayList<>();
        list.add(command);
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute("aggregate", list).onSuccess(x -> {
                if (x != null && x.size() > 0) {
                    JsonObject ret = x.getJsonObject(0);
                    Long total = ret.getLong("total");
                    promise.complete(total);
                } else {
                    promise.complete(0L);
                }
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Void> delete() {
        Promise<Void> promise = Promise.promise();
        checkColumns();
        List<Object> list = new ArrayList<>();
        JsonObject match = getMatch();
        JsonObject command = new JsonObject()
                .put("delete", getTableName())
                .put("deletes", new JsonArray().add(new JsonObject().put("q", match.getJsonObject("$match")).put("limit", 0)));
        list.add(command);
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute("delete", list).onSuccess(x -> {
                promise.complete();
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    private JsonObject getMatch() {
        List<SingleCondition> singleConditions = new ArrayList<>();
        for (Condition condition : this.jpqlBuilder.getCondition()) {
            if (condition instanceof SingleCondition) {
                singleConditions.add((SingleCondition) condition);
            } else if (condition instanceof AggregateCondition) {
                singleConditions.addAll(((AggregateCondition) condition).getConditions());
            }
        }
        Map<Boolean, List<SingleCondition>> conditionMap = singleConditions.stream().collect(Collectors.groupingBy(Condition::isAnd));

        List<SingleCondition> andConditions = conditionMap.getOrDefault(true, Collections.emptyList());
        List<SingleCondition> orConditions = conditionMap.getOrDefault(false,Collections.emptyList());
        JsonObject query = new JsonObject();
        if (andConditions.size() > 0) {
            JsonArray array = new JsonArray();
            for (SingleCondition condition : andConditions) {
                JsonObject andInfo = new JsonObject();
                andInfo.put(condition.getColumn(), new JsonObject().put(condition.getCondition(), condition.getValue()));
                array.add(andInfo);
            }
            query.put("$and", array);
        }
        if (orConditions.size() > 0) {
            JsonArray array = new JsonArray();
            for (SingleCondition condition : orConditions) {
                JsonObject orInfo = new JsonObject();
                orInfo.put(condition.getColumn(), new JsonObject().put(condition.getCondition(), condition.getValue()));
                array.add(orInfo);
            }
            query.put("$or", array);
        }
        return new JsonObject().put("$match", query);
    }



    @Override
    public Future<List<T>> all() {
        Promise<List<T>> promise = Promise.promise();
        checkColumns();
        JsonObject field = new JsonObject();
        List<String> columns = jpqlBuilder.columns();

        for (String column : columns) {
            field.put(column, 1);
        }
        JsonArray pipeline = new JsonArray();
        pipeline.add(getMatch());
        pipeline.add(new JsonObject().put("$project", field));
        JsonObject command = new JsonObject()
                .put("aggregate", getTableName())
                .put("pipeline", pipeline)
                .put("cursor", new JsonObject());
        List<Object> list = new ArrayList<>();
        list.add(command);
        this.dataProxy.getSession().onSuccess(session -> {
            session.execute("aggregate", list).onSuccess(x -> {
                if (x.size() > 0) {
                    List<T> data = new ArrayList<>();
                    for (int i = 0; i < x.size(); i++) {
                        JsonObject row = x.getJsonObject(i);
                        T entity = entityManager.convertEntity(row, entityClass);
                        data.add(entity);
                    }
                    promise.complete(data);
                } else {
                    promise.fail(new RuntimeException("no value find"));
                }
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<PageData<T>> pagination(int offset, int rowCount) {
        Promise<PageData<T>> promise = Promise.promise();
        count().onSuccess(x -> {
            PageData<T> pageData = new PageData<>();
            pageData.setTotal(x);
            if (x > 0) {
                JsonObject match = getMatch();
                JsonArray pipeline = new JsonArray();
                pipeline.add(match);
                pipeline.add(new JsonObject().put("$skip", offset));
                pipeline.add(new JsonObject().put("$limit", rowCount));
                JsonObject field = new JsonObject();
                List<String> columns = jpqlBuilder.columns();
                for (String column : columns) {
                    field.put(column, 1);
                }
                pipeline.add(new JsonObject().put("$project", field));
                JsonObject command = new JsonObject()
                        .put("aggregate", getTableName())
                        .put("pipeline", pipeline)
                        .put("cursor", new JsonObject());
                List<Object> list = new ArrayList<>();
                list.add(command);
                this.dataProxy.getSession().onSuccess(session -> {
                    session.execute("aggregate", list).onSuccess(y -> {
                        List<T> results = new ArrayList<>();
                        for (int i = 0; i < y.size(); i++) {
                            JsonObject record = y.getJsonObject(i);
                            T entity = entityManager.convertEntity(record, entityClass);
                            results.add(entity);
                        }
                        pageData.setRecords(results);
                        promise.complete(pageData);
                    }).onFailure(promise::fail);
                });
            } else {
                pageData.setRecords(new ArrayList<>());
                promise.complete(pageData);
            }
        }).onFailure(promise::fail);
        return promise.future();
    }
}
