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
package cn.vtohru.orm.dataaccess.write.impl;

import java.util.ArrayList;
import java.util.List;

import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.observer.IObserver;
import cn.vtohru.orm.observer.IObserverContext;
import cn.vtohru.orm.observer.IObserverEvent;
import cn.vtohru.orm.observer.ObserverEventType;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Handles the event {@link ObserverEventType#BEFORE_INSERT }
 * 
 * @author Michael Remme
 * 
 */
public class BeforeInsertHandler {

  /**
   * @param write
   * @param storeObject
   * @param context
   * @param ol
   * @return
   */
  public <T> Future<Void> handle(IWrite<T> write, T entity, IObserverContext context, List<IObserver> ol) {
    Promise<Void> f = Promise.promise();
    CompositeFuture cf = loopObserver(ol, write, entity, context);
    cf.onComplete(cfr -> {
      if (cfr.failed()) {
        f.fail(cfr.cause());
      } else {
        f.complete();
      }
    });
    return f.future();
  }

  /**
   * for each defined observer, process the instance
   * 
   * @param ol
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected <T> CompositeFuture loopObserver(List<IObserver> ol, IWrite<T> writeObject, T entity,
      IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    IObserverEvent event = IObserverEvent.createEvent(getEventType(), entity, null, writeObject,
        writeObject.getDataStore());
    for (IObserver observer : ol) {
      if (observer.canHandleEvent(event, context)) {
        Future tf = observer.handleEvent(event, context);
        if (tf != null) {
          fl.add(tf);
        }
      }
    }
    return CompositeFuture.all(fl);
  }

  protected ObserverEventType getEventType() {
    return ObserverEventType.BEFORE_INSERT;
  }
}
