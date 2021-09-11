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

import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.datastore.IColumnHandler;
import cn.vtohru.orm.mapping.datastore.impl.DefaultColumnInfo;

import javax.persistence.Id;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoColumnInfo extends DefaultColumnInfo {
  public static final String ID_FIELD_NAME = "_id";

  /**
   * @param field
   * @param columnHandler
   */
  public MongoColumnInfo(IProperty field, IColumnHandler columnHandler) {
    super(field, columnHandler);
  }

  @Override
  protected String computePropertyName(IProperty field) {
    if (field.hasAnnotation(Id.class)) {
      return ID_FIELD_NAME;
    }
    return super.computePropertyName(field);
  }

}
