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
package cn.vtohru.orm.dataaccess.query.impl;

import cn.vtohru.orm.dataaccess.query.IdField;

/**
 * Implementation of {@link IdField}
 * 
 * @author sschmitt
 * 
 */
public class IndexedIdField extends IndexedField implements IdField {

  public IndexedIdField(String name) {
    super(name);
  }
}