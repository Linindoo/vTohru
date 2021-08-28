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
import cn.vtohru.orm.mapping.ISyncResult;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link ISyncResult} for mongodb, which uses Json as format
 * 
 * @author Michael Remme
 * 
 */
public class MongoSyncResult implements ISyncResult<JsonObject> {
  private List<ISyncCommand<JsonObject>> commands = new ArrayList<>();

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.ISyncResult#getCommands()
   */
  @Override
  public List<ISyncCommand<JsonObject>> getCommands() {
    return commands;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.ISyncResult#addCommand(cn.vtohru.orm.mapping.
   * ISyncCommand)
   */
  @Override
  public void addCommand(ISyncCommand<JsonObject> command) {
    commands.add(command);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.ISyncResult#isUnmodified()
   */
  @Override
  public boolean isUnmodified() {
    return commands.isEmpty();
  }

}
