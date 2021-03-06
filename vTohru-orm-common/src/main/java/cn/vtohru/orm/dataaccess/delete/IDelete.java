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

package cn.vtohru.orm.dataaccess.delete;

import cn.vtohru.orm.dataaccess.IDataAccessObject;
import cn.vtohru.orm.dataaccess.ISession;
import cn.vtohru.orm.dataaccess.query.IQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * IDelete is responsible for the deletion of instances from the connected datastore
 * 
 * @author Michael Remme
 * @param <T>
 *          the underlaying mapper to be handled
 */

public interface IDelete<T> extends IDataAccessObject<T> {

  /**
   * Delete instances from the connected {@link IDataStore}
   * 
   * @param resultHandler
   *          a handler, which will receive information about the {@link IDeleteResult}
   */
  public void delete(Handler<AsyncResult<IDeleteResult>> resultHandler);

  Future<Void> execute(ISession session);

  /**
   * Defines the {@link IQuery} which is used as base to delete records from the datastore.
   * 
   * @param query
   *          the {@link IQuery} which shall be used to specify the records to be deleted from the {@link IDataStore}
   */
  public void setQuery(IQuery<T> query);

  /**
   * Add an instance which will be deleted from the {@link IDataStore}
   * 
   * @param record
   */
  public void add(T record);

  /**
   * Add a number of instance which shall be deleted from the {@link IDataStore}
   * 
   * @param records
   *          the records to be deleted
   */
  @SuppressWarnings("unchecked")
  public void add(T... records);

  /**
   * Get the number of records to be deleted
   * 
   * @return the count
   */
  public int size();

}
