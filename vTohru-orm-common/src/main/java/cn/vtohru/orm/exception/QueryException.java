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
package cn.vtohru.orm.exception;

import cn.vtohru.orm.dataaccess.query.impl.IQueryExpression;

/**
 * An exception which occured during a query execution
 * 
 * @author Michael Remme
 * 
 */
public class QueryException extends RuntimeException {

  public QueryException(Throwable cause) {
    super(cause);
  }

  public QueryException(IQueryExpression mq, Throwable cause) {
    super(mq.toString(), cause);
  }

}
