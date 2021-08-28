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
package cn.vtohru.orm.observer.impl;

import java.util.Properties;

import cn.vtohru.orm.observer.IObserver;
import cn.vtohru.orm.observer.IObserverContext;
import cn.vtohru.orm.observer.IObserverEvent;
import io.vertx.core.Vertx;

/**
 * An abstract implementation which contains the properties
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractObserver implements IObserver {
  private Properties properties = new Properties();

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.observer.IObserver#getObserverProperties()
   */
  @Override
  public Properties getObserverProperties() {
    return properties;
  }

  @Override
  public boolean canHandleEvent(IObserverEvent event, IObserverContext context) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.observer.IObserver#init(io.vertx.core.Vertx)
   */
  @Override
  public void init(Vertx vertx) {
    // empty implementation here
  }

}
