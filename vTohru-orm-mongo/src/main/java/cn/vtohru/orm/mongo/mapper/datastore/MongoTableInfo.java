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

package cn.vtohru.orm.mongo.mapper.datastore;

import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.datastore.IColumnHandler;
import cn.vtohru.orm.mapping.datastore.IColumnInfo;
import cn.vtohru.orm.mapping.datastore.impl.DefaultTableInfo;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoTableInfo extends DefaultTableInfo {

  /**
   * @param mapper
   */
  public MongoTableInfo(IMapper mapper) {
    super(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.mapping.datastore.impl.DefaultTableInfo#generateColumnInfo(de.braintags.vertx.util.
   * pojomapper.mapping.IField, cn.vtohru.orm.mapping.datastore.IColumnHandler)
   */
  @Override
  protected IColumnInfo generateColumnInfo(IProperty field, IColumnHandler columnHandler) {
    return new MongoColumnInfo(field, columnHandler);
  }

}
