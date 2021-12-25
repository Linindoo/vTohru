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
package cn.vtohru.orm.dataaccess.query;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.IAccessResult;
import cn.vtohru.orm.dataaccess.query.impl.IQueryExpression;
import cn.vtohru.orm.mapping.IMapper;

import java.util.List;
import java.util.Optional;

public interface IQueryResult<E> extends IAccessResult {

  /**
   * Get the {@link IDataStore} by which the current instance was created
   *
   * @return
   */
  IDataStore getDataStore();

  /**
   * Get the underlaying {@link IMapper}
   *
   * @return
   */
  IMapper<E> getMapper();

  /**
   * Get the original query, which was executed in the datastore
   *
   * @return the query
   */
  IQueryExpression getOriginalQuery();

  /**
   * If the {@link IQuery#setReturnCompleteCount(boolean)} is set to true and {@link IQuery#setLimit(int)} is set with a
   * value > 0, then here will be returned the complete number of fitting records
   * Otherwise the length of the current selection is returned
   *
   * @return the complete number of records
   */
  long getCompleteResult();

  List<E> result();

  Optional<E> first();

  int size();


}
