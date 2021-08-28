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

import cn.vtohru.orm.annotation.field.Id;
import cn.vtohru.orm.exception.MappingException;
import cn.vtohru.orm.mapping.IIdInfo;
import cn.vtohru.orm.mapping.IObjectFactory;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.impl.AbstractMapper;
import cn.vtohru.orm.mapping.impl.IdInfo;
import cn.vtohru.orm.mapping.impl.Mapper;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.mongo.mapper.jackson.JacksonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * An extension of {@link Mapper} for use with Mongo
 *
 * @author Michael Remme
 * 
 */

public class MongoMapper<T> extends AbstractMapper<T> {
  private BeanDescription beanDescription;
  private final String keyGeneratorReference;
  private final Class<?> creatorClass;

  /**
   * @param mapperClass
   * @param mapperFactory
   */
  public MongoMapper(final Class<T> mapperClass, final MongoMapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
    creatorClass = getEntity().polyClass() == Object.class ? getMapperClass() : getEntity().polyClass();
    this.keyGeneratorReference = creatorClass.getSimpleName();
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
    boolean polySet = getEntity().polyClass() != Object.class;
    if (ti != null && !polySet) {
      throw new MappingException(
              "If you are setting JsonTypeInfo, you must define Entity.polyClass as well in mapper : "
                      + getMapperClass().getName());
    }
    if (polySet && ti == null) {
      throw new MappingException(
              "If you are setting Entity.polyClass, you must define JsonTypeInfo as well in mapper : "
                      + getMapperClass().getName());
    }
  }
  @Override
  public <U extends Annotation> U getAnnotation(final Class<U> annotationClass) {
    U ann = super.getAnnotation(annotationClass);
    if (ann == null) {
      ann = beanDescription.getClassAnnotations().get(annotationClass);
    }
    return ann;
  }
  public Class<?> getCreatorClass() {
    return creatorClass;
  }

  @Override
  protected void computePersistentFields() {
    ObjectMapper mapper = ((MongoDataStore) getMapperFactory().getDataStore()).getJacksonMapper();
    JavaType type = mapper.constructType(getMapperClass());
    this.beanDescription = mapper.getSerializationConfig().introspect(type);
    List<BeanPropertyDefinition> propertyList = beanDescription.findProperties();
    propertyList.forEach(def -> addMappedField(def.getFullName().getSimpleName(), new JacksonProperty(this, def)));
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
    throw new UnsupportedOperationException("There is no need to call this method for this implementation");
  }

  @Override
  public boolean handleReferencedRecursive() {
    throw new UnsupportedOperationException("There is no need to call this method for this implementation");
  }

  @Override
  public String getKeyGeneratorReference() {
    return keyGeneratorReference;
  }
}
