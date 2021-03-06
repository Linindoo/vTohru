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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.vtohru.orm.annotation.Indexes;
import cn.vtohru.orm.mapping.IDataStoreSynchronizer;
import cn.vtohru.orm.mapping.IIndexDefinition;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.ISyncResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Abstract implementation for {@link IDataStoreSynchronizer}
 *
 * @author Michael Remme
 *
 */
public abstract class AbstractDataStoreSynchronizer<T> implements IDataStoreSynchronizer<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataStoreSynchronizer.class);
  private final List<String> synchronizedInstances = new ArrayList<>();

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.mapping.IDataStoreSynchronizer#synchronize(cn.vtohru.orm.
   * mapping .IMapper, io.vertx.core.Handler)
   */
  @Override
  public final <U> void synchronize(final IMapper<U> mapper, final Handler<AsyncResult<ISyncResult<T>>> resultHandler) {
    if (synchronizedInstances.contains(mapper.getMapperClass().getName())) {
      resultHandler.handle(Future.succeededFuture(getSyncResult()));
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("starting synchronization for mapper " + mapper.getClass().getSimpleName());
      }
      syncTable(mapper, res -> {
        if (res.failed()) {
          resultHandler.handle(Future.failedFuture(res.cause()));
        } else {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("successful synchronized for mapper " + mapper.getClass().getSimpleName());
          }
          syncIndexDefinitions(mapper, idxResult -> {
            if (idxResult.failed()) {
              resultHandler.handle(Future.failedFuture(idxResult.cause()));
            } else {
              synchronizedInstances.add(mapper.getMapperClass().getName());
              resultHandler.handle(Future.succeededFuture(getSyncResult()));
            }
          });
        }
      });
    }
  }

  /**
   * Check for existing index definitions and sync them if existing. The default implementation calls the method
   * {@link #syncIndexes(IMapper, Indexes, Handler)}
   *
   * @param mapper
   * @param resultHandler
   */
  protected void syncIndexDefinitions(final IMapper<?> mapper, final Handler<AsyncResult<Void>> resultHandler) {
    if (mapper.getIndexDefinitions() == null || mapper.getIndexDefinitions().isEmpty()) {
      resultHandler.handle(Future.succeededFuture());
    } else {
      LOGGER.debug("Start synchronization of IndexDefinitions");
      syncIndexes(mapper, mapper.getIndexDefinitions(), resultHandler);
    }
  }

  /**
   * Called to perform the synchronization of the definitions found in {@link Indexes}
   *
   * @param mapper
   * @param indexDefinitions
   * @param resultHandler
   */
  protected abstract void syncIndexes(IMapper<?> mapper, Set<IIndexDefinition> indexDefinitions,
      Handler<AsyncResult<Void>> resultHandler);

  /**
   * Called if the synchronization wasn't done yet. This method shall perform the synchronization for the table /
   * collection itself and create / update the entity inside the datastore
   *
   * @param mapper
   * @param resultHandler
   */
  protected abstract void syncTable(IMapper mapper, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Get the instance of ISyncResult, which is used by this implementation
   *
   * @return the sync result
   */
  protected abstract ISyncResult<T> getSyncResult();
}
