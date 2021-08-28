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

import cn.vtohru.orm.dataaccess.IAccessResult;
import cn.vtohru.orm.observer.IObserver;
import cn.vtohru.orm.observer.IObserverContext;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * 
 * 
 * @author Michael Remme
 * @param <T>
 *          the type of the IDataAccessObject
 * @param <U>
 *          the type of the IAccessResult
 */
public abstract class AbstractEventHandler<T, U extends IAccessResult> {

  /**
   * Handles the event
   * 
   * @param accessObject
   * @param result
   * @param context
   * @param ol
   * @return
   */
  public Future<Void> handle(T accessObject, U result, IObserverContext context, List<IObserver> ol) {
    Promise<Void> promise = Promise.promise();
    CompositeFuture cf = loopObserver(ol, accessObject, result, context);
    cf.onComplete(cfr -> {
      if (cfr.failed()) {
        promise.fail(cfr.cause());
      } else {
        promise.complete();
      }
    });
    return promise.future();
  }

  /**
   * for each defined observer, process the entities of the write object
   * 
   * @param ol
   * @param accessObject
   * @param result
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected CompositeFuture loopObserver(List<IObserver> ol, T accessObject, U result, IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    for (IObserver observer : ol) {
      fl.add(loopEntities(observer, accessObject, result, context));
    }
    return CompositeFuture.all(fl);
  }

  /**
   * Execute the current observer on each entity of the write object
   * 
   * @param observer
   * @param accessObject
   * @param result
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected Future<Void> loopEntities(IObserver observer, T accessObject, U result, IObserverContext context) {
    Promise<Void> promise = Promise.promise();
    List<Future> fl = createEntityFutureList(observer, accessObject, result, context);
    if (fl.isEmpty()) {// if all handlers work fire-and-forget or ifnothing was handled
      promise.complete();
    } else {
      CompositeFuture cf = CompositeFuture.all(fl);
      cf.onComplete(res -> {
        if (res.failed()) {
          promise.fail(res.cause());
        } else {
          promise.complete();
        }
      });
    }
    return promise.future();
  }

  /**
   * Use the entities to create the Future list
   * 
   * @param observer
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected abstract List<Future> createEntityFutureList(IObserver observer, T accessObject, U result,
      IObserverContext context);
}
