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
package cn.vtohru.orm.mapping.impl.keygen;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class DefaultKeyGenerator extends AbstractKeyGenerator {
  public static final String SERVICE_NAME = "KeyGeneratorVerticle";
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(DefaultKeyGenerator.class);
  public static final String NAME = "DefaultKeyGenerator";
  private Vertx vertx;

  /**
   * @param name
   * @param datastore
   * @param vertx
   */
  public DefaultKeyGenerator(IDataStore datastore, Vertx vertx) {
    super(NAME, datastore);
    this.vertx = vertx;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.IKeyGenerator#generateKey(cn.vtohru.orm.mapping.
   * IMapper, io.vertx.core.Handler)
   */
  @Override
  public void generateKey(IMapper<?> mapper, Handler<AsyncResult<Key>> handler) {
    vertx.eventBus().request(SERVICE_NAME, mapper.getKeyGeneratorReference(), result -> {
      if (result.failed()) {
        LOGGER.error(result.cause());
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handler.handle(Future.succeededFuture(new Key(result.result().body())));
      }
    });
  }

}
