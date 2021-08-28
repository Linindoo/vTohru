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

import cn.vtohru.orm.dataaccess.write.IWriteEntry;
import cn.vtohru.orm.dataaccess.write.impl.WriteResult;

import java.util.List;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class MongoWriteResult extends WriteResult {

  /**
   * 
   */
  public MongoWriteResult() {
    super();
  }

  /**
   * @param resultList
   */
  public MongoWriteResult(List<IWriteEntry> resultList) {
    super(resultList);
  }

}
