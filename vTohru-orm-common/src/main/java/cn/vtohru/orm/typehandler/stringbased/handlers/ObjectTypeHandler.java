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

public class ObjectTypeHandler extends AbstractTypeHandler {
  private static final Class<?>[] handleClass = { Object.class };

  /**
   *
   * @param typeHandlerFactory
   *          th eparent {@link ITypeHandlerFactory}
   */
  public ObjectTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, handleClass);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.typehandler.ITypeHandler#fromStore(java.lang.Object)
   */
  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source, resultHandler);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.typehandler.ITypeHandler#intoStore(java.lang.Object)
   */
  @Override
  public void intoStore(Object source, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source, resultHandler);
  }

}
