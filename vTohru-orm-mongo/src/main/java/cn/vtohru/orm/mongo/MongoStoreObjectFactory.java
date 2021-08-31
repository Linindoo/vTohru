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
package cn.vtohru.orm.mongo;

import cn.vtohru.annotation.Verticle;
import cn.vtohru.orm.annotation.lifecycle.BeforeSave;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IStoreObject;
import cn.vtohru.orm.mapping.impl.AbstractStoreObjectFactory;
import cn.vtohru.orm.mongo.dataaccess.MongoStoreObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * An implementation for Mongo
 *
 * @author Michael Remme
 *
 */

public class MongoStoreObjectFactory extends AbstractStoreObjectFactory<JsonObject> {

  @Override
  public <T> void createStoreObject(final IMapper<T> mapper, final T entity,
      final Handler<AsyncResult<IStoreObject<T, JsonObject>>> handler) {
    mapper.executeLifecycle(BeforeSave.class, entity, lcr -> {
      if (lcr.failed()) {
        handler.handle(Future.failedFuture(lcr.cause()));
      } else {
        MongoStoreObject<T> storeObject = new MongoStoreObject<>(mapper, entity);
        storeObject.initFromEntity(initResult -> {
          if (initResult.failed()) {
            handler.handle(Future.failedFuture(initResult.cause()));
          } else {
            handler.handle(Future.succeededFuture(storeObject));
          }
        });
      }
    });
  }

  @Override
  public <T> void createStoreObject(final JsonObject storedObject, final IMapper<T> mapper,
      final Handler<AsyncResult<IStoreObject<T, JsonObject>>> handler) {
    MongoStoreObject<T> storeObject = new MongoStoreObject<>(storedObject, mapper);
    storeObject.initToEntity(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

  public <T> void createStoreObject(final IMapper<T> mapper, final T entity, final JsonObject view,
      final Handler<AsyncResult<IStoreObject<T, JsonObject>>> handler) {
    mapper.executeLifecycle(BeforeSave.class, entity, lcr -> {
      if (lcr.failed()) {
        handler.handle(Future.failedFuture(lcr.cause()));
      } else {
        MongoStoreObject<T> storeObject = new MongoStoreObject<T>(mapper, entity, view);
        storeObject.initFromEntity(initResult -> {
          if (initResult.failed()) {
            handler.handle(Future.failedFuture(initResult.cause()));
          } else {
            handler.handle(Future.succeededFuture(storeObject));
          }
        });
      }
    });
  }

}
