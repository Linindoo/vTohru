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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.datastore.IColumnHandler;
import cn.vtohru.orm.mapping.datastore.IColumnInfo;
import cn.vtohru.orm.mapping.datastore.ITableInfo;

/**
 * A default implementation for {@link ITableInfo}
 * 
 * @author Michael Remme
 * 
 */

public abstract class DefaultTableInfo implements ITableInfo {
  private String name;
  private Map<String, IColumnInfo> colsByJavaFieldName = new HashMap<String, IColumnInfo>();
  private Map<String, IColumnInfo> colsByColumnName = new HashMap<String, IColumnInfo>();

  /**
   * 
   * @param mapper
   *          the mapper to be used to create the instance
   */
  public DefaultTableInfo(IMapper mapper) {
    if (mapper.getEntity() == null || mapper.getEntity().name().equals("")) {
      this.name = mapper.getMapperClass().getSimpleName();
    } else {
      this.name = mapper.getEntity().name();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.ITableInfo#getName()
   */
  @Override
  public final String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.mapping.datastore.ITableInfo#createColumn(cn.vtohru.orm.mapping
   * .IField, cn.vtohru.orm.mapping.datastore.IColumnHandler)
   */
  @Override
  public final void createColumnInfo(IProperty field, IColumnHandler columnHandler) {
    IColumnInfo ci = generateColumnInfo(field, columnHandler);
    addColumnInfo(field, ci);
    addColumnInfo(ci);
  }

  /**
   * Add an {@link IColumnInfo} so that it can be looked up by the {@link IProperty}.
   * 
   * @param field
   *          the instance of {@link IProperty} to be used as lookup
   * @param ci
   *          the {@link IColumnInfo} to be looked up
   */
  protected void addColumnInfo(IProperty field, IColumnInfo ci) {
    colsByJavaFieldName.put(field.getName().toLowerCase(), ci);
  }

  /**
   * Add an {@link IColumnInfo} so that it can be looked up by the name of the column.
   * 
   * @param ci
   *          the {@link IColumnInfo} to be looked up
   */
  protected void addColumnInfo(IColumnInfo ci) {
    colsByColumnName.put(ci.getName().toLowerCase(), ci);
  }

  /**
   * Generate the instance of {@link IColumnInfo} which is used from this implementation of {@link ITableInfo}
   * 
   * @param field
   *          the field to be mapped
   * @param columnHandler
   *          the instance of {@link IColumnHandler}
   * @return the implementation of {@link IColumnInfo}
   */
  protected abstract IColumnInfo generateColumnInfo(IProperty field, IColumnHandler columnHandler);

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.ITableInfo#getColumnInfo(java.lang.String)
   */
  @Override
  public final IColumnInfo getColumnInfo(IProperty field) {
    return colsByJavaFieldName.get(field.getName().toLowerCase());
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.datastore.ITableInfo#getColumnInfo(java.lang.String)
   */
  @Override
  public final IColumnInfo getColumnInfo(String columnName) {
    return colsByColumnName.get(columnName.toLowerCase());
  }

  @Override
  public List<String> getColumnNames() {
    return new ArrayList<String>(colsByColumnName.keySet());
  }

}
