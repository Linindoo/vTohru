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
import cn.vtohru.orm.typehandler.ITypeHandler;
import cn.vtohru.orm.typehandler.ITypeHandlerFactory;
import cn.vtohru.orm.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * An abstract implementation of {@link ITypeHandler} dealing with numeric data
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractNumericTypeHandler extends AbstractTypeHandler {

  /**
   * @param typeHandlerFactory
   * @param classesToDeal
   */
  public AbstractNumericTypeHandler(ITypeHandlerFactory typeHandlerFactory, Class<?>... classesToDeal) {
    super(typeHandlerFactory, classesToDeal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * cn.vtohru.orm.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    String s = source == null || ((String) source).trim().hashCode() == 0 ? "0" : ((String) source).trim();
    success(createInstance(s), resultHandler);
  }

  /**
   * Create the suitable instance from the given String
   * 
   * @param value
   *          the value to be used. It is guarnteed, that this is not null
   * @return the created, suitable instance
   */
  protected abstract Object createInstance(String value);

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.typehandler.ITypeHandler#intoStore(java.lang.Object)
   */
  @Override
  public final void intoStore(Object source, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? "0" : ((Number) source).toString(), resultHandler);
  }
}
