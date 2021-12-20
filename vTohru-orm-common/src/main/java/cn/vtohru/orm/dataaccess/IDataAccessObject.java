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
package cn.vtohru.orm.dataaccess;


import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.mapping.IMapper;
import io.vertx.core.Future;

/**
 * describes common methods for all dataaccess objects
 * 
 * @author Michael Remme
 * @param <T>
 *          the mapper class which is dealed by the current instance
 */

public interface IDataAccessObject<T> {

  /**
   * Get the parent {@link IDataStore}
   * 
   * @return the {@link IDataStore}
   */
  IDataStore<?, ?> getDataStore();

  /**
   * Get the underlaying mapper class of the instance
   * 
   * @return the class
   */
  Class<T> getMapperClass();

  /**
   * Get the underlaying instance of {@link IMapper}
   * 
   * @return the mapper
   */
  IMapper<T> getMapper();

  void setSession(ISession session);

}
