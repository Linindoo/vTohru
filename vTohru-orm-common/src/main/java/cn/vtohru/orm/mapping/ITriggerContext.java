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
package cn.vtohru.orm.mapping;

import io.vertx.core.Future;

/**
 * An ITriggerContext can be used as argument for mapper methods, which are annotated by one of the annotations like
 * {@link BeforeSave}, {@link AfterSave} etc.
 * Inside the implementation of the trigger method you will implement the functionality like:
 * 
 * <pre>
 * try {
 *   IDataStore datastore = getMapper().getMapperFactory().getDataStore();
 *   // do some suitable actions for the trigger
 *   future.complete();
 * } catch (Exception e) {
 *   future.fail(e);
 * }
 * 
 * </pre>
 * 
 * @author Michael Remme
 * 
 */
public interface ITriggerContext extends Future<Void> {

  /**
   * Get the instance of IMapper, which is underlaying the current request
   * 
   * @return the mapper
   */
  IMapper getMapper();
}
