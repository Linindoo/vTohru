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
package cn.vtohru.orm.mapping.impl.keygen;


import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.mapping.IKeyGenerator;

/**
 * An abstract implementation of {@link IKeyGenerator}
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractKeyGenerator implements IKeyGenerator {
  private String name;
  private IDataStore datastore;

  /**
   * 
   * @param name
   *          the name of the generator
   * @param datastore
   *          the datastore to be used
   */
  public AbstractKeyGenerator(String name, IDataStore datastore) {
    this.name = name;
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.IKeyGenerator#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Get the {@link IDataStore} where the current instance is belonging to
   * 
   * @return
   */
  public IDataStore getDataStore() {
    return datastore;
  }
}
