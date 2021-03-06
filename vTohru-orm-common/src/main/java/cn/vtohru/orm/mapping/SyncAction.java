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

package cn.vtohru.orm.mapping;

/**
 * This describes the action, which is performed by {@link IDataStoreSynchronizer}
 * 
 * @author Michael Remme
 * 
 */

public enum SyncAction {

  /**
   * The IDataStoreSynchronizer created a new table inside the datastore
   */
  CREATE,
  /**
   * The IDataStoreSynchronizer updated a table inside the datastore
   */
  UPDATE,

  /**
   * The IDataStoreSynchronizer deleted a table inside the datastore
   */
  DELETE,

  /**
   * No action was performed, the mapper and table / column are in sync
   */
  NO_ACTION;

}
