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

package cn.vtohru.orm.mapping.datastore.impl;

import cn.vtohru.orm.annotation.field.Property;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.datastore.IColumnHandler;
import cn.vtohru.orm.mapping.datastore.IColumnInfo;
import io.micronaut.core.annotation.AnnotationValue;

/**
 * Default implementation of IColumnInfo which contains the needed properties and above the interface getter methods for
 * column properties either the getter methods for those implementations, which will reread information from some
 * database meta data
 * 
 * @author Michael Remme
 * 
 */

public abstract class DefaultColumnInfo implements IColumnInfo {
  private final String colName;
  private IColumnHandler columnHandler;

  private String type = Property.UNDEFINED_COLUMN_TYPE;
  private int length = Property.UNDEFINED_INTEGER;
  private int scale = Property.UNDEFINED_INTEGER;
  private int precision = Property.UNDEFINED_INTEGER;

  private boolean nullable = true;
  private boolean id = false;

  /**
   * Constructor to create an instance with the given column name
   * 
   * @param colName
   *          the name of the column
   */
  protected DefaultColumnInfo(final String colName) {
    this.colName = colName;
  }

  /**
   * Initializes an instance by using a defined {@link Property} and adds the defined {@link IColumnHandler}
   * 
   * @param field
   *          the {@link IProperty} to be used for init
   * @param columnHandler
   *          the {@link IColumnHandler} to be used
   */
  public DefaultColumnInfo(final IProperty field, final IColumnHandler columnHandler) {
    this.columnHandler = columnHandler;
    colName = computePropertyName(field);
    init(field, columnHandler);
  }

  protected String computePropertyName(final IProperty field) {
    AnnotationValue<Property> annotation = field.getAnnotation(Property.class);
    if (annotation != null) {
      String propName =  annotation.stringValue("value").orElse("");
      if (!propName.equals(Property.UNDEFINED_COLUMN_NAME))
        return propName;
    }
    return field.getName();
  }

  protected void init(final IProperty field, final IColumnHandler columnHandler) {
    AnnotationValue<Property> annotation = field.getAnnotation(Property.class);
    if (annotation != null) {
      type = annotation.get("columnType", String.class).orElse("");
      length = annotation.get("length", Integer.class).orElse(0);
      scale = annotation.get("scale", Integer.class).orElse(0);
      precision = annotation.get("precision", Integer.class).orElse(0);
      nullable = annotation.get("nullable", Boolean.class).orElse(false);
    }
    if (field.getMapper().getIdInfo().getIndexedField() == field)
      id = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.IColumnInfo#getName()
   */
  @Override
  public String getName() {
    return colName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.IColumnInfo#getFieldGenerator()
   */
  @Override
  public IColumnHandler getColumnHandler() {
    return columnHandler;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.IColumnInfo#getType()
   */
  @Override
  public String getType() {
    return type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.IColumnInfo#getLength()
   */
  @Override
  public int getLength() {
    return length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.IColumnInfo#getScale()
   */
  @Override
  public int getScale() {
    return scale;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.IColumnInfo#getPrecision()
   */
  @Override
  public int getPrecision() {
    return precision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.IColumnInfo#isNullable()
   */
  @Override
  public boolean isNullable() {
    return nullable;
  }

  /**
   * @param type
   *          the type to set
   */
  public final void setType(final String type) {
    this.type = type;
  }

  /**
   * @param length
   *          the length to set
   */
  public final void setLength(final int length) {
    this.length = length;
  }

  /**
   * @param scale
   *          the scale to set
   */
  public final void setScale(final int scale) {
    this.scale = scale;
  }

  /**
   * @param precision
   *          the precision to set
   */
  public final void setPrecision(final int precision) {
    this.precision = precision;
  }

  /**
   * @param nullable
   *          the nullable to set
   */
  public final void setNullable(final boolean nullable) {
    this.nullable = nullable;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.IColumnInfo#isId()
   */
  @Override
  public boolean isId() {
    return id;
  }

}
