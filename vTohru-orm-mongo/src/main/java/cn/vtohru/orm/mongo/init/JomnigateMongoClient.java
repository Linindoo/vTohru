package cn.vtohru.orm.mongo.init;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.impl.MongoClientImpl;
import io.vertx.ext.mongo.impl.config.MongoClientOptionsParser;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JomnigateMongoClient extends MongoClientImpl {

  private final String database;
  private final Vertx vertx;

  JomnigateMongoClient(final Vertx vertx, final JsonObject config, final String dataSourceName) {
    super(vertx, config, dataSourceName);
    this.vertx = vertx;
    MongoClientOptionsParser parser = new MongoClientOptionsParser(vertx, config);
    database = parser.database();
  }

  public MongoCollection<JsonObject> getCollection(final String name) {
    MongoCollection<JsonObject> coll = mongo.getDatabase(database).getCollection(name, JsonObject.class);
    return coll;
  }

  /**
   * Create a Mongo client which maintains its own data source.
   *
   * @param vertx
   *          the Vert.x instance
   * @param config
   *          the configuration
   * @return the client
   */
  static JomnigateMongoClient createNonShared(final Vertx vertx, final JsonObject config) {
    return new JomnigateMongoClient(vertx, config, UUID.randomUUID().toString());
  }

  /**
   * Create a Mongo client which shares its data source with any other Mongo clients created with the same
   * data source name
   *
   * @param vertx
   *          the Vert.x instance
   * @param config
   *          the configuration
   * @param dataSourceName
   *          the data source name
   * @return the client
   */
  static JomnigateMongoClient createShared(final Vertx vertx, final JsonObject config, final String dataSourceName) {
    return new JomnigateMongoClient(vertx, config, dataSourceName);
  }

  /**
   * Like {@link #createShared(io.vertx.core.Vertx, JsonObject, String)} but with the default data source name
   *
   * @param vertx
   *          the Vert.x instance
   * @param config
   *          the configuration
   * @return the client
   */
  static JomnigateMongoClient createShared(final Vertx vertx, final JsonObject config) {
    return new JomnigateMongoClient(vertx, config, DEFAULT_POOL_NAME);
  }

  public Future<List<JsonObject>> aggregateOnCollection(final String collectionName, final List<Bson> pipeLine) {
    Promise<List<JsonObject>> promise = Promise.promise();
    Context context = vertx.getOrCreateContext();
    List<JsonObject> resultList = new ArrayList<>();
    promise.complete(resultList);
//    getCollection(collectionName).aggregate(pipeLine, JsonObject.class).into(resultList,
//        (result, error) -> context.runOnContext(v -> {
//          if (error != null) {
//            promise.fail(error);
//          } else {
//            promise.complete(resultList);
//          }
//        }));
    return promise.future();
  }

}
