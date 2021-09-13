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

import cn.vtohru.orm.annotation.field.*;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.datastore.IColumnInfo;

import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractProperty<T> implements IProperty<T> {
  /**
   * Annotations which shall be checked for a field definition
   */
  protected static final List<Class<? extends Annotation>> FIELD_ANNOTATIONS = Arrays.asList(Id.class, Property.class, Ignore.class);
  private IMapper<?> mapper;
  /**
   * 
   */
  public AbstractProperty(IMapper<?> mapper) {
    this.mapper = mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.IField#getMapper()
   */
  @Override
  public final IMapper<?> getMapper() {
    return mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.IField#getColumnInfo()
   */
  @Override
  public IColumnInfo getColumnInfo() {
    return getMapper().getTableInfo().getColumnInfo(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.IField#isIdField()
   */
  @Override
  public boolean isIdField() {
    return hasAnnotation(Id.class);
  }


  @Override
  public String toString() {
    return getFullName();
  }

}
