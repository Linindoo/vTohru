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

import java.lang.reflect.Field;

import cn.vtohru.orm.exception.PropertyAccessException;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.IPropertyAccessor;

/**
 * An accessor onto a public field of a java object
 *
 * @author Michael Remme
 *
 */

public class JavaFieldAccessor implements IPropertyAccessor {
  private String name;
  private Field field;

  /**
   *
   * @param field
   *          the underlaying {@link IProperty}
   */
  public JavaFieldAccessor(Field field) {
    this.name = field.getName();
    this.field = field;
  }

  @Override
  public Object readData(Object record) {
    try {
      return record == null ? null : field.get(record);
    } catch (Exception e) {
      throw new PropertyAccessException("Cannot read data from property " + name, e);
    }
  }

  @Override
  public void writeData(Object record, Object data) {
    try {
      field.set(record, data);
    } catch (Exception e) {
      throw new PropertyAccessException("Cannot write data from property " + name, e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IPropertyAccessor#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

}
