package cn.vtohru.orm.mongo.dataaccess;

import cn.vtohru.orm.dataaccess.IDataAccessObject;
import cn.vtohru.orm.mongo.MongoDataStore;
import com.mongodb.MongoCommandException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.mongo.MongoClient;

import java.util.function.Function;

interface MongoDataAccesObject<T> extends IDataAccessObject<T> {

  int RETRY_TIMEOUT = 500;
  int RATE_LIMIT_ERROR_CODE = 16500;
  int MAX_RETRIES = 9;
  int START_TRY_COUNT = 0;

  default String getCollection() {
    return getMapper().getTableInfo().getName();
  }

  default MongoClient getMongoClient() {
    return (MongoClient) ((MongoDataStore) getDataStore()).getClient();
  }

  default <A> Function<Throwable, Future<A>> retryMethod(final int tryCount,
      final Function<Integer, Future<A>> action) {
    return e -> {
      if (e instanceof MongoCommandException && ((MongoCommandException) e).getCode() == RATE_LIMIT_ERROR_CODE
          && tryCount < MAX_RETRIES) {
        Promise<Void> f = Promise.promise();
        getDataStore().getVertx().setTimer(RETRY_TIMEOUT, res -> f.complete());
        return f.future().compose(v -> action.apply(tryCount + 1));
      } else {
        return Future.failedFuture(e);
      }
    };
  }
}
