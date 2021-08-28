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

import java.util.function.Function;

import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.ITriggerContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;

/**
 * A TriggerContext can be used as argument for mapper methods, which are annotated by one of the annotations like
 * {@link BeforeSave}, {@link AfterSave} etc.
 * 
 * @author Michael Remme
 * 
 */
public class TriggerContext implements ITriggerContext {
  private IMapper mapper;
  private Future<Void> future;

  {
    Promise<Void> promise = Promise.promise();
    future = promise.future();
  }

  /**
   * 
   */
  TriggerContext(IMapper mapper, Handler<AsyncResult<Void>> handler) {
    this.mapper = mapper;
    future.onComplete(handler);
  }

  /**
   * Get the instance of IMapper, which is underlaying the current request
   * 
   * @return the mapper
   */
  @Override
  public final IMapper getMapper() {
    return mapper;
  }

  /**
   * @return
   * @see io.vertx.core.Future#isComplete()
   */
  @Override
  public boolean isComplete() {
    return future.isComplete();
  }

  @Override
  public Future<Void> onComplete(Handler<AsyncResult<Void>> handler) {
    return null;
  }

  /**
   * @param handler
   * @return
   * @see io.vertx.core.Future#setHandler(io.vertx.core.Handler)
   */

  /**
   * @return
   * @see io.vertx.core.Future#result()
   */
  @Override
  public Void result() {
    return future.result();
  }

  /**
   * @return
   * @see io.vertx.core.Future#cause()
   */
  @Override
  public Throwable cause() {
    return future.cause();
  }

  /**
   * @return
   * @see io.vertx.core.Future#succeeded()
   */
  @Override
  public boolean succeeded() {
    return future.succeeded();
  }

  /**
   * @return
   * @see io.vertx.core.Future#failed()
   */
  @Override
  public boolean failed() {
    return future.failed();
  }

  /**
   * @param mapper
   * @return
   * @see io.vertx.core.Future#compose(Function)
   */
  @Override
  public <U> Future<U> compose(Function<Void, Future<U>> mapper) {
    return future.compose(mapper);
  }

  /**
   * @param value
   * @return
   * @see io.vertx.core.Future#map(Object)
   */
  @Override
  public <V> Future<V> map(V value) {
    return future.map(value);
  }


  /**
   * @param mapper
   * @return
   * @see io.vertx.core.Future#map(Function)
   */
  @Override
  public <U> Future<U> map(Function<Void, U> mapper) {
    return future.map(mapper);
  }

  /**
   * @param mapper
   * @return
   * @see io.vertx.core.Future#otherwise(Function)
   */
  @Override
  public Future<Void> otherwise(Function<Throwable, Void> mapper) {
    return future.otherwise(mapper);
  }

  /**
   * @param value
   * @return
   * @see io.vertx.core.Future#otherwise(Object)
   */
  @Override
  public Future<Void> otherwise(Void value) {
    return future.otherwise(value);
  }

  /**
   * @param mapper
   * @return
   * @see io.vertx.core.Future#recover(Function)
   */
  @Override
  public Future<Void> recover(Function<Throwable, Future<Void>> mapper) {
    return future.recover(mapper);
  }

  @Override
  public <U> Future<U> compose(Function<Void, Future<U>> function, Function<Throwable, Future<U>> function1) {
    return future.compose(function, function1);
  }

  @Override
  public <U> Future<U> transform(Function<AsyncResult<Void>, Future<U>> function) {
    return future.transform(function);
  }

  @Override
  public <U> Future<Void> eventually(Function<Void, Future<U>> function) {
    return future.eventually(function);
  }

}
