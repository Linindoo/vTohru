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

import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.IObjectReference;

/**
 * ObjectReference is used as carrier when reading objects from the datastore, which contain referenced objects
 * 
 * @author Michael Remme
 * 
 */
public class ObjectReference implements IObjectReference {
  private IProperty field;
  private Object dbSource;

  /**
   * Create a new instance
   * 
   * @param field
   *          the underlaying field, where the object shall be stored
   * @param dbSource
   *          the value from the datastore
   * @param mapperClass
   *          the mapper class
   */
  public ObjectReference(IProperty field, Object dbSource) {
    this.field = field;
    this.dbSource = dbSource;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.impl.IObjectReference#getField()
   */
  @Override
  public IProperty getField() {
    return field;
  }

  @Override
  public Object getDbSource() {
    return dbSource;
  }
}
