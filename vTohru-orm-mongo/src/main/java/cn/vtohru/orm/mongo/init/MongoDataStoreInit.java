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
package cn.vtohru.orm.mongo.init;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.exception.InitException;
import cn.vtohru.orm.init.AbstractDataStoreInit;
import cn.vtohru.orm.init.DataStoreSettings;
import cn.vtohru.orm.init.EncoderSettings;
import cn.vtohru.orm.init.IDataStoreInit;
import cn.vtohru.orm.mapping.IKeyGenerator;
import cn.vtohru.orm.mapping.impl.keygen.DefaultKeyGenerator;
import cn.vtohru.orm.mongo.MongoDataStore;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An initializer for {@link MongoDataStore}
 *
 * @author Michael Remme
 *
 */
public class MongoDataStoreInit extends AbstractDataStoreInit implements IDataStoreInit {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(MongoDataStoreInit.class);

  /**
   * The property, which defines the connection string. Something like "mongodb://localhost:27017"
   */
  public static final String CONNECTION_STRING_PROPERTY = "connection_string";

  /**
   * The name of the property to define, wether a local instance of Mongo shall be started. Usable only for debugging
   * and testing purpose
   */
  public static final String START_MONGO_LOCAL_PROP = "startMongoLocal";

  /**
   * If START_MONGO_LOCAL_PROP is set to true, then this defines the local port to be used. Default is 27017
   */
  public static final String LOCAL_PORT_PROP = "localPort";

  /**
   * If READ_CONCERN_PROP is set to a value, then this defines the read concern
   */
  public static final String READ_CONCERN_PROP = "readConcernLevel";

  /**
   * If WRITE_CONCERN_PROP is set to a value, then this defines the write concern
   */
  public static final String WRITE_CONCERN_PROP = "writeConcern";

  /**
   * The default connection to be used, if CONNECTION_STRING_PROPERTY is undefined
   */
  public static final String DEFAULT_CONNECTION = "mongodb://localhost:27017";
  private static final String DEFAULT_KEY_GENERATOR = DefaultKeyGenerator.NAME;

  private MongodExecutable exe;
  boolean startMongoLocal = false;
  private JomnigateMongoClient mongoClient;
  private MongoDataStore mongoDataStore;
  protected int localPort = 27017;

  /**
   * This method creates new datastore settings for mongo by using system properties
   * <UL>
   * <LI>{@value #CONNECTION_STRING_PROPERTY} to set the connection of the database
   * <LI>{@value #START_MONGO_LOCAL_PROP} wether to start mongo temporary local or expecting a running instance
   * <LI>{@value #LOCAL_PORT_PROP} If START_MONGO_LOCAL_PROP is set to true, then this defines the local port to be
   * used. Default is 27017
   * <LI>defaultKeyGenerator to set the name of the default keygenerator to be used
   * </UL>
   *
   * @return
   */
  public static DataStoreSettings createSettings() {
    DataStoreSettings settings = createDefaultSettings();
    String connectionString = System.getProperty(MongoDataStoreInit.CONNECTION_STRING_PROPERTY, null);
    if (connectionString != null) {
      settings.getProperties().put(MongoDataStoreInit.CONNECTION_STRING_PROPERTY, connectionString);
    }
    String sl = System.getProperty(MongoDataStoreInit.START_MONGO_LOCAL_PROP, null);
    if (sl != null) {
      settings.getProperties().put(MongoDataStoreInit.START_MONGO_LOCAL_PROP, sl);
    }
    String localPort = System.getProperty(MongoDataStoreInit.LOCAL_PORT_PROP, null);
    if (localPort != null) {
      settings.getProperties().put(MongoDataStoreInit.LOCAL_PORT_PROP, localPort);
    }
    String keyGenerator = System.getProperty(IKeyGenerator.DEFAULT_KEY_GENERATOR, DEFAULT_KEY_GENERATOR);
    settings.getProperties().put(IKeyGenerator.DEFAULT_KEY_GENERATOR, keyGenerator);
    LOGGER.info("SETTINGS ARE: " + settings.toString());
    return settings;
  }

