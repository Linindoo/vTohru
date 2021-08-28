/*-
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

import com.google.common.collect.ImmutableSet;
import cn.vtohru.orm.annotation.Index;
import cn.vtohru.orm.dataaccess.query.ISearchCondition;
import cn.vtohru.orm.mapping.IIndexDefinition;
import cn.vtohru.orm.mapping.IIndexFieldDefinition;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IndexOption;
import cn.vtohru.orm.mongo.dataaccess.MongoQueryExpression;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for MongoDb
 * 
 * @author Michael Remme
 * 
 */
public final class MongoUtil {

  private MongoUtil() {

  }

  /**
   * Request for existing collections
   * 
   * @param ds
   *          the datastore to be used
   * @param result
   *          returns the complete result with detailed information of every collection
   */
  public static final void getCollections(final MongoDataStore ds, final Handler<AsyncResult<JsonObject>> result) {
    JsonObject jsonCommand = new JsonObject().put("listCollections", 1);
    ((MongoClient) ds.getClient()).runCommand("listCollections", jsonCommand, result);
  }

  /**
   * Request for existing collections as names
   * 
   * @param ds
   *          the datastore to be used
   * @param result
   *          returns the complete result with detailed information of every collection
   */
  public static final void getCollectionNames(final MongoDataStore ds,
      final Handler<AsyncResult<List<String>>> result) {
    getCollections(ds, res -> {
      if (res.failed()) {
        result.handle(Future.failedFuture(res.cause()));
      } else {
        List<String> returnList = new ArrayList<>();
        JsonArray array = res.result().getJsonObject("cursor").getJsonArray("firstBatch");
        array.forEach(jo -> returnList.add(((JsonObject) jo).getString("name")));
        result.handle(Future.succeededFuture(returnList));
      }
    });
  }

  /**
   * Call to explicitly create a collection inside the database
   * 
   * @param ds
   * @param collection
   * @param handler
   */
  public static void createCollection(final MongoDataStore ds, final String collection,
      final Handler<AsyncResult<JsonObject>> handler) {
    JsonObject jsonCommand = new JsonObject().put("create", collection);
    ((MongoClient) ds.getClient()).runCommand("create", jsonCommand, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(new RuntimeException(result.cause())));
      } else {
        handler.handle(result);
      }
    });
  }

  /**
   * List all existing indexes for the given collection
   * 
   * @param ds
   * @param collection
   * @param handler
   */
  public static void getIndexes(final MongoDataStore ds, final String collection,
      final Handler<AsyncResult<JsonObject>> handler) {
    JsonObject jsonCommand = new JsonObject().put("listIndexes", collection);
    ((MongoClient) ds.getClient()).runCommand("listIndexes", jsonCommand, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(new RuntimeException(result.cause())));
      } else {
        handler.handle(result);
      }
    });
  }

  /**
   * Request for existing indexes as names
   * 
   * @param ds
   * @param collection
   * @param handler
   */
  public static final void getIndexNames(final MongoDataStore ds, final String collection,
      final Handler<AsyncResult<List<String>>> result) {
    getIndexes(ds, collection, res -> {
      if (res.failed()) {
        result.handle(Future.failedFuture(res.cause()));
      } else {
        List<String> returnList = new ArrayList<>();
        JsonArray array = res.result().getJsonObject("cursor").getJsonArray("firstBatch");
        array.forEach(jo -> returnList.add(((JsonObject) jo).getString("name")));
        result.handle(Future.succeededFuture(returnList));
      }
    });
  }

  /**
   * Create indexes which are defined by the given {@link Index}
   * 
   * @param dataStore
   *          the datastore
   *          the index definition
   * @param handler
   *          the handler to be informed with the result of the index creation
   */
  public static final void createIndexes(final MongoDataStore dataStore, final IMapper<?> mapper,
      final Handler<AsyncResult<JsonObject>> handler) {
    String collection = mapper.getTableInfo().getName();
    ImmutableSet<IIndexDefinition> indexDefinitions = mapper.getIndexDefinitions();
    JsonObject indexCommand = new JsonObject().put("createIndexes", collection);

    CompositeFuture.all(indexDefinitions.stream().map(def -> createIndexDefinition(def, mapper, dataStore))
        .collect(Collectors.toList())).onComplete(rIndex -> {
          if (rIndex.failed())
            handler.handle(Future.failedFuture(rIndex.cause()));
          else {
            indexCommand.put("indexes", rIndex.result().list());
            ((MongoClient) dataStore.getClient()).runCommand("createIndexes", indexCommand, rCreate -> {
              if (rCreate.failed()) {
                handler.handle(Future.failedFuture(new RuntimeException(rCreate.cause())));
              } else {
                handler.handle(rCreate);
              }
            });
          }
        });
  }

  private static Future<JsonObject> createIndexDefinition(final IIndexDefinition indexDefinition,
      final IMapper<?> mapper, final MongoDataStore dataStore) {
    Promise<JsonObject> promise = Promise.promise();
    JsonObject idxObject = new JsonObject();
    try {
      idxObject.put("name", indexDefinition.getName());
      JsonObject keyObject = new JsonObject();
      idxObject.put("key", keyObject);

      for (IIndexFieldDefinition field : indexDefinition.getFields()) {
        addIndexField(keyObject, field);
      }
    } catch (Exception e) {
      promise.fail(e);
      return promise.future();
    }
    addIndexOptions(idxObject, indexDefinition.getIndexOptions(), mapper, dataStore, promise);
    return promise.future();
  }

  private static void addIndexField(final JsonObject keyObject, final IIndexFieldDefinition field) {
    keyObject.put(field.getName(), field.getType().toIndexValue());
  }

  private static void addIndexOptions(final JsonObject indexDef,
      final List<cn.vtohru.orm.mapping.IndexOption> indexOptions, final IMapper<?> mapper,
      final MongoDataStore dataStore, final Handler<AsyncResult<JsonObject>> handler) {
    @SuppressWarnings("rawtypes")
    List<Future> futures = new ArrayList<>();
    for (IndexOption option : indexOptions) {
      Object value = option.getValue();
      Promise<Void> future = Promise.promise();
      futures.add(future.future());

      switch (option.getFeature()) {
        case UNIQUE:
          indexDef.put("unique", value);
          future.complete();
          break;
        case PARTIAL_FILTER_EXPRESSION:
          convertFilterExpression((String) value, indexDef, mapper, dataStore, future);
          break;
        case SPARSE:
          indexDef.put("sparse", value);
          future.complete();
          break;
        default:
          future.fail(new IllegalArgumentException("Unknown IndexFeature: " + option.getFeature()));
      }
    }
    CompositeFuture.all(futures).onComplete(result -> handler.handle(result.map(indexDef)));
  }

  private static void convertFilterExpression(final String filterExpression, final JsonObject indexDef,
      final IMapper<?> mapper, final MongoDataStore dataStore, final Handler<AsyncResult<Void>> future) {
    ISearchCondition condition;
    try {
      condition = dataStore.getJacksonMapper().readValue(filterExpression, ISearchCondition.class);
    } catch (IOException e) {
      future.handle(Future.failedFuture(e));
      return;
    }

    MongoQueryExpression mongoQueryExpression = new MongoQueryExpression();
    mongoQueryExpression.setMapper(mapper);
    mongoQueryExpression.buildSearchCondition(condition, null, result -> {
      future.handle(result.map(v -> {
        indexDef.put("partialFilterExpression", mongoQueryExpression.getQueryDefinition());
        return null;
      }));
    });
  }
}
