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
import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.delete.IDeleteResult;
import cn.vtohru.orm.dataaccess.delete.impl.Delete;
import cn.vtohru.orm.dataaccess.query.IQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;

/**
 * An implementation of {@link IDelete} for Mongo
 *
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoDelete<T> extends Delete<T> implements MongoDataAccesObject<T> {

  /**
   * Constructor
   *
   * @param mapperClass
   *          the mapper class
   * @param datastore
   *          the datastore to be used
   */
  public MongoDelete(final Class<T> mapperClass, final IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.delete.impl.Delete#deleteQuery(cn.vtohru.orm.
   * dataaccess.query.IQuery, io.vertx.core.Handler)
   */
  @Override
  protected void deleteQuery(final IQuery<T> q, final Handler<AsyncResult<IDeleteResult>> resultHandler) {
    q.buildQueryExpression(null, qDefResult -> {
      if (qDefResult.failed()) {
        resultHandler.handle(Future.failedFuture(qDefResult.cause()));
      } else {
        removeDocuments(((MongoQueryExpression) qDefResult.result()).getQueryDefinition(), START_TRY_COUNT)
                .<IDeleteResult>map(result -> new MongoDeleteResult(getDataStore(), getMapper(), result)).onComplete(resultHandler::handle);
      }
    });
  }

  private Future<MongoClientDeleteResult> removeDocuments(final JsonObject queryExpression, final int tryCount) {
    Promise<MongoClientDeleteResult> promise = Promise.promise();
    getMongoClient().removeDocuments(getCollection(), queryExpression, promise);
    return promise.future().recover(retryMethod(tryCount, count -> removeDocuments(queryExpression, count)));
  }

}