  /**
   * This method applies the system properties to settings for mongo:
   * <UL>
   * <LI>{@value #CONNECTION_STRING_PROPERTY} to set the connection of the database
   * <LI>{@value #START_MONGO_LOCAL_PROP} wether to start mongo temporary local or expecting a running instance
   * <LI>{@value #LOCAL_PORT_PROP} If START_MONGO_LOCAL_PROP is set to true, then this defines the local port to be
   * used. Default is 27017
   * <LI>defaultKeyGenerator to set the name of the default keygenerator to be used
   * </UL>
   *
   * @return
   */
  public static DataStoreSettings applySystemProperties(final DataStoreSettings settings) {
    String connectionString = System.getProperty(MongoDataStoreInit.CONNECTION_STRING_PROPERTY, null);
    if (connectionString != null) {
      settings.getProperties().put(MongoDataStoreInit.CONNECTION_STRING_PROPERTY, connectionString);
    }
    String sl = System.getProperty(MongoDataStoreInit.START_MONGO_LOCAL_PROP, null);
    if (sl != null) {
      settings.getProperties().put(MongoDataStoreInit.START_MONGO_LOCAL_PROP, sl);
    }
    String localPort = System.getProperty(MongoDataStoreInit.LOCAL_PORT_PROP, null);
    if (localPort != null) {
      settings.getProperties().put(MongoDataStoreInit.LOCAL_PORT_PROP, localPort);
    }
    String keyGenerator = System.getProperty(IKeyGenerator.DEFAULT_KEY_GENERATOR, DEFAULT_KEY_GENERATOR);
    settings.getProperties().put(IKeyGenerator.DEFAULT_KEY_GENERATOR, keyGenerator);
    LOGGER.info("SETTINGS ARE: " + settings.toString());
    return settings;
  }

  /**
   * Helper method which creates the default settings for an instance of {@link MongoDataStore}
   *
   * @return default instance of {@link DataStoreSettings} to init a {@link MongoDataStore}
   */
  public static final DataStoreSettings createDefaultSettings() {
    DataStoreSettings settings = new DataStoreSettings(MongoDataStoreInit.class, "testdatabase");
    settings.getProperties().put(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION);
    settings.getProperties().put(START_MONGO_LOCAL_PROP, "false");
    settings.getProperties().put(LOCAL_PORT_PROP, "27017");
    settings.getProperties().put(SHARED_PROP, "false");
    settings.getProperties().put(HANDLE_REFERENCED_RECURSIVE_PROP, "true");
    settings.getProperties().put(IKeyGenerator.DEFAULT_KEY_GENERATOR, DEFAULT_KEY_GENERATOR);
    EncoderSettings es = new EncoderSettings();
//    es.setName(StandardEncoder.class.getSimpleName());
//    es.setEncoderClass(StandardEncoder.class);
//    es.getProperties().put(StandardEncoder.SALT_PROPERTY, StandardEncoder.generateSalt());
    settings.getEncoders().add(es);
    return settings;
  }

