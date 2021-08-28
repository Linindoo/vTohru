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
 * Handles the event {@link ObserverEventType#AFTER_LOAD }
 * 
 * @author Michael Remme
 * 
 */
public class AfterLoadHandler extends AbstractEventHandler<IQuery<?>, IQueryResult<?>> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.impl.AbstractEventHandler#createEntityFutureList(cn.vtohru.orm
   * .observer.IObserver, cn.vtohru.orm.dataaccess.IDataAccessObject,
   * cn.vtohru.orm.observer.IObserverContext)
   */
  @SuppressWarnings("rawtypes")
  @Override
  protected List<Future> createEntityFutureList(IObserver observer, IQuery<?> queryObject, IQueryResult<?> result,
      IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    result.toArray(res -> {
      if (res.failed()) {
        fl.add(Future.failedFuture(res.cause()));
      } else {
        Object[] selection = res.result();
        for (Object o : selection) {
          IObserverEvent event = IObserverEvent.createEvent(ObserverEventType.AFTER_LOAD, o, result, queryObject,
              queryObject.getDataStore());
          if (observer.canHandleEvent(event, context)) {
            Future tf = observer.handleEvent(event, context);
            if (tf != null) {
              fl.add(tf);
            }
          }
        }
      }
    });

    return fl;
  }

}
