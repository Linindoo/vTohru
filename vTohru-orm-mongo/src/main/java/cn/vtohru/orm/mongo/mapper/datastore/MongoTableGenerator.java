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

package cn.vtohru.orm.mongo.mapper.datastore;

import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.datastore.ITableInfo;
import cn.vtohru.orm.mapping.datastore.impl.DefaultTableGenerator;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoTableGenerator extends DefaultTableGenerator {

  /**
   * 
   */
  public MongoTableGenerator() {
  }

  @Override
  public ITableInfo createTableInfo(IMapper mapper) {
    return new MongoTableInfo(mapper);
  }

}
