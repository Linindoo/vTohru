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
package cn.vtohru.orm.typehandler.stringbased.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.typehandler.ITypeHandler;
import cn.vtohru.orm.typehandler.ITypeHandlerFactory;
import cn.vtohru.orm.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * An {@link ITypeHandler} which is dealing {@link Date}. Currently its only dealing with the long value of a Date.
 * Could be modified to the use of ISO-8601 ( "$date", "1937-09-21T00:00:00+00:00" ) and the use of a date scanner (
 * eutil ). Question is: is it needed to store a Date / Time in a readable format in Mongo?
 * 
 * @author Michael Remme
 * 
 */

public class TimeTypeHandler extends AbstractDateTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public TimeTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Time.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.typehandler.ITypeHandler#fromStore(java.lang.Object)
   */
  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Object result = null;
    if (source != null) {
      try {
        Constructor constr = getConstructor(field, cls, long.class);
        Calendar cal = parseTime((String) source);
        result = constr.newInstance(cal.getTimeInMillis());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        fail(e, resultHandler);
        return;
      }
    }
    success(result, resultHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * cn.vtohru.orm.mapping.IField)
   */
  @Override
  public void intoStore(Object source, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Calendar cal = Calendar.getInstance();
    cal.setTime((Date) source);
    success(source == null ? source : formatTime(cal), resultHandler);
  }

}
