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

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.query.IQueryCountResult;
import cn.vtohru.orm.dataaccess.query.IQueryResult;
import cn.vtohru.orm.dataaccess.query.impl.IQueryExpression;
import cn.vtohru.orm.dataaccess.query.impl.Query;
import cn.vtohru.orm.dataaccess.query.impl.QueryCountResult;
import cn.vtohru.orm.exception.QueryException;
import cn.vtohru.orm.mapping.IStoreObjectFactory;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.mongo.mapper.MongoMapper;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link IQuery} for Mongo
 *
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoQuery<T> extends Query<T> implements MongoDataAccesObject<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MongoQuery.class);
  private static final String SEARCH_LOG = "executing query in database %s collection %s with %s";

  /**
   * Constructor
   *
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
   */
  public MongoQuery(final Class<T> mapperClass, final IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.query.impl.Query#internalExecute(cn.vtohru.orm.
   * dataaccess.query.impl.IQueryExpression, io.vertx.core.Handler)
   */
  @Override
  public void internalExecute(final IQueryExpression queryExpression,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    try {
      doFind((MongoQueryExpression) queryExpression, resultHandler);
    } catch (Exception e) {
      Future<IQueryResult<T>> future = Future.failedFuture(e);
      resultHandler.handle(future);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.query.IQuery#executeExplain(io.vertx.core.Handler)
   */
  @Override
  public void executeExplain(final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    resultHandler.handle(Future.failedFuture(new UnsupportedOperationException("Not implemented yet")));
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * cn.vtohru.orm.dataaccess.query.impl.Query#internalExecuteCount(cn.vtohru.orm.
   * dataaccess.query.impl.IQueryExpression, io.vertx.core.Handler)
   */
  @Override
  public void internalExecuteCount(final IQueryExpression queryExpression,
      final Handler<AsyncResult<IQueryCountResult>> resultHandler) {
    count(((MongoQueryExpression) queryExpression).getQueryDefinition(), START_TRY_COUNT)
        .<IQueryCountResult> map(
            queryResult -> new QueryCountResult(getMapper(), getDataStore(), queryResult, queryExpression))
        .onComplete(resultHandler);
  }

  private Future<Long> count(final JsonObject queryDefinition, final int tryCount) {
    Promise<Long> f = Promise.promise();
    getMongoClient().count(getCollection(), queryDefinition, f);
    return f.future().recover(retryMethod(tryCount, count -> count(queryDefinition, count)));
  }

  private void doFind(final MongoQueryExpression queryExpression,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format(SEARCH_LOG, getDataStore().getSettings().getDatabaseName(), getCollection(),
          queryExpression.getQueryDefinition()));

    find(queryExpression, 1).onComplete(res -> {
      if (res.failed()) {
        resultHandler.handle(Future.failedFuture(new QueryException(queryExpression, res.cause())));
      } else {
        createQueryResult(res.result(), queryExpression, resultHandler);
      }
    });
  }

  private Future<List<JsonObject>> find(final MongoQueryExpression queryExpression, final int tryCount) {
    Promise<List<JsonObject>> f = Promise.promise();
    getMongoClient().findWithOptions(getCollection(), queryExpression.getQueryDefinition(),
        queryExpression.getFindOptions(), f);
    return f.future().recover(retryMethod(tryCount, count -> find(queryExpression, count)));
  }

  private void createQueryResult(final List<JsonObject> findList, final MongoQueryExpression queryExpression,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    generateResult(findList).onSuccess(x -> {
      MongoQueryResult<T> qR = new MongoQueryResult<T>(x, (MongoDataStore) getDataStore(),
              (MongoMapper) getMapper(), queryExpression);
      if (isReturnCompleteCount()) {
        if (queryExpression.getOffset() == 0 && queryExpression.getLimit() > 0
                && qR.size() < queryExpression.getLimit()) {
          qR.setCompleteResult(qR.size());
          resultHandler.handle(Future.succeededFuture(qR));
        } else {
          fetchCompleteCount(qR, resultHandler);
        }
      } else {
        qR.setCompleteResult(-1);
        resultHandler.handle(Future.succeededFuture(qR));
      }
    }).onFailure(e -> resultHandler.handle(Future.failedFuture(e)));
  }

  public Future<List<T>> generateResult(List<JsonObject> result) {
    List<Future> futures = new ArrayList<>();
    for (int i = 0; i < result.size(); i++) {
      IStoreObjectFactory<JsonObject> sf = (IStoreObjectFactory<JsonObject>) getDataStore().getStoreObjectFactory();
      Promise<T> promise = Promise.promise();
      sf.createStoreObject(result.get(i), getMapper(), x -> {
        if (x.failed()) {
          promise.fail(x.cause());
        } else {
          T pojo = x.result().getEntity();
          promise.complete(pojo);
        }
      });
      futures.add(promise.future());
    }
    Promise<List<T>> promise = Promise.promise();
    CompositeFuture.all(futures).onSuccess(x -> {
      promise.complete(x.result().list());
    }).onFailure(promise::fail);
    return promise.future();
  }



  private void fetchCompleteCount(final MongoQueryResult<T> qR,
      final Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
    executeCount(cr -> {
      if (cr.failed()) {
        resultHandler.handle(Future.failedFuture(cr.cause()));
      } else {
        long count = cr.result().getCount();
        qR.setCompleteResult(count);
        resultHandler.handle(Future.succeededFuture(qR));
      }
    });
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.query.impl.Query#getQueryExpressionClass()
   */
  @Override
  protected Class<? extends IQueryExpression> getQueryExpressionClass() {
    return MongoQueryExpression.class;
  }
}
