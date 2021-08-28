/*-
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package cn.vtohru.orm.mongo.init;

import cn.vtohru.orm.mapping.ISyncCommand;
import cn.vtohru.orm.mapping.SyncAction;
import io.vertx.core.json.JsonObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MongoSyncCommand implements ISyncCommand<JsonObject> {
  private final SyncAction syncAction;
  private JsonObject syncCommand;

  public MongoSyncCommand(SyncAction action) {
    this.syncAction = action;
  }

  public MongoSyncCommand(SyncAction action, JsonObject command) {
    this.syncAction = action;
    this.syncCommand = command;
  }

  @Override
  public JsonObject getCommand() {
    return syncCommand;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.ISyncResult#getAction()
   */
  @Override
  public SyncAction getAction() {
    return syncAction;
  }

}