  @Override
  protected void internalInit(final Handler<AsyncResult<IDataStore>> handler) {
    try {
      checkMongoLocal();
      checkShared();
      localPort = startMongoExe(startMongoLocal, localPort);
      initMongoClient(initResult -> {
        if (initResult.failed()) {
          LOGGER.error("could not start mongo client", initResult.cause());
          handler.handle(Future.failedFuture(new InitException(initResult.cause())));
        } else {
//          mongoDataStore = new MongoDataStore(vertx, mongoClient, getConfig(), settings);
          handler.handle(Future.succeededFuture(mongoDataStore));
        }
      });
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  /**
   * Get the instance of MongoClient, which was created during init.
   *
   * @return
   */
  public JomnigateMongoClient getMongoClient() {
    return mongoClient;
  }

  /**
   * If for debugging purpose an internal MongodExecutable was started, it is returned here
   *
   * @return the instance of MongodExecutable or null, if not used
   */
  public MongodExecutable getMongodExecutable() {
    return exe;
  }

  private void initMongoClient(final Handler<AsyncResult<Void>> handler) {
    try {
      LOGGER.info("init MongoClient with " + settings);
      JsonObject jconfig = getConfig();
      JomnigateMongoClient tempClient = shared
          ? JomnigateMongoClient.createShared(vertx, jconfig, settings.getDatabaseName())
          : JomnigateMongoClient.createNonShared(vertx, jconfig);
      if (tempClient == null) {
        handler.handle(Future.failedFuture(new InitException("No MongoClient created")));
      } else {
        tempClient.getCollections(resultHandler -> {
          if (resultHandler.failed()) {
            LOGGER.error("", resultHandler.cause());
            handler.handle(Future.failedFuture(resultHandler.cause()));
          } else {
            List<String> collections = resultHandler.result();
            LOGGER.info(String.format("found %d collections", collections.size()));
            if (isClearDatabaseOnInit()) {
              LOGGER.info("Deleting all collections because 'clearDatabaseOnInit' is true");
              @SuppressWarnings("rawtypes")
              List<Future> futures = new ArrayList<>();
              for (String collection : collections) {
                Promise<Void> future = Promise.promise();
                tempClient.dropCollection(collection, future);
                futures.add(future.future());
              }
              CompositeFuture.join(futures).onComplete(clearResult -> {
                if (clearResult.failed()) {
                  LOGGER.warn("clear database failed ( partially )", new InitException(clearResult.cause()));
                }
                this.mongoClient = tempClient;
                handler.handle(Future.succeededFuture());
              });
            } else {
              this.mongoClient = tempClient;
              handler.handle(Future.succeededFuture());
            }
          }
        });
      }
    } catch (Exception e) {
      handler.handle(Future.failedFuture(new InitException(e)));
    }
  }

  @Override
  protected JsonObject createConfig() {
    JsonObject config = new JsonObject();
    config.put("connection_string", getConnectionString());
    config.put(DBNAME_PROP, getDatabaseName());
    config.put(IDataStore.HANDLE_REFERENCED_RECURSIVE, handleReferencedRecursive);
    String prop = getProperty(IKeyGenerator.DEFAULT_KEY_GENERATOR, null);
    config.put(IKeyGenerator.DEFAULT_KEY_GENERATOR, prop);
    String readConcern = getProperty(READ_CONCERN_PROP, null);
    if (readConcern != null) {
      config.put(READ_CONCERN_PROP, readConcern);
    }
    String writeConcern = getProperty(WRITE_CONCERN_PROP, null);
    if (writeConcern != null) {
      config.put(WRITE_CONCERN_PROP, writeConcern);
    }
    return config;
  }

  /**
   * Get the connection String for the mongo db
   *
   * @return
   */
  private String getConnectionString() {
    if (startMongoLocal) {
      return "mongodb://localhost:" + localPort;
    } else {
      return getProperty(CONNECTION_STRING_PROPERTY, DEFAULT_CONNECTION);
    }
  }

  /**
   * returns true if a local instance of Mongo shall be started
   *
   * @return
   */
  private void checkMongoLocal() {
    startMongoLocal = getBooleanProperty(START_MONGO_LOCAL_PROP, false);
    localPort = getIntegerProperty(LOCAL_PORT_PROP, localPort);
  }

  /**
   * Starts an instance of a local mongo, if startMongoLocal is true
   *
   * @param startMongoLocal
   *          defines wether to start a local instance
   * @param localPort
   *          the port where to start the instance
   */
  private int startMongoExe(final boolean startMongoLocal, final int localPort) {
    int retries = 0;
    final int maxRetries = 5;
    int port = localPort;
    boolean started = false;
    if (!started) {
      throw new InitException("Error starting local mongo");
    }
    return port;
  }


  private boolean internalStartMongoExe(final boolean startMongoLocal, final int localPort) {
    return true;
  }

}
