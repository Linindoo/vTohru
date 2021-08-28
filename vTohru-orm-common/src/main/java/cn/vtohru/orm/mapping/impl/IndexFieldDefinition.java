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

import cn.vtohru.orm.annotation.IndexType;
import cn.vtohru.orm.mapping.IIndexFieldDefinition;

/**
 * Implementation of {@link IIndexFieldDefinition}
 * 
 * @author sschmitt
 *
 */

/**
 * @author sschmitt
 *
 */
public class IndexFieldDefinition implements IIndexFieldDefinition {

  private String name;
  private IndexType type;

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public IndexType getType() {
    return type;
  }

  public void setType(final IndexType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "IndexFieldDefinition [name=" + name + ", type=" + type + "]";
  }

}
