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
import cn.vtohru.orm.util.AbstractCollectionAsync;
import cn.vtohru.orm.util.IteratorAsync;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of IQueryResult. Extensions must implement one method to generate single pojos
 *
 * @author Michael Remme
 * @param <T>
 *          the class of the mapper, which builds the result
 */

public abstract class AbstractQueryResult<T> extends AbstractCollectionAsync<T> implements IQueryResult<T> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractQueryResult.class);

  private final IMapper<T> mapper;
  private final IDataStore datastore;
  private final T[] pojoResult;
  private final IQueryExpression originalQuery;
  private long completeResult;

  /**
   * Constructor
   *
   * @param datastore
   *          the datastore which was used
   * @param mapper
   *          the mapper which was used
   * @param resultSize
   *          the size of the resulting query
   * @param originalQuery
   *          the original query which was processed to create the current result
   */
  @SuppressWarnings("unchecked")
  public AbstractQueryResult(final IDataStore datastore, final IMapper<T> mapper, final int resultSize, final IQueryExpression originalQuery) {
    this.datastore = datastore;
    this.mapper = mapper;
    this.originalQuery = originalQuery;
    this.pojoResult = (T[]) new Object[resultSize];
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.util.util.CollectionAsync#size()
   */
  @Override
  public final int size() {
    return pojoResult.length;
  }

  /**
   * Create a Pojo from the information read from the datastore at position i and return it to the handler. The handler
   * will place the object into the internal array at the same position
   *  @param i
   *          the position inside the result from the datastore
   * @return
   */
  protected abstract Future<T> generatePojo(int i);

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

  @Override
  public IteratorAsync<T> iterator() {
    return new QueryResultIterator();
  }

  class QueryResultIterator implements IteratorAsync<T> {
    private int currentIndex = 0;

    @Override
    public boolean hasNext() {
      return currentIndex < pojoResult.length;
    }

    @Override
    public void next(final Handler<AsyncResult<T>> handler) {
      int thisIndex = currentIndex++;
      if (thisIndex >= pojoResult.length) {
        handler.handle(Future.failedFuture("no result data"));
      } else if (pojoResult[thisIndex] == null) {
        generatePojo(thisIndex).onComplete(handler);
      } else {
        handler.handle(Future.succeededFuture(pojoResult[thisIndex]));
      }
    }

    @Override
    public void result(Handler<AsyncResult<List<T>>> handler) {
      List<Future> futures = new ArrayList<>();
      for (int i = 0; i < pojoResult.length; i++) {
        futures.add(generatePojo(i));
      }
      CompositeFuture.all(futures).onSuccess(x -> {
        handler.handle(Future.succeededFuture(x.result().list()));
      }).onFailure(e -> handler.handle(Future.failedFuture(e)));
    }

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
