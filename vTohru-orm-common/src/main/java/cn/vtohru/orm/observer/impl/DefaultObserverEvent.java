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

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.IAccessResult;
import cn.vtohru.orm.dataaccess.IDataAccessObject;
import cn.vtohru.orm.observer.IObserverEvent;
import cn.vtohru.orm.observer.ObserverEventType;

/**
 * Default implementation for an {@link IObserverEvent}
 * 
 * @author Michael Remme
 * 
 */
public class DefaultObserverEvent implements IObserverEvent {
  private final ObserverEventType eventType;
  private final Object entity;
  private final IAccessResult accessResult;
  private final IDataAccessObject<?> accessObject;
  private IDataStore<?, ?> datastore;

  /**
   * 
   * @param eventType
   * @param entity
   * @param accessResult
   * @param accessObject
   * @param datastore
   */
  public DefaultObserverEvent(ObserverEventType eventType, Object entity, IAccessResult accessResult,
      IDataAccessObject<?> accessObject, IDataStore<?, ?> datastore) {
    this.eventType = eventType;
    this.entity = entity;
    this.accessResult = accessResult;
    this.accessObject = accessObject;
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.observer.IObserverEvent#getEventType()
   */
  @Override
  public ObserverEventType getEventType() {
    return eventType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.observer.IObserverEvent#getEntity()
   */
  @Override
  public Object getSource() {
    return entity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.observer.IObserverEvent#getAccessResult()
   */
  @Override
  public IAccessResult getAccessResult() {
    return accessResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.observer.IObserverEvent#getAccessObject()
   */
  @Override
  public IDataAccessObject<?> getAccessObject() {
    return accessObject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.observer.IObserverEvent#getDataStore()
   */
  @Override
  public IDataStore<?, ?> getDataStore() {
    return datastore;
  }

}
