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

package cn.vtohru.orm.dataaccess.query.impl;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.query.IQueryResult;
import cn.vtohru.orm.mapping.IMapper;

/**
 * An abstract implementation of IQueryResult. Extensions must implement one method to generate single pojos
 *
 * @author Michael Remme
 * @param <T>
 *          the class of the mapper, which builds the result
 */

public abstract class AbstractQueryResult<T> implements IQueryResult<T> {

  private final IMapper<T> mapper;
  private final IDataStore datastore;
  private final IQueryExpression originalQuery;
  private long completeResult;

  /**
   * Constructor
   *  @param datastore
   *          the datastore which was used
   * @param mapper
   *          the mapper which was used
   * @param originalQuery
   */
  @SuppressWarnings("unchecked")
  public AbstractQueryResult(final IDataStore datastore, final IMapper<T> mapper, final IQueryExpression originalQuery) {
    this.datastore = datastore;
    this.mapper = mapper;
    this.originalQuery = originalQuery;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.query.IQueryResult#getDataStore()
   */
  @Override
  public IDataStore getDataStore() {
    return datastore;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.query.IQueryResult#getMapper()
   */
  @Override
  public IMapper<T> getMapper() {
    return mapper;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.query.IQueryResult#getOriginalQuery()
   */
  @Override
  public IQueryExpression getOriginalQuery() {
    return originalQuery;
  }
  /**
   * @return the completeResult
   */
  @Override
  public final long getCompleteResult() {
    return completeResult;
  }

  /**
   * @param completeResult
   *          the completeResult to set
   */
  public final void setCompleteResult(final long completeResult) {
    this.completeResult = completeResult;
  }

  @Override
  public String toString() {
    return String.valueOf(originalQuery);
  }


}
