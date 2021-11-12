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

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.impl.AbstractDataStore;
import cn.vtohru.orm.init.DataStoreSettings;
import cn.vtohru.orm.mapping.IKeyGenerator;
import cn.vtohru.orm.mapping.Json;
import cn.vtohru.orm.mongo.dataaccess.MongoDelete;
import cn.vtohru.orm.mongo.dataaccess.MongoQuery;
import cn.vtohru.orm.mongo.dataaccess.MongoWrite;
import cn.vtohru.orm.mongo.init.MongoDataStoreSynchronizer;
import cn.vtohru.orm.mongo.mapper.MongoMapperFactory;
import cn.vtohru.orm.mongo.mapper.datastore.MongoTableGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.convert.ConversionService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

/**
 * An {@link IDataStore} which is dealing with {@link MongoClient}
 *
 * @author Michael Remme
 *
 */

@Verticle
@GlobalScope
public class MongoDataStore extends AbstractDataStore<JsonObject, JsonObject> {
  /**
   * The minimal version of MongoDb, which is expected by the current implementation
   */
  public static final String EXPECTED_VERSION_STARTS_WITH = "3.2.";

  /**
   * The name of the property, which describes the database to be used
   */
  public static final String DATABASE_NAME = "db_name";
  private static final String MONGO_CONFIG_KEY = "mongo";
  private MongoClient client;
  private VerticleApplicationContext verticleApplicationContext;
  public MongoDataStore(ApplicationContext context) {
    super((VerticleApplicationContext) context, new JsonObject(), new DataStoreSettings());
    verticleApplicationContext = (VerticleApplicationContext) context;
    JsonObject mongoConfig = verticleApplicationContext.getVProperty(MONGO_CONFIG_KEY, JsonObject.class).orElse(new JsonObject());
    Boolean shared = mongoConfig.getBoolean("shared", false);
    this.client = shared ? MongoClient.createShared(verticleApplicationContext.getVertx(), mongoConfig) : MongoClient.create(verticleApplicationContext.getVertx(), mongoConfig);
    MongoMapperFactory mf = new MongoMapperFactory(this);
    setMapperFactory(mf);
    MongoStoreObjectFactory storeObjectFactory = new MongoStoreObjectFactory();
    setStoreObjectFactory(storeObjectFactory);
    MongoDataStoreSynchronizer dataStoreSynchronizer = new MongoDataStoreSynchronizer(this);
    setDataStoreSynchronizer(dataStoreSynchronizer);
    setTableGenerator(new MongoTableGenerator());
    ConversionService.SHARED.addConverter(Long.class, Date.class, (object, targetType, context1) -> Optional.of(new Date(object)));
    ConversionService.SHARED.addConverter(JsonObject.class, Object.class, (jsonObject, targetType, context2) -> Optional.of(jsonObject.mapTo(targetType)));
  }

  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new MongoQuery<>(mapper, this);
  }

  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return new MongoWrite<>(mapper, this);
  }

  @Override
  public <T> IDelete<T> createDelete(Class<T> mapper) {
    return new MongoDelete<>(mapper, this);
  }

  /**
   * Get the underlaying instance of {@link MongoClient}
   *
   * @return the client
   */
  @Override
  public Object getClient() {
    return client;
  }

  /**
   * @return the database
   */
  @Override
  public String getDatabase() {
    return getProperties().getString(DATABASE_NAME).toLowerCase();
  }

  @Override
  public void shutdown(Handler<AsyncResult<Void>> resultHandler) {
    try {
      client.close();
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(new RuntimeException(e)));
    }
  }

  @Override
  public IKeyGenerator getDefaultKeyGenerator() {
    String genName = getProperties().getString(IKeyGenerator.DEFAULT_KEY_GENERATOR);
    return genName == null ? null : getKeyGenerator(genName);
  }

  public ObjectMapper getJacksonMapper() {
    return Json.mapper;
  }

}
