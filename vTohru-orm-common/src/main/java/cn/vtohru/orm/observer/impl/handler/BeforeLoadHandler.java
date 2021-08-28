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
package cn.vtohru.orm.observer.impl.handler;

import java.util.ArrayList;
import java.util.List;

import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.query.IQueryResult;
import cn.vtohru.orm.observer.IObserver;
import cn.vtohru.orm.observer.IObserverContext;
import cn.vtohru.orm.observer.IObserverEvent;
import cn.vtohru.orm.observer.ObserverEventType;
import io.vertx.core.Future;

/**
 * Handles the event {@link ObserverEventType#BEFORE_LOAD }
 * 
 * @author Michael Remme
 * 
 */
public class BeforeLoadHandler extends AbstractEventHandler<IQuery<?>, IQueryResult<?>> {

  @SuppressWarnings("rawtypes")
  @Override
  protected List<Future> createEntityFutureList(IObserver observer, IQuery<?> queryObject, IQueryResult<?> result,
      IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    fl.add(Future.failedFuture(
        new UnsupportedOperationException("we should not land here, cause there are no entities before load")));
    return fl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.impl.AbstractEventHandler#loopEntities(cn.vtohru.orm.observer.
   * IObserver, cn.vtohru.orm.dataaccess.IDataAccessObject,
   * cn.vtohru.orm.dataaccess.IAccessResult, cn.vtohru.orm.observer.IObserverContext)
   */
  @Override
  protected Future<Void> loopEntities(IObserver observer, IQuery<?> accessObject, IQueryResult<?> result,
      IObserverContext context) {
    IObserverEvent event = IObserverEvent.createEvent(ObserverEventType.BEFORE_LOAD, null, null, accessObject,
        accessObject.getDataStore());
    if (observer.canHandleEvent(event, context)) {
      return observer.handleEvent(event, context);
    } else {
      return Future.succeededFuture();
    }
  }

}
