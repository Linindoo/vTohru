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
package cn.vtohru.orm.mongo.mapper;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.exception.MappingException;
import cn.vtohru.orm.mapping.IIdInfo;
import cn.vtohru.orm.mapping.IObjectFactory;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.impl.AbstractMapper;
import cn.vtohru.orm.mapping.impl.DefaultObjectFactory;
import cn.vtohru.orm.mapping.impl.IdInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.Id;

public class MongoMapper<T> extends AbstractMapper<T> {
  private final String keyGeneratorReference;
  private final Class<?> creatorClass;
  private IObjectFactory objectFactory;

  /**
   * @param mapperClass
   * @param mapperFactory
   */
  public MongoMapper(final Class<T> mapperClass, final MongoMapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
    creatorClass = getMapperClass();
    this.keyGeneratorReference = creatorClass.getSimpleName();
    this.objectFactory = new DefaultObjectFactory();
    this.objectFactory.setMapper(this);
    checkIdField();
  }

  /**
   * Currently the id field for mongo must be character
   */
  @SuppressWarnings("rawtypes")
  private void checkIdField() {
    Class idClass = getIdInfo().getField().getType();
    if (!CharSequence.class.isAssignableFrom(idClass))
      throw new UnsupportedOperationException(
          "Currently the id field must be Character based for mongo driver. Class: " + getMapperClass());
  }

  @Override
  protected void validate() {
    JsonTypeInfo ti = getAnnotation(JsonTypeInfo.class);
    if (ti != null) {
      throw new MappingException(
              "If you are setting JsonTypeInfo, you must define Entity.polyClass as well in mapper : "
                      + getMapperClass().getName());
    }
  }

  protected void addMappedField(final String name, final IProperty mf) {
    if (mf.hasAnnotation(Id.class)) {
      if (getIdInfo() != null)
        throw new MappingException("duplicate Id field definition found for mapper " + getMapperClass());
      setIdInfo(createIdInfo(mf));
    }
    if (!mf.isIgnore()) {
      this.getMappedProperties().put(name, mf);
    }
  }

  protected IIdInfo createIdInfo(final IProperty property) {
    return new IdInfo(property);
  }

  @Override
  public IObjectFactory getObjectFactory() {
    return objectFactory;
  }

  @Override
  public boolean handleReferencedRecursive() {
    return this.getMapperFactory().getDataStore().getProperties().getBoolean(IDataStore.HANDLE_REFERENCED_RECURSIVE,
            false);
  }

  @Override
  public String getKeyGeneratorReference() {
    return keyGeneratorReference;
  }
}
