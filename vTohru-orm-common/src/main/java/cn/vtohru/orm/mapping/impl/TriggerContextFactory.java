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

import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.ITriggerContext;
import cn.vtohru.orm.mapping.ITriggerContextFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Default implementation of {@link ITriggerContextFactory}
 * 
 * @author Michael Remme
 * 
 */
public class TriggerContextFactory implements ITriggerContextFactory {

  /**
   * 
   */
  public TriggerContextFactory() {
  }

  @Override
  public ITriggerContext createTriggerContext(IMapper mapper, Handler<AsyncResult<Void>> handler) {
    return new TriggerContext(mapper, handler);
  }

}
