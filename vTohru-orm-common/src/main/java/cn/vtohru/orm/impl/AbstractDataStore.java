/*-
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package cn.vtohru.orm.impl;

import java.util.HashMap;
import java.util.Map;

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.annotation.KeyGenerator;
import cn.vtohru.orm.exception.UnsupportedKeyGenerator;
import cn.vtohru.orm.init.DataStoreSettings;
import cn.vtohru.orm.mapping.IDataStoreSynchronizer;
import cn.vtohru.orm.mapping.IKeyGenerator;
import cn.vtohru.orm.mapping.IMapperFactory;
import cn.vtohru.orm.mapping.IStoreObjectFactory;
import cn.vtohru.orm.mapping.ITriggerContextFactory;
import cn.vtohru.orm.mapping.datastore.ITableGenerator;
import cn.vtohru.orm.mapping.impl.TriggerContextFactory;
import cn.vtohru.orm.mapping.impl.keygen.DebugGenerator;
import cn.vtohru.orm.mapping.impl.keygen.DefaultKeyGenerator;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * An abstract implementation of {@link IDataStore}
 *
 * @author Michael Remme
 *
 * @param <S>
 *          the type of the {@link IStoreObjectFactory} like Json, String etc.
 * @param <U>
 *          the format used by the underlaing {@link IDataStoreSynchronizer}
 *
 */

public abstract class AbstractDataStore<S, U> implements IDataStore<S, U> {
  private VerticleApplicationContext context;
  private JsonObject properties;
  private IMapperFactory mapperFactory;
  private IStoreObjectFactory<S> storeObjectFactory;
  private ITableGenerator tableGenerator;
  private IDataStoreSynchronizer<U> dataStoreSynchronizer;
  private Map<String, IKeyGenerator> keyGeneratorMap = new HashMap<>();
  private ITriggerContextFactory triggerContextFactory = new TriggerContextFactory();
  private Map<String, IEncoder> encoderMap = new HashMap<>();
  private int defaultQueryLimit;
  private DataStoreSettings settings;

  /**
   * Create a new instance. The possible properties are defined by its concete implementation
   *
   *          the instance if {@link Vertx} used
   * @param properties
   *          the properties by which the new instance is created
   */
  public AbstractDataStore(VerticleApplicationContext context, JsonObject properties, DataStoreSettings settings) {
    this.context = context;
    this.properties = properties;
    initSupportedKeyGenerators();
    defaultQueryLimit = properties.getInteger(DEFAULT_QUERY_LIMIT, 500);
    this.settings = settings;
  }

  /**
   * @return the storeObjectFactory
   */
  @Override
  public IStoreObjectFactory<S> getStoreObjectFactory() {
    return storeObjectFactory;
  }

  /**
   * @param storeObjectFactory
   *          the storeObjectFactory to set
   */
  public final void setStoreObjectFactory(IStoreObjectFactory<S> storeObjectFactory) {
    this.storeObjectFactory = storeObjectFactory;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#getMapperFactory()
   */
  @Override
  public final IMapperFactory getMapperFactory() {
    return mapperFactory;
  }

  /**
   * @param mapperFactory
   *          the mapperFactory to set
   */
  protected final void setMapperFactory(IMapperFactory mapperFactory) {
    this.mapperFactory = mapperFactory;
  }

  /**
   * @return the tableGenerator
   */
  @Override
  public final ITableGenerator getTableGenerator() {
    return tableGenerator;
  }

  /**
   * @param tableGenerator
   *          the tableGenerator to set
   */
  public final void setTableGenerator(ITableGenerator tableGenerator) {
    this.tableGenerator = tableGenerator;
  }

  /**
   * @return the dataStoreSynchronizer
   */
  @Override
  public final IDataStoreSynchronizer<U> getDataStoreSynchronizer() {
    return dataStoreSynchronizer;
  }

  /**
   * @param dataStoreSynchronizer
   *          the dataStoreSynchronizer to set
   */
  public final void setDataStoreSynchronizer(IDataStoreSynchronizer<U> dataStoreSynchronizer) {
    this.dataStoreSynchronizer = dataStoreSynchronizer;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#getProperties()
   */
  @Override
  public JsonObject getProperties() {
    return properties;
  }

  /**
   * Add an {@link IKeyGenerator} supported by the current instance
   *
   * @param generator
   */
  private void addSupportedKeyGenerator(IKeyGenerator generator) {
    keyGeneratorMap.put(generator.getName(), generator);
  }

  /**
   * Define all {@link IKeyGenerator}, which are supported by the current instance by using the method
   * {@link #addSupportedKeyGenerator(IKeyGenerator)}
   */
  private void initSupportedKeyGenerators() {
    Vertx vertx = this.context.getVertx();
    DebugGenerator generator = new DebugGenerator(this);
    addSupportedKeyGenerator(generator);
    DefaultKeyGenerator defaultGenerator = new DefaultKeyGenerator(this, vertx);
    addSupportedKeyGenerator(defaultGenerator);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#getKeyGenerator(java.lang.String)
   */
  @Override
  public final IKeyGenerator getKeyGenerator(String generatorName) {
    if (generatorName.equals(KeyGenerator.NULL_KEY_GENERATOR)) {
      return null;
    }
    if (keyGeneratorMap.containsKey(generatorName)) {
      return keyGeneratorMap.get(generatorName);
    }
    throw new UnsupportedKeyGenerator("This generator is not supported by the current datastore: " + generatorName);
  }

  @Override
  public VerticleApplicationContext getContext() {
    return this.context;
  }

  /**
   * @return the triggerContextFactory
   */
  @Override
  public final ITriggerContextFactory getTriggerContextFactory() {
    return triggerContextFactory;
  }

  /**
   * @param triggerContextFactory
   *          the triggerContextFactory to set
   */
  @Override
  public final void setTriggerContextFactory(ITriggerContextFactory triggerContextFactory) {
    this.triggerContextFactory = triggerContextFactory;
  }

  /**
   * The map contains the {@link IEncoder} which are defined for the current instance
   *
   * @return the encoderMap
   */
  public Map<String, IEncoder> getEncoderMap() {
    return encoderMap;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#getEncoder(java.lang.String)
   */
  @Override
  public IEncoder getEncoder(String name) {
    return getEncoderMap().get(name);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.IDataStore#getDefaultQueryLimit()
   */
  @Override
  public int getDefaultQueryLimit() {
    return defaultQueryLimit;
  }

  /**
   * @return the settings
   */
  @Override
  public DataStoreSettings getSettings() {
    return settings;
  }

}
