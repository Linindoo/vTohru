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

import cn.vtohru.orm.mapping.ISyncCommand;
import cn.vtohru.orm.mapping.ISyncResult;

/**
 * The default implementation for all datastores, where String is used as native format to synchronize a connected table
 * 
 * @author Michael Remme
 * 
 */

public class DefaultSyncResult implements ISyncResult<String> {
  private List<ISyncCommand<String>> commands = new ArrayList<>();

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.ISyncResult#getCommands()
   */
  @Override
  public List<ISyncCommand<String>> getCommands() {
    return commands;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.mapping.ISyncResult#addCommand(cn.vtohru.orm.mapping.
   * ISyncCommand)
   */
  @Override
  public void addCommand(ISyncCommand<String> command) {
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
