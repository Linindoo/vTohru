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
import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.IDataStoreMetaData;
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
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * An {@link IDataStore} which is dealing with {@link MongoClient}
 *
 * @author Michael Remme
 *
 */

@Verticle
public class MongoDataStore extends AbstractDataStore<JsonObject, JsonObject> {
  /**
   * The minimal version of MongoDb, which is expected by the current implementation
   */
  public static final String EXPECTED_VERSION_STARTS_WITH = "3.2.";

  /**
   * The name of the property, which describes the database to be used
   */
  public static final String DATABASE_NAME = "db_name";
  private MongoClient client;
  private MongoMetaData metaData;

  public MongoDataStore(VerticleApplicationContext context) {
    super(context, new JsonObject(), new DataStoreSettings());
    JsonObject config = context.getVertx().getOrCreateContext().config();
    JsonObject mongo = config.getJsonObject("mongo", new JsonObject());
    this.client = MongoClient.create(context.getVertx(), mongo);
    metaData = new MongoMetaData(this);
    MongoMapperFactory mf = new MongoMapperFactory(this);
    setMapperFactory(mf);
    MongoStoreObjectFactory storeObjectFactory = new MongoStoreObjectFactory();
    setStoreObjectFactory(storeObjectFactory);
    MongoDataStoreSynchronizer dataStoreSynchronizer = new MongoDataStoreSynchronizer(this);
    setDataStoreSynchronizer(dataStoreSynchronizer);
    setTableGenerator(new MongoTableGenerator());
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new MongoQuery<>(mapper, this);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return new MongoWrite<>(mapper, this);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#createDelete(java.lang.Class)
   */
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

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#getMetaData()
   */
  @Override
  public IDataStoreMetaData getMetaData() {
    return metaData;
  }

  /**
   * @return the database
   */
  @Override
  public String getDatabase() {
    return getProperties().getString(DATABASE_NAME).toLowerCase();
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> resultHandler) {
    try {
      client.close();
      resultHandler.handle(Future.succeededFuture());
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(new RuntimeException(e)));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public IKeyGenerator getDefaultKeyGenerator() {
    String genName = getProperties().getString(IKeyGenerator.DEFAULT_KEY_GENERATOR);
    return genName == null ? null : getKeyGenerator(genName);
  }

  public ObjectMapper getJacksonMapper() {
    return Json.mapper;
  }

}
