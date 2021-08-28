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

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.observer.IObserver;
import cn.vtohru.orm.observer.IObserverContext;
import cn.vtohru.orm.observer.IObserverEvent;
import cn.vtohru.orm.observer.ObserverEventType;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * A handler which handles the event Before_Mapping
 * 
 * @author Michael Remme
 * 
 */
public class BeforeMappingHandler {

  /**
   * Handles the event
   * 
   * @param mapper
   * @param context
   * @param ol
   * @return
   */
  public Future<Void> handle(Class<?> mapperClass, IObserverContext context, List<IObserver> ol,
      IDataStore<?, ?> datastore) {
    Promise<Void> f = Promise.promise();
    CompositeFuture cf = loopObserver(ol, mapperClass, context, datastore);
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
   * for each defined observer, process the mapper
   * 
   * @param ol
   * @param mapper
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected CompositeFuture loopObserver(List<IObserver> ol, Class<?> mapperClass, IObserverContext context,
      IDataStore<?, ?> datastore) {
    List<Future> fl = new ArrayList<>();
    for (IObserver observer : ol) {
      IObserverEvent event = IObserverEvent.createEvent(ObserverEventType.AFTER_MAPPING, mapperClass, null, null,
          datastore);
      if (observer.canHandleEvent(event, context)) {
        Future tf = observer.handleEvent(event, context);
        if (tf != null) {
          fl.add(tf);
        }
      }
    }
    return CompositeFuture.all(fl);
  }
}
