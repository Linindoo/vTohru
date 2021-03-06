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
import cn.vtohru.orm.mapping.datastore.IColumnHandler;
import cn.vtohru.orm.mapping.datastore.ITableGenerator;

/**
 * Default implementation of ITableGenerator
 * 
 * @author Michael Remme
 * 
 */

public abstract class DefaultTableGenerator implements ITableGenerator {
  /**
   * If for a class a {@link IColumnHandler} was requested and found, it is cached by here with the class to handle as
   * key
   */
  private final Map<Class<?>, IColumnHandler> cachedColumnHandler = new HashMap<Class<?>, IColumnHandler>();
  protected static final List<IColumnHandler> definedColumnHandlers = new ArrayList<IColumnHandler>();

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.mapping.datastore.ITableGenerator#getColumnHandler(de.braintags.vertx.util.
   * pojomapper .mapping.IField)
   */
  @Override
  public IColumnHandler getColumnHandler(IProperty field) {
    Class<?> fieldClass = field.getType();
    if (cachedColumnHandler.containsKey(fieldClass))
      return cachedColumnHandler.get(fieldClass);
    IColumnHandler handler = examineMatch(field);
    if (handler == null)
      handler = getDefaultColumnHandler();
    cachedColumnHandler.put(fieldClass, handler);
    return handler;
  }

  /**
   * Checks for a valid TypeHandler by respecting graded results
   * 
   * @param field
   * @return
   */
  private IColumnHandler examineMatch(IProperty field) {
    IColumnHandler returnHandler = null;
    List<IColumnHandler> ths = getDefinedColumnHandlers();
    for (IColumnHandler ch : ths) {
      short matchResult = ch.matches(field);
      switch (matchResult) {
      case 2:
        return ch;

      case 1:
        returnHandler = ch;
        break;

      default:
        break;
      }
    }
    return returnHandler;
  }

  /**
   * This implementation returns null
   * 
   * @return the defined default handler or null, if none defined
   */
  public IColumnHandler getDefaultColumnHandler() {
    return null;
  }

  /**
   * Get all defined {@link IColumnHandler} defined for the current instance
   * 
   * @return
   */
  public List<IColumnHandler> getDefinedColumnHandlers() {
    return definedColumnHandlers;
  }

}
