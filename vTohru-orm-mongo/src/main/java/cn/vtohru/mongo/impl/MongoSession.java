package cn.vtohru.mongo.impl;

import cn.vtohru.orm.DbSession;
import cn.vtohru.orm.ITransaction;
import cn.vtohru.orm.Query;
import cn.vtohru.orm.data.IDataProxy;
import cn.vtohru.orm.entity.EntityField;
import cn.vtohru.orm.entity.EntityInfo;
import cn.vtohru.orm.entity.EntityManager;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.*;

import javax.persistence.GenerationType;
import java.util.List;

public class MongoSession implements DbSession {
    private MongoClient mongoClient;
    private EntityManager entityManager;
    private MongoTransaction transactionSession;


    public MongoSession(MongoClient mongoClient, EntityManager entityManager) {
        this.mongoClient = mongoClient;
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
        Promise<T> promise = Promise.promise();
        EntityInfo entity = entityManager.getEntity(model.getClass());
        JsonObject document = new JsonObject();
        for (EntityField entityField : entity.getFieldMap().values()) {
            if (!GenerationType.IDENTITY.name().equals(entityField.getGenerationType())) {
                document.put(entityField.getFieldName(), entityField.getProperty().get(model));
            }
        }
        Future<String> opFuture = (transactionSession == null || transactionSession.isClosed()) ? mongoClient.insertWithOptions(entity.getTableName(), document, WriteOption.ACKNOWLEDGED) : mongoClient.insertWithOptions(transactionSession.getClientSession(), entity.getTableName(), document, WriteOption.ACKNOWLEDGED);
        opFuture.onSuccess(x -> {
            for (EntityField keyField : entity.getKeyFields()) {
                keyField.getProperty().set(model, x);
            }
            promise.complete(model);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<T> update(T model) {
        Promise<T> promise = Promise.promise();
        EntityInfo entity = entityManager.getEntity(model.getClass());
        JsonObject document = new JsonObject();
        for (EntityField entityField : entity.getFieldMap().values()) {
            if (!GenerationType.IDENTITY.name().equals(entityField.getGenerationType())) {
                document.put(entityField.getFieldName(), entityField.getProperty().get(model));
            }
        }
        JsonObject query = new JsonObject();
        for (EntityField keyField : entity.getKeyFields()) {
            query.put(keyField.getFieldName(), keyField.getProperty().get(model));
        }
        UpdateOptions updateOptions = new UpdateOptions();
        Future<MongoClientUpdateResult> updateFuture = (transactionSession == null || transactionSession.isClosed()) ? mongoClient.updateCollectionWithOptions(entity.getTableName(), query, document, updateOptions) : mongoClient.updateCollectionWithOptions(transactionSession.getClientSession(), entity.getTableName(), query, document, updateOptions);
        updateFuture.onSuccess(x -> {
            System.out.println(x.toJson());
//            for (EntityField keyField : entity.getKeyFields()) {
//                keyField.getProperty().set(model, x);
//            }
            promise.complete(model);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<Void> remove(T model) {
        Promise<Void> promise = Promise.promise();
        EntityInfo entity = entityManager.getEntity(model.getClass());
        JsonObject query = new JsonObject();
        for (EntityField keyField : entity.getKeyFields()) {
            query.put(keyField.getFieldName(), keyField.getProperty().get(model));
        }
        Future<JsonObject> deleteFuture = (transactionSession == null || transactionSession.isClosed()) ? mongoClient.findOneAndDelete(entity.getTableName(), query) : mongoClient.findOneAndDelete(transactionSession.getClientSession(), entity.getTableName(), query);

        deleteFuture.onSuccess(x -> {
            promise.complete();
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Future<T> fetch(T model) {
        Promise<T> promise = Promise.promise();
        EntityInfo entity = entityManager.getEntity(model.getClass());
        JsonObject query = new JsonObject();
        for (EntityField keyField : entity.getKeyFields()) {
            query.put(keyField.getFieldName(), keyField.getProperty().get(model));
        }
        Future<JsonObject> findFuture = (transactionSession == null || transactionSession.isClosed()) ? mongoClient.findOne(entity.getTableName(), query, null) : mongoClient.findOne(transactionSession.getClientSession(), entity.getTableName(), query, null);

        findFuture.onSuccess(x -> {
            for (EntityField entityField : entity.getFieldMap().values()) {
                entityField.getProperty().set(model, x.getValue(entityField.getFieldName()));
            }
            promise.complete(model);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public <T> Query<T> from(Class<T> clazz) {
        MongoQuery<T> mongoQuery = new MongoQuery<>(new IDataProxy(null, this), entityManager);
        return mongoQuery.from(clazz);
    }

    @Override
    public Future<JsonArray> execute(String jpql, List<Object> params) {
        JsonArray array = new JsonArray();
        Promise<JsonArray> promise = Promise.promise();
        for (Object param : params) {
            array.add(param);
        }
        JsonObject command = new JsonObject()
                .put("aggregate", jpql)
                .put("pipeline", array)
                .put("cursor",new JsonObject());
        Future<JsonObject> aggregateFuture = (transactionSession == null || transactionSession.isClosed()) ? mongoClient.runCommand("aggregate", command) : mongoClient.runCommand(transactionSession.getClientSession(), "aggregate", command);
        aggregateFuture.onSuccess(x -> {
            JsonObject cursorData = x.getJsonObject("cursor");
            JsonArray firstBatch = cursorData.getJsonArray("firstBatch");
            promise.complete(firstBatch);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<ITransaction> beginTransaction() {
        Promise<ITransaction> promise = Promise.promise();
        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        mongoClient.startSession().onSuccess(x -> {
            x.startTransaction(txnOptions);
            MongoTransaction mongoTransaction = new MongoTransaction(x);
            this.transactionSession = mongoTransaction;
            promise.complete(mongoTransaction);
        }).onFailure(promise::fail);
        return promise.future();
    }

}
