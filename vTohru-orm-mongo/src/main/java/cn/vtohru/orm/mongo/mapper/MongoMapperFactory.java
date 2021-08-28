/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package cn.vtohru.orm.mongo.mapper;

import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IPropertyMapperFactory;
import cn.vtohru.orm.mapping.impl.AbstractMapperFactory;
import cn.vtohru.orm.mapping.impl.MapperFactory;
import cn.vtohru.orm.mongo.MongoDataStore;
import cn.vtohru.orm.typehandler.ITypeHandlerFactory;

/**
 * An extension of {@link MapperFactory}
 *
 * @author Michael Remme
 * 
 */

public class MongoMapperFactory extends AbstractMapperFactory {

  /**
   * @param dataStore
   */
  public MongoMapperFactory(MongoDataStore dataStore) {
    super(dataStore);
  }

  @Override
  protected <T> IMapper<T> createMapper(Class<T> mapperClass) {
    return new MongoMapper<>(mapperClass, this);
  }

  @Override
  public ITypeHandlerFactory getTypeHandlerFactory() {
    throw new UnsupportedOperationException("JacksonMapperFactory is not allowed to use typehandlers");
  }

  @Override
  public IPropertyMapperFactory getPropertyMapperFactory() {
    throw new UnsupportedOperationException("JacksonMapperFactory is not allowed to use IPropertyMapper");
  }
}
