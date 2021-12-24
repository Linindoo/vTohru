package cn.vtohru.orm.mongo;

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
    MongoStoreObject<T> storeObject = new MongoStoreObject<>(mapper, entity);
    storeObject.initFromEntity(initResult -> {
      if (initResult.failed()) {
        handler.handle(Future.failedFuture(initResult.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
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
    MongoStoreObject<T> storeObject = new MongoStoreObject<T>(mapper, entity, view);
    storeObject.initFromEntity(initResult -> {
      if (initResult.failed()) {
        handler.handle(Future.failedFuture(initResult.cause()));
      } else {
        handler.handle(Future.succeededFuture(storeObject));
      }
    });
  }

}
