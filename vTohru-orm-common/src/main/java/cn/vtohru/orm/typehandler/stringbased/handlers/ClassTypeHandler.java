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

import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.typehandler.AbstractTypeHandler;
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

public class ClassTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public ClassTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Class.class);
  }

  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    try {
      success(source == null ? source : Class.forName((String) source), resultHandler);
    } catch (ClassNotFoundException e) {
      fail(e, resultHandler);
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void intoStore(Object source, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : ((Class) source).getName(), resultHandler);
  }

}
