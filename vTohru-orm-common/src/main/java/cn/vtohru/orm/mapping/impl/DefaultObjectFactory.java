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
package cn.vtohru.orm.mapping.impl;

import cn.vtohru.orm.exception.MappingException;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IObjectFactory;
import cn.vtohru.orm.mapping.IProperty;
import io.micronaut.core.beans.BeanIntrospection;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Default implementation of {@link IObjectFactory}
 *
 * @author Michael Remme
 *
 */

public class DefaultObjectFactory implements IObjectFactory {
  private IMapper mapper;

  @Override
  public <T> T createInstance(Class<T> clazz) {
    BeanIntrospection introspection = BeanIntrospection.getIntrospection(clazz);
    return (T) introspection.instantiate();
  }

  @Override
  public void setMapper(IMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public IMapper getMapper() {
    return mapper;
  }

}
