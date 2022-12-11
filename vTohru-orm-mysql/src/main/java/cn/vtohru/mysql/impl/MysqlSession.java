package cn.vtohru.mysql.impl;

import cn.vtohru.orm.DbSession;
import cn.vtohru.orm.ITransaction;
import cn.vtohru.orm.Query;
import cn.vtohru.orm.entity.EntityField;
import cn.vtohru.orm.entity.EntityInfo;
import cn.vtohru.orm.entity.EntityManager;
import cn.vtohru.orm.data.IDataProxy;
import cn.vtohru.orm.exception.OrmException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.*;

import javax.persistence.GenerationType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MysqlSession implements DbSession {

    private SqlConnection sqlConnection;
    private EntityManager entityManager;

    public MysqlSession(SqlConnection sqlConnection, EntityManager entityManager) {
        this.sqlConnection = sqlConnection;
        this.entityManager = entityManager;
    }
    @Override
    public <T> Future<T> persist(T model) {
        if (entityManager.existPrimary(model)) {
            return update(model);
        }
        return insert(model);
    }

    @Override
    public <T> Future<T> insert(T model) {
        EntityInfo entity = entityManager.getEntity(model.getClass());
        JsonObject entries = new JsonObject();

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(entity.getTableName()).append(" (");
        List<String> fields = new ArrayList<>();
        List<Object> fieldValues = new ArrayList<>();
        for (Map.Entry<String, EntityField> fieldEntry : entity.getFieldMap().entrySet()) {
            if (!GenerationType.IDENTITY.name().equals(fieldEntry.getValue().getGenerationType())) {
                fields.add(fieldEntry.getValue().getFieldName());
                fieldValues.add(fieldEntry.getValue().getProperty().get(model));
            }
        }
        sql.append(String.join(",", fields)).append(")").append("values (")
                .append(fields.stream().map(x -> "?").collect(Collectors.joining(","))).append(")");
        for (Map.Entry<String, EntityField> fieldEntry : entity.getFieldMap().entrySet()) {
            EntityField entityField = fieldEntry.getValue();
            if (!entityField.isPrimary()) {
                entries.put(entityField.getFieldName(), entityField.getProperty().get(model));
            }
        }
        Promise<T> promise = Promise.promise();
        sqlConnection.preparedQuery(sql.toString()).execute(Tuple.from(fieldValues)).onSuccess(x -> {
            Long id = x.property(PropertyKind.create("last-inserted-id", Long.class));
            for (EntityField keyField : entity.getKeyFields()) {
                keyField.getProperty().set(model, id);
            }
            promise.complete(model);
        }).onFailure(promise::fail);

        return promise.future();
    }

    @Override
    public <T> Future<Long> insertBatch(List<T> model) {
        Promise<Long> promise = Promise.promise();
        if (model == null || model.size() == 0) {
            promise.complete(0L);
        } else {
            EntityInfo entity = entityManager.getEntity(model.get(0).getClass());
            JsonObject entries = new JsonObject();

            StringBuilder sql = new StringBuilder();
            sql.append("insert into ").append(entity.getTableName()).append(" (");
            List<String> fields = new ArrayList<>();
            List<Object> fieldValues = new ArrayList<>();
            for (Map.Entry<String, EntityField> fieldEntry : entity.getFieldMap().entrySet()) {
                if (!GenerationType.IDENTITY.name().equals(fieldEntry.getValue().getGenerationType())) {
                    fields.add(fieldEntry.getValue().getFieldName());
                    fieldValues.add(fieldEntry.getValue().getProperty().get(model));
                }
            }
            sql.append(String.join(",", fields)).append(")").append("values ")
                    .append(model.stream().map(x -> "( " + fields.stream().map(y -> "?").collect(Collectors.joining(",")) + " )").collect(Collectors.joining(",")));

            for (T dt : model) {
                for (Map.Entry<String, EntityField> fieldEntry : entity.getFieldMap().entrySet()) {
                    EntityField entityField = fieldEntry.getValue();
                    if (!entityField.isPrimary()) {
                        entries.put(entityField.getFieldName(), entityField.getProperty().get(dt));
                    }
                }
            }
            sqlConnection.preparedQuery(sql.toString()).execute(Tuple.from(fieldValues)).onSuccess(x -> {
                promise.complete((long) x.size());
            }).onFailure(promise::fail);
        }
        return promise.future();
    }

    @Override
    public <T> Future<T> update(T model) {
        EntityInfo entity = entityManager.getEntity(model.getClass());
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(entity.getTableName()).append(" set ");
        List<String> fields = new ArrayList<>();
        List<Object> fieldValues = new ArrayList<>();
        for (Map.Entry<String, EntityField> fieldEntry : entity.getFieldMap().entrySet()) {
            if (!GenerationType.IDENTITY.name().equals(fieldEntry.getValue().getGenerationType())) {
                fields.add(fieldEntry.getValue().getFieldName());
                fieldValues.add(fieldEntry.getValue().getProperty().get(model));
            }
        }
        List<Object> keyValues = new ArrayList<>();
        for (EntityField keyField : entity.getKeyFields()) {
            keyValues.add(keyField.getProperty().get(model));
        }
        fieldValues.addAll(keyValues);
        sql.append(fields.stream().map(x->x +"=" + "?").collect(Collectors.joining(",")))
                .append(" where ").append(entity.getKeyFields().stream().map(x->x.getFieldName() + "=?").collect(Collectors.joining("and")));
        Promise<T> promise = Promise.promise();
        sqlConnection.preparedQuery(sql.toString()).execute(Tuple.from(fieldValues)).onSuccess(x -> {
            promise.complete(model);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<Void> remove(T model) {
        EntityInfo entity = entityManager.getEntity(model.getClass());
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(entity.getTableName());
        List<Object> fieldValues = new ArrayList<>();
        for (EntityField keyField : entity.getKeyFields()) {
            Object value = keyField.getProperty().get(model);
            fieldValues.add(value);
        }
        sql.append(" where ").append(entity.getKeyFields().stream().map(x->x.getFieldName() + "=?").collect(Collectors.joining("and")));
        Promise<Void> promise = Promise.promise();
        sqlConnection.preparedQuery(sql.toString()).execute(Tuple.from(fieldValues)).onSuccess(x -> {
            promise.complete();
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<T> fetch(T model) {
        EntityInfo entity = entityManager.getEntity(model.getClass());
        StringBuilder sql = new StringBuilder();
        sql.append("select ").append(entity.getFieldMap().values().stream().map(EntityField::getFieldName).collect(Collectors.joining(",")))
                .append(" from ").append(entity.getTableName()).append(" where ").append(entity.getKeyFields().stream().map(x -> x.getFieldName() + "=?").collect(Collectors.joining(" and ")));
        List<Object> fieldValues = new ArrayList<>();
        for (EntityField keyField : entity.getKeyFields()) {
            fieldValues.add(keyField.getProperty().get(model));
        }
        Promise<T> promise = Promise.promise();
        sqlConnection.preparedQuery(sql.toString()).execute(Tuple.from(fieldValues)).onSuccess(x -> {
            if (x.size() <= 0) {
                promise.fail(new OrmException("no value find"));
            } else {
                for (Row row : x) {
                    for (EntityField entityField : entity.getFieldMap().values()) {
                        entityField.getProperty().set(model, row.getValue(entityField.getFieldName()));
                    }
                    promise.complete(model);
                    break;
                }
            }
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Query<T> from(Class<T> clazz) {
        MysqlQuery<T> mysqlQuery = new MysqlQuery(new IDataProxy(null, this), entityManager);
        mysqlQuery.from(clazz);
        return mysqlQuery;
    }

    @Override
    public Future<JsonArray> execute(String jpql, List<Object> params) {
        Promise<JsonArray> promise = Promise.promise();
        sqlConnection.preparedQuery(jpql).execute(Tuple.from(params)).onSuccess(x -> {
            JsonArray results = new JsonArray();
            for (Row row : x) {
                JsonObject result = new JsonObject();
                for (int i = 0; i < row.size(); i++) {
                    String columnName = row.getColumnName(i);
                    result.put(columnName, row.getValue(i));
                }
                results.add(result);
            }
            promise.complete(results);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<ITransaction> beginTransaction() {
        Promise<ITransaction> promise = Promise.promise();
        this.sqlConnection.begin().onSuccess(x -> {
            promise.complete(new MysqlTransaction(x));
        }).onFailure(promise::fail);
        return promise.future();
    }

}
