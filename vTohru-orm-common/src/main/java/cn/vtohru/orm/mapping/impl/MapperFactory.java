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

import cn.vtohru.context.VerticleApplicationContext;
import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IMapperFactory;
import cn.vtohru.orm.mapping.IPropertyMapperFactory;
import cn.vtohru.orm.typehandler.ITypeHandlerFactory;

/**
 * Default implementation of {@link IMapperFactory}
 * 
 * @author Michael Remme
 * 
 */

public class MapperFactory extends AbstractMapperFactory {
  private ITypeHandlerFactory typeHandlerFactory;
  private IPropertyMapperFactory propertyMapperFactory;
  private VerticleApplicationContext context;

  /**
   *
   */
  public MapperFactory(IDataStore<?, ?> dataStore, ITypeHandlerFactory typeHandlerFactory,
                       IPropertyMapperFactory propertyMapperFactory, VerticleApplicationContext context) {
    super(dataStore);
    this.typeHandlerFactory = typeHandlerFactory;
    this.propertyMapperFactory = propertyMapperFactory;
    this.context = context;
  }

  /**
   * Creates a new instance of IMapper for the given class
   * 
   * @param mapperClass
   *          the class to be mapped
   * @return the mapper
   */
  @Override
  protected <T> IMapper<T> createMapper(Class<T> mapperClass) {
    return new Mapper<>(mapperClass, this);
  }

  /**
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  @Override
  public final ITypeHandlerFactory getTypeHandlerFactory() {
    return typeHandlerFactory;
  }

  /**
   * Set the {@link ITypeHandlerFactory} which shall be used by the current implementation
   * 
   * @param typeHandlerFactory
   *          the typeHandlerFactory to set
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  protected final void setTypeHandlerFactory(ITypeHandlerFactory typeHandlerFactory) {
    this.typeHandlerFactory = typeHandlerFactory;
  }

  /**
   * @deprecated will be removed after complete switch to jackson
   * 
   */
  @Deprecated
  @Override
  public final IPropertyMapperFactory getPropertyMapperFactory() {
    return propertyMapperFactory;
  }

  /**
   * @param propertyMapperFactory
   *          the propertyMapperFactory to set
   * @deprecated will be removed after complete switch to jackson
   */
  @Deprecated
  protected final void setPropertyMapperFactory(IPropertyMapperFactory propertyMapperFactory) {
    this.propertyMapperFactory = propertyMapperFactory;
  }

}
