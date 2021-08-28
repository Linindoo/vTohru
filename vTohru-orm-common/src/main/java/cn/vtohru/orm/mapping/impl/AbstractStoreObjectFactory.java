/*-
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

import java.util.ArrayList;
import java.util.List;

import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IStoreObject;
import cn.vtohru.orm.mapping.IStoreObjectFactory;
import io.vertx.core.*;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractStoreObjectFactory<F> implements IStoreObjectFactory<F> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.mapping.IStoreObjectFactory#createStoreObjects(cn.vtohru.orm.
   * mapping.IMapper, java.util.List, io.vertx.core.Handler)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <T> void createStoreObjects(final IMapper<T> mapper, final List<T> entities,
      final Handler<AsyncResult<List<IStoreObject<T, F>>>> handler) {
    List<Future> fl = createFutureList(mapper, entities);
    CompositeFuture cf = CompositeFuture.all(fl);
    cf.onComplete(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        List stl = createStoreObjectList(cf);
        handler.handle(Future.succeededFuture(stl));
      }
    });
  }

  @SuppressWarnings("unchecked")
  private <T> List<IStoreObject<T, ?>> createStoreObjectList(final CompositeFuture cf) {
    List<IStoreObject<T, ?>> stl = new ArrayList<>();
    cf.list().forEach(f -> stl.add((IStoreObject<T, ?>) f));
    return stl;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <T> List<Future> createFutureList(final IMapper<T> mapper, final List<T> entities) {
    List<Future> fl = new ArrayList<>();
    for (T entity : entities) {
      Promise f = Promise.promise();
      fl.add(f.future());
      createStoreObject(mapper, entity, f);
    }
    return fl;
  }

}
