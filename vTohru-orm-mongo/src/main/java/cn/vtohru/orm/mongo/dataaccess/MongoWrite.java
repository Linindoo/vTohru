/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package cn.vtohru.orm.mongo.dataaccess;

import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.query.ISearchCondition;
import cn.vtohru.orm.dataaccess.query.impl.IQueryExpression;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.dataaccess.write.IWriteEntry;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.dataaccess.write.WriteAction;
import cn.vtohru.orm.dataaccess.write.impl.AbstractWrite;
import cn.vtohru.orm.dataaccess.write.impl.WriteEntry;
import cn.vtohru.orm.exception.DuplicateKeyException;
import cn.vtohru.orm.exception.WriteException;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IStoreObject;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.mongo.MongoStoreObjectFactory;
import cn.vtohru.orm.mongo.mapper.datastore.MongoColumnInfo;
import cn.vtohru.orm.observer.IObserverContext;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.BulkOperationType;
import io.vertx.ext.mongo.MongoClientBulkWriteResult;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * An implementation of {@link IWrite} for Mongo
 *
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoWrite<T> extends AbstractWrite<T> implements MongoDataAccesObject<T> {

  protected JsonObject view = new JsonObject();
  private JsonObject setOnInsertFields;

  /**
   * Constructor
   *
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
   */
  public MongoWrite(final Class<T> mapperClass, final MongoDataStore datastore) {
    super(mapperClass, datastore);
  }

  @Override
  public Future<IWriteResult> internalSave(final IObserverContext context) {
    Promise<IWriteResult> promise = Promise.promise();
    List<T> entities = getObjectsToSave();
    if (getQuery() != null && entities.size() > 1)
      promise.fail(new IllegalStateException("Can only update one entity at once if a query is defined"));
    else if (entities.isEmpty()) {
      promise.complete(new MongoWriteResult());
    } else {
      CompositeFuture.all(entities.stream().map(entity -> convertEntity(entity, context)).collect(toList()))
              .compose(cfConvert -> {
                List<StoreObjectHolder> holders = cfConvert.list();
                return writeEntities(holders).recover(e -> {
                  if (entities.size() == 1 && e instanceof MongoBulkWriteException)
                    return handleSingleWriteError(holders, (MongoBulkWriteException) e);
                  else
                    return Future.failedFuture(e);
                });
              }).onSuccess(x -> promise.complete(new MongoWriteResult(x.list()))).onFailure(promise::fail);
    }
    return promise.future();
  }

  private Future<CompositeFuture> handleSingleWriteError(final List<StoreObjectHolder> holders,
      final MongoBulkWriteException bulkException) {
    if (bulkException.getWriteErrors().size() == 1) {
      BulkWriteError writeError = bulkException.getWriteErrors().get(0);
      if (writeError.getCode() == 11000) {
        if (writeError.getMessage().contains("_id_")) {
          if (getMapper().getKeyGenerator() != null) {
            Promise<Void> promise = Promise.promise();
            holders.get(0).storeObject.getNextId(promise);
            return promise.future().compose(v -> writeEntities(holders));
          } else {
            return Future.failedFuture(new DuplicateKeyException(
                    "Duplicate key error on insert, but no KeyGenerator is defined", bulkException));
          }
        } else {
          return Future.failedFuture(new DuplicateKeyException(bulkException));
        }
      }
    }
    return Future.failedFuture(bulkException);
  }

  private Future<CompositeFuture> writeEntities(final List<StoreObjectHolder> holders) {
    List<BulkOperation> bulkOperations = holders.stream().map(storeObjectHolder -> storeObjectHolder.bulkOperation)
        .collect(toList());
    return write(bulkOperations, START_TRY_COUNT).compose(writeResult -> {
      @SuppressWarnings("rawtypes")
      List<Future> futures = IntStream.range(0, holders.size())
          .mapToObj(i -> finishWrite(getObjectsToSave().get(i), holders.get(i), writeResult)).collect(toList());
      return CompositeFuture.all(futures);
    });
  }

  private Future<MongoClientBulkWriteResult> write(List<BulkOperation> bulkOperations, final int tryCount) {
    Promise<MongoClientBulkWriteResult> promise = Promise.promise();
    String collection = getCollection();
    getMongoClient().bulkWrite(collection, bulkOperations).onComplete(promise);
    return promise.future().recover(retryMethod(tryCount, count -> write(bulkOperations, count)));
  }

  private Future<IWriteEntry> finishWrite(final T entity, final StoreObjectHolder holder,
      final MongoClientBulkWriteResult writeResult) {
    BulkOperation bulkOperation = holder.bulkOperation;
    MongoStoreObject<T> storeObject = holder.storeObject;
    Promise<IWriteEntry> promise = Promise.promise();
    Future<IWriteEntry> fAfterWrite = promise.future();
    if (bulkOperation.getType() == BulkOperationType.INSERT) {
      Object newId = bulkOperation.getDocument().getString("_id");
      if (newId == null)
        newId = storeObject.getGeneratedId();
      finishInsert(newId, entity, storeObject, promise);
    } else {
      Object currentId = storeObject.get(getMapper().getIdInfo().getField());
      if (getQuery() == null)
        finishUpdate(currentId, entity, storeObject, promise);
      else
        finishQueryUpdate(currentId, entity, storeObject, writeResult, promise);
    }
    return fAfterWrite;
  }

  private Future<StoreObjectHolder> convertEntity(final T entity, final IObserverContext context) {
    return preSave(entity, context).compose(v -> createStoreObject(entity)).compose(this::createBulkOperation);
  }

  private Future<StoreObjectHolder> createBulkOperation(final MongoStoreObject<T> storeObject) {
    if (storeObject.isNewInstance()) {
      if (getQuery() != null) {
        return Future.failedFuture(new IllegalStateException("Can not update with a query and objects without id"));
      } else
        return Future.succeededFuture(
            new StoreObjectHolder(storeObject, BulkOperation.createInsert(new JsonObject(storeObject.getContainer().toBuffer()))));
    } else {
      IMapper<T> mapper = getMapper();
      Object currentId = storeObject.get(mapper.getIdInfo().getField());
      if (getQuery() != null) {
        IQuery<T> q = getDataStore().createQuery(getMapperClass());
        q.setSearchCondition(ISearchCondition.and(ISearchCondition.in(mapper.getIdInfo().getIndexedField(), currentId),
            getQuery().getSearchCondition()));
        Promise<IQueryExpression> promise = Promise.promise();
        q.buildQueryExpression(null, promise);
        JsonObject document = new JsonObject(storeObject.getContainer().toBuffer());
        document.remove(MongoColumnInfo.ID_FIELD_NAME);
        return promise.future().compose(queryExpression -> {
          JsonObject filter = ((MongoQueryExpression) queryExpression).getQueryDefinition();
          BulkOperation bulkOperation;
          if (partialUpdate)
            bulkOperation = createPartialUpdate(filter, document, false);
          else
            bulkOperation = BulkOperation.createReplace(filter, document, false);
          return Future.succeededFuture(new StoreObjectHolder(storeObject, bulkOperation));
        });
      } else {
        JsonObject filter = new JsonObject().put(MongoColumnInfo.ID_FIELD_NAME, currentId);
        BulkOperation bulkOperation;
        JsonObject container = storeObject.getContainer();
        JsonObject document = new JsonObject(container.toBuffer());
        document.remove(MongoColumnInfo.ID_FIELD_NAME);
        if (partialUpdate)
          bulkOperation = createPartialUpdate(filter, document, true);
        else
          bulkOperation = BulkOperation.createReplace(filter, document, true);
        return Future.succeededFuture(new StoreObjectHolder(storeObject, bulkOperation));
      }
    }
  }

  private BulkOperation createPartialUpdate(final JsonObject filter, final JsonObject object, final boolean upsert) {
    JsonObject document = new JsonObject().put("$set", object);
    if (setOnInsertFields != null) {
      document.put("$setOnInsert", setOnInsertFields);
    }
    return BulkOperation.createUpdate(filter, document, upsert, false);
  }

  private class StoreObjectHolder {
    private final MongoStoreObject<T> storeObject;
    private final BulkOperation bulkOperation;

    protected StoreObjectHolder(final MongoStoreObject<T> storeObject, final BulkOperation bulkOperation) {
      this.storeObject = storeObject;
      this.bulkOperation = bulkOperation;
    }

  }

  private Future<MongoStoreObject<T>> createStoreObject(final T entity) {
    Promise<MongoStoreObject<T>> promise = Promise.promise();
    ((MongoStoreObjectFactory) getDataStore().getStoreObjectFactory()).createStoreObject(getMapper(), entity, view,
        res -> promise.handle(res.map(storeObject -> (MongoStoreObject<T>) storeObject)));
    return promise.future();
  }

  /**
   * Execution done before instances are stored into the datastore
   *
   * @return
   */
  protected Future<Void> preSave(final T entity, final IObserverContext context) {
    if (isNewInstance(entity)) {
      return getMapper().getObserverHandler().handleBeforeInsert(this, entity, context);
    } else {
      return getMapper().getObserverHandler().handleBeforeUpdate(this, entity, context);
    }
  }

  /**
   * We need the info before the {@link IStoreObject} is created for the event beforeSave
   *
   * @param entity
   * @return
   */
  private boolean isNewInstance(final T entity) {
    Object javaValue = getMapper().getIdInfo().getField().getPropertyAccessor().readData(entity);
    return javaValue == null;
  }

  private void finishQueryUpdate(final Object id, final T entity, final MongoStoreObject<T> storeObject,
      final MongoClientBulkWriteResult updateResult, final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    if (updateResult.getMatchedCount() != 0 && updateResult.getMatchedCount() == updateResult.getModifiedCount()) {
      finishUpdate(id, entity, storeObject, resultHandler);
    } else if (updateResult.getMatchedCount() == 0 && updateResult.getModifiedCount() == 0) {
      resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.NOT_MATCHED)));
    } else {
      resultHandler.handle(Future.failedFuture(new WriteException("Matched " + updateResult.getMatchedCount()
          + "documents but modified: " + updateResult.getModifiedCount() + "documents")));
    }
  }

  private void finishInsert(final Object id, final T entity, final MongoStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    setIdValue(id, storeObject, result -> {
      if (result.failed()) {
        resultHandler.handle(Future.failedFuture(result.cause()));
        return;
      }
      executePostSave(entity, lcr -> {
        if (lcr.failed()) {
          resultHandler.handle(Future.failedFuture(lcr.cause()));
        } else {
          resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.INSERT)));
        }
      });

    });
  }

  private void finishUpdate(final Object id, final T entity, final MongoStoreObject<T> storeObject,
      final Handler<AsyncResult<IWriteEntry>> resultHandler) {
    executePostSave(entity, lcr -> {
      if (lcr.failed()) {
        resultHandler.handle(Future.failedFuture(lcr.cause()));
      } else {
        resultHandler.handle(Future.succeededFuture(new WriteEntry(storeObject, id, WriteAction.UPDATE)));
      }
    });
  }

  public void setView(final JsonObject view) {
    this.view = view;
  }

  public void setSetOnInsertFields(final JsonObject setOnInsertFields) {
    this.setOnInsertFields = setOnInsertFields;
  }

}
