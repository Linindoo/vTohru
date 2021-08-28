/*
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

package cn.vtohru.orm.mongo.dataaccess;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.delete.impl.DeleteResult;
import cn.vtohru.orm.mapping.IMapper;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class MongoDeleteResult extends DeleteResult {

  /**
   * @param datastore
   * @param mapper
   * @param command
   */
  public MongoDeleteResult(IDataStore datastore, IMapper mapper, Object command) {
    super(datastore, mapper, command);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.delete.IDeleteResult#getDeletedInstances()
   */
  @Override
  public int getDeletedInstances() {
    return 0;
  }

}
