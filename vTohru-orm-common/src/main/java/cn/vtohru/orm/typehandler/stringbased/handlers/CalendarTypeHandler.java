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

import java.util.Calendar;

import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.typehandler.ITypeHandlerFactory;
import cn.vtohru.orm.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class CalendarTypeHandler extends AbstractDateTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public CalendarTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Calendar.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * cn.vtohru.orm.mapping.IField)
   */
  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source != null) {
      success(parseDateTime((String) source), resultHandler);
    } else
      success(null, resultHandler);

  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * cn.vtohru.orm.mapping.IField)
   */
  @Override
  public void intoStore(Object source, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : formatDateTime((Calendar) source), resultHandler);
  }

}
