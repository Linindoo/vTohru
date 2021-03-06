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


/**
 * IObjectFactory is used to create new instances for a mapper.
 * 
 * @author Michael Remme
 * 
 */

public interface IObjectFactory {

  /**
   * Creates an instance of the given class.
   * 
   * @param clazz
   * @return
   */
  <T> T createInstance(Class<T> clazz);

  void setMapper(IMapper mapper);

  /**
   * Get the Mapper, where the IObjectFactory is contained
   * 
   * @return the {@link IMapper}
   */
  IMapper getMapper();
}
