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

import java.math.BigDecimal;

import cn.vtohru.orm.typehandler.ITypeHandlerFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class BigDecimalTypeHandler extends AbstractDecimalTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public BigDecimalTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, BigDecimal.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.typehandler.stringbased.handlers.AbstractDecimalTypeHandler#createInstance(java.
   * lang.String)
   */
  @Override
  protected Object createInstance(String value) {
    return new BigDecimal(value);
  }

}
