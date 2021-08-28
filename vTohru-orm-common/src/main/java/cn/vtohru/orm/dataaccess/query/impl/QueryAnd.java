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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.vtohru.orm.dataaccess.query.ISearchCondition;
import cn.vtohru.orm.dataaccess.query.QueryLogic;

/**
 * Represents a container that joins search conditions with an {@link QueryLogic#AND}<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 * 
 * @author sschmitt
 */
public class QueryAnd extends AbstractSearchConditionContainer {

  /**
   * Initializes the container with zero or more sub conditions that will be connected with {@link QueryLogic#AND}
   * 
   * @param conditions
   */
  @JsonCreator
  public QueryAnd(ISearchCondition... searchConditions) {
    super(searchConditions);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.query.ISearchConditionContainer#getQueryLogic()
   */
  @Override
  @JsonIgnore
  public QueryLogic getQueryLogic() {
    return QueryLogic.AND;
  }

}
