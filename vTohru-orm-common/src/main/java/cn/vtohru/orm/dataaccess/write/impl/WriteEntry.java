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
package cn.vtohru.orm.dataaccess.write.impl;

import cn.vtohru.orm.dataaccess.write.IWriteEntry;
import cn.vtohru.orm.dataaccess.write.WriteAction;
import cn.vtohru.orm.mapping.IStoreObject;

/**
 * Default implementation of {@link IWriteEntry}
 * 
 * @author Michael Remme
 * 
 */

public class WriteEntry implements IWriteEntry {
  private final IStoreObject<?, ?> sto;
  private final Object id;
  private final WriteAction action;

  public WriteEntry(IStoreObject<?, ?> sto, Object id, WriteAction action) {
    this.sto = sto;
    this.id = id;
    this.action = action;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.write.IWriteEntry#getStoreObject()
   */
  @Override
  public IStoreObject<?, ?> getStoreObject() {
    return sto;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.write.IWriteEntry#getId()
   */
  @Override
  public Object getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.write.IWriteEntry#getAction()
   */
  @Override
  public WriteAction getAction() {
    return action;
  }

  @Override
  public String toString() {
    return "Action: " + action + " | " + getStoreObject().toString();
  }
}
