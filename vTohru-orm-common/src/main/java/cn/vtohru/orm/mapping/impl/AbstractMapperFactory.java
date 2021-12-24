/*
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
package cn.vtohru.orm.mapping.impl;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IMapperFactory;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract implementation of IMapperFactory
 *
 * @author Michael Remme
 */
public abstract class AbstractMapperFactory implements IMapperFactory {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractMapperFactory.class);

  private final IDataStore<?, ?> datastore;
  private Map<String, IMapper<?>> mappedClasses = new HashMap<>();
  private final Object so = new Object();

  /**
   * @param dataStore
   */
  public AbstractMapperFactory(final IDataStore<?, ?> dataStore) {
    this.datastore = dataStore;
  }

  @Override
  public void reset() {
    synchronized (so) {
      mappedClasses = new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IMapperFactory#getDataStore()
   */
  @Override
  public final IDataStore<?, ?> getDataStore() {
    return datastore;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final <T> IMapper<T> getMapper(final Class<T> mapperClass) {
    String className = mapperClass.getName();
    if (mappedClasses.containsKey(className)) {
      return (IMapper<T>) mappedClasses.get(className);
    }
    if (!mapperClass.isAnnotationPresent(Entity.class))
      throw new UnsupportedOperationException(String
          .format("The class %s is no mappable entity. Add the annotation Entity to the class", mapperClass.getName()));

    IMapper<T> mapper = createMapperBlocking(mapperClass);

    synchronized (so) {
      Map<String, IMapper<?>> tmpMap = new HashMap<>(mappedClasses);
      tmpMap.put(className, mapper);
      mappedClasses = tmpMap;
    }
    return mapper;
  }

  private final <T> IMapper<T> createMapperBlocking(final Class<T> mapperClass) {
    IMapper<T> mapper = createMapper(mapperClass);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("post mapping for " + mapperClass.getName());
    }
    return mapper;
  }
  @Override
  public final boolean isMapper(final Class<?> mapperClass) {
    if (mappedClasses.containsKey(mapperClass.getName()) || mapperClass.isAnnotationPresent(Entity.class))
      return true;
    return false;
  }

  /**
   * Creates a new instance of IMapper for the given class
   *
   * @param mapperClass
   *          the class to be mapped
   * @return the mapper
   */
  protected abstract <T> IMapper<T> createMapper(Class<T> mapperClass);

}
