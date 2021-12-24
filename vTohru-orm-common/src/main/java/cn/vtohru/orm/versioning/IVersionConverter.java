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
package cn.vtohru.orm.versioning;

import cn.vtohru.orm.IDataStore;
import io.vertx.core.Future;

public interface IVersionConverter<T> {

  /**
   * Convert an instance from one version to the next one
   * 
   * @param datastore
   * @param toBeConverted
   * @return
   */
  Future<Void> convert(IDataStore<?, ?> datastore, T toBeConverted);

}
