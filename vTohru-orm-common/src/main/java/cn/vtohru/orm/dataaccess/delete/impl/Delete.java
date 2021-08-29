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

package cn.vtohru.orm.dataaccess.delete.impl;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.annotation.lifecycle.AfterDelete;
import cn.vtohru.orm.annotation.lifecycle.BeforeDelete;
import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.delete.IDeleteResult;
import cn.vtohru.orm.dataaccess.impl.AbstractDataAccessObject;
import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.query.ISearchCondition;
import cn.vtohru.orm.dataaccess.query.IdField;
import cn.vtohru.orm.exception.ParameterRequiredException;
import cn.vtohru.orm.mapping.IIdInfo;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.observer.IObserverContext;
import io.vertx.core.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract implementation of {@link IDelete}
 *
 * @author Michael Remme
 * @param <T>
 *          the underlaying mapper to be used
 */

public abstract class Delete<T> extends AbstractDataAccessObject<T> implements IDelete<T> {

  private static final String ERROR_MESSAGE = "You can only use ONE source for deletion, either an IQuery or a list of instances";
  private IQuery<T> query;
  private final List<T> recordList = new ArrayList<>();

  /**
   * @param mapperClass
   * @param datastore
   */
  public Delete(final Class<T> mapperClass, final IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /**
   * Get the objects, which were defined to be deleted
   *
   * @return
   */
  Iterator<T> getSelection() {
    return recordList.iterator();
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.delete.IDelete#delete(io.vertx.core.Handler)
   */
  @Override
  public final void delete(final Handler<AsyncResult<IDeleteResult>> resultHandler) {
    if (getQuery() != null) {
      deleteQuery(query, resultHandler);
    } else if (!recordList.isEmpty()) {
      deleteRecords(resultHandler);
    } else
      throw new ParameterRequiredException("Nor query nor records defined to be deleted");
  }

  @Override
  public int size() {
    return recordList.size();
  }

  /**
   * This method deletes all records, which are fitting the query arguments
   *
   * @param query
   *          the query to be handled
   * @param resultHandler
   *          the handler to be informed
   */
  protected abstract void deleteQuery(IQuery<T> query, Handler<AsyncResult<IDeleteResult>> resultHandler);

  /**
   * This method deletes records, which were added into the current instance
   *
   * @param resultHandler
   *          the handler to be informed
   */
  protected final void deleteRecords(final Handler<AsyncResult<IDeleteResult>> resultHandler) {
    if (recordList.isEmpty()) {
      resultHandler.handle(Future.succeededFuture());
      return;
    }

    CompositeFuture cf = CompositeFuture.all(executeLifeCycle(BeforeDelete.class));
    cf.onComplete(res -> {
      if (res.failed()) {
        resultHandler.handle(Future.failedFuture(res.cause()));
      } else {
        try {
          IObserverContext context = IObserverContext.createInstance();
          preDelete(context).compose(pre -> doDeleteRecords()).compose(dr -> postDelete(dr, context)).onComplete(resultHandler);
        } catch (Exception e) {
          resultHandler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  private Future<IDeleteResult> doDeleteRecords() {
    Promise<IDeleteResult> promise = Promise.promise();
    IIdInfo idInfo = getMapper().getIdInfo();
    IdField idField = idInfo.getIndexedField();
    CompositeFuture cf = CompositeFuture.all(getRecordIds(idInfo.getField()));
    cf.onComplete(res -> {
      if (res.failed()) {
        promise.fail(res.cause());
      } else {
        deleteRecordsById(idField, cf.list(), promise);
      }
    });
    return promise.future();
  }

  /**
   * Execution done before instances are deleted from the datastore
   *
   * @return
   */
  protected Future<Void> preDelete(final IObserverContext context) {
    return getMapper().getObserverHandler().handleBeforeDelete(this, context);
  }

  /**
   * Execution done after entities were deleted from the datastore
   *
   */
  protected Future<IDeleteResult> postDelete(final IDeleteResult dr, final IObserverContext context) {
    Future<Void> f = getMapper().getObserverHandler().handleAfterDelete(this, dr, context);
    Promise<IDeleteResult> promise = Promise.promise();
    f.onComplete(res -> {
      if (res.failed()) {
        promise.fail(res.cause());
      } else {
        promise.complete(dr);
      }
    });
    return promise.future();
  }

  @SuppressWarnings("rawtypes")
  private List<Future> executeLifeCycle(final Class lifecycleClass) {
    List<Future> fl = new ArrayList<>();
    for (T record : getRecordList()) {
      Promise<Object> promise = Promise.promise();
      getMapper().executeLifecycle(lifecycleClass, record, x->{
        if (x.succeeded()) {
          promise.complete(x.result());
        } else {
          promise.fail(x.cause());
        }
      });
      fl.add(promise.future());
    }
    return fl;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * cn.vtohru.orm.dataaccess.delete.IDelete#setQuery(cn.vtohru.orm.dataaccess
   * .query.IQuery)
   */
  @Override
  public void setQuery(final IQuery<T> query) {
    if (!recordList.isEmpty())
      throw new UnsupportedOperationException(ERROR_MESSAGE);
    this.query = query;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.delete.IDelete#add(java.lang.Object)
   */
  @Override
  public void add(final T record) {
    if (query != null)
      throw new UnsupportedOperationException(ERROR_MESSAGE);
    recordList.add(record);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.delete.IDelete#add(java.lang.Object[])
   */
  @SuppressWarnings("unchecked")
  @Override
  public void add(final T... records) {
    if (query != null)
      throw new UnsupportedOperationException(ERROR_MESSAGE);
    recordList.addAll(Arrays.asList(records));
  }

  /**
   * @return the query
   */
  protected IQuery<T> getQuery() {
    return query;
  }

  /**
   * @deprecated use getSelection() instead
   */
  @Deprecated
  protected List<T> getRecordList() {
    return recordList;
  }

  /**
   * Generates a list of record ids from the records
   *
   * @param idField
   * @param resultHandler
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private List<Future> getRecordIds(final IProperty idField) {
    List<Future> fList = new ArrayList<>();
    for (T record : getRecordList()) {
      Promise<Object> promise = Promise.promise();
      idField.getPropertyMapper().readForStore(record, idField, promise);
      fList.add(promise.future());
    }
    return fList;
  }

  /**
   * Performs a deletion of instances by their ID
   *
   * @param idField
   *          the idfield
   * @param objectIds
   *          list of recordIds
   * @param resultHandler
   *          the handler to be informed
   */
  protected void deleteRecordsById(final IdField idField, final List<Object> objectIds,
      final Handler<AsyncResult<IDeleteResult>> resultHandler) {
    IQuery<T> q = getDataStore().createQuery(getMapperClass());
    q.setSearchCondition(ISearchCondition.in(idField, objectIds));
    deleteQuery(q, dr -> {
      if (dr.failed()) {
        resultHandler.handle(dr);
      } else {
        CompositeFuture cf = CompositeFuture.all(executeLifeCycle(AfterDelete.class));
        cf.onComplete(res -> {
          if (res.failed()) {
            resultHandler.handle(Future.failedFuture(res.cause()));
          } else {
            resultHandler.handle(dr);
          }
        });
      }
    });
  }

}
