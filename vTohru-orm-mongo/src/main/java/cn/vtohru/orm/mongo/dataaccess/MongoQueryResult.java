package cn.vtohru.orm.mongo.dataaccess;

import cn.vtohru.orm.dataaccess.query.IQueryResult;
import cn.vtohru.orm.dataaccess.query.impl.AbstractQueryResult;
import cn.vtohru.orm.mapping.IStoreObjectFactory;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.mongo.mapper.MongoMapper;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * An implementation of {@link IQueryResult} for Mongo
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the underlaying mapper
 */
public class MongoQueryResult<T> extends AbstractQueryResult<T> {
  /**
   * Contains the original result from mongo
   */
  private List<JsonObject> jsonResult;


  /**
   * @param jsonResult
   * @param store
   * @param mapper
   */
  public MongoQueryResult(List<JsonObject> jsonResult, MongoDataStore store, MongoMapper mapper,
      MongoQueryExpression queryExpression) {
    super(store, mapper, jsonResult.size(), queryExpression);
    this.jsonResult = jsonResult;
  }

  @Override
  protected Future<T> generatePojo(int i) {
    JsonObject sourceObject = jsonResult.get(i);
    IStoreObjectFactory<JsonObject> sf = getDataStore().getStoreObjectFactory();
    Promise<T> promise = Promise.promise();
    sf.createStoreObject(sourceObject, getMapper(), result -> {
      if (result.failed()) {
        promise.fail(result.cause());
      } else {
        T pojo = result.result().getEntity();
        promise.complete(pojo);
      }
    });
    return promise.future();
  }

  public List<JsonObject> getOriginalResult() {
    return jsonResult;
  }
}
