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
package cn.vtohru.orm.dataaccess.query.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.vtohru.orm.dataaccess.query.IFieldValueResolver;
import cn.vtohru.orm.dataaccess.query.IIndexedField;
import cn.vtohru.orm.dataaccess.query.IVariableFieldCondition;
import cn.vtohru.orm.dataaccess.query.QueryOperator;
import io.micronaut.core.annotation.Nullable;

/**
 * A field condition that contains one ore more variables as value. The variable will be replaced with its actual value
 * by an {@link IFieldValueResolver} during the building of the {@link IQueryExpression}.
 * The value of this object is already just the actual name of the variable, without any identifying start- and end-tags
 *
 * @author sschmitt
 *
 */
public class VariableFieldCondition extends FieldCondition implements IVariableFieldCondition {

  /**
   * Creates a complete field condition
   *
   * @param field
   *          the field of this condition
   * @param logic
   *          the compare logic of this condition
   * @param value
   *          the value of this condition, must contain a variable
   */
  @JsonCreator
  public VariableFieldCondition(IIndexedField field, QueryOperator logic, @Nullable Object value) {
    super(field, logic, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.query.impl.FieldCondition#setIntermediateResult(java.lang.Class,
   * java.lang.Object)
   */
  @Override
  @JsonIgnore
  public void setIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass, Object result) {
    // never cache a field condition that contains a variable
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.query.impl.FieldCondition#getIntermediateResult(java.lang.Class)
   */
  @Override
  @JsonIgnore
  public Object getIntermediateResult(Class<? extends IQueryExpression> queryExpressionClass) {
    // never cache a field condition that contains a variable
    return null;
  }

}
