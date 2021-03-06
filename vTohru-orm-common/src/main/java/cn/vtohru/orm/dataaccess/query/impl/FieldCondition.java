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

import cn.vtohru.orm.mapping.Json;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import cn.vtohru.orm.dataaccess.query.IFieldCondition;
import cn.vtohru.orm.dataaccess.query.IIndexedField;
import cn.vtohru.orm.dataaccess.query.QueryOperator;
import cn.vtohru.orm.dataaccess.query.exception.InvalidQueryValueException;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IProperty;
import io.micronaut.core.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of {@link IFieldCondition}<br>
 * <br>
 * Copyright: Copyright (c) 20.12.2016 <br>
 * Company: Braintags GmbH <br>
 *
 * @author sschmitt
 */

public class FieldCondition implements IFieldCondition {

  private final IIndexedField field;
  private final QueryOperator operator;
  private final JsonNode value;

  private final Map<Class<? extends IQueryExpression>, Object> cacheMap = new HashMap<>(1);

  /**
   * Creates a complete field condition
   *
   * @param field
   *          the field of this condition
   * @param logic
   *          the compare logic of this condition
   * @param value
   *          the value of this condition, can be null
   */
  public FieldCondition(final IIndexedField field, final QueryOperator logic, @Nullable final Object value) {
    this.field = field;
    this.operator = logic;
    this.value = transformObject(value);
    validateValue(value);
  }

  @JsonCreator
  protected FieldCondition(final IIndexedField field, final QueryOperator logic, @Nullable final JsonNode value) {
    this.field = field;
    this.operator = logic;
    this.value = value;
  }

  public static JsonNode transformObject(@Nullable final Object object) {
    JsonNode node = Json.mapper.convertValue(object, JsonNode.class);
    return node;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.query.IFieldCondition#setIntermediateResult(java.lang.Class,
   * java.lang.Object)
   */
  @Override
  @JsonIgnore
  public void setIntermediateResult(final Class<? extends IQueryExpression> queryExpressionClass, final Object result) {
    cacheMap.put(queryExpressionClass, result);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.query.IFieldCondition#getIntermediateResult(java.lang.Class)
   */
  @Override
  @JsonIgnore
  public Object getIntermediateResult(final Class<? extends IQueryExpression> queryExpressionClass) {
    return cacheMap.get(queryExpressionClass);
  }

  /**
   * @return the POJO field of this condition
   */
  @Override
  public IIndexedField getField() {
    return field;
  }

  /**
   * @return the compare logic of this condition
   */
  @Override
  public QueryOperator getOperator() {
    return operator;
  }

  /**
   * @return the value of this condition, can be null
   */
  @Override
  public JsonNode getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return field + " " + operator + " " + String.valueOf(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.query.ISearchCondition#validate(cn.vtohru.orm.mapping.
   * IMapper)
   */
  @Override
  public <T> void validate(final IMapper<T> mapper) {
    String fieldName = field.getFieldName();
    int dot = fieldName.indexOf('.');
    if (dot > 0) { // for now we are checking the base field only
      fieldName = fieldName.substring(0, dot);
    }
    IProperty p = mapper.getField(fieldName);
    if (p == null) {
      throw new RuntimeException(mapper + fieldName);
    }

    validateValue(null);
  }

  private void validateValue(final Object originalValue) {
    if (value != null) {
      if (value.isObject()) {
        if (originalValue != null) {
          if (!(originalValue instanceof GeoSearchArgument)) {
            throw new InvalidQueryValueException(
                "Only values that convert into primitive values, or GeoSearchArgument are allowed, not: "
                    + originalValue.getClass());
          }
        } else {
          // currently only geo search values are allowed, otherwise the value must convert to a primitive type
          try {
            Json.mapper.convertValue(value, GeoSearchArgument.class);
          } catch (Exception e) {
            throw new InvalidQueryValueException(
                "Only values that convert into primitive values, or GeoSearchArgument are allowed, not: " + value);
          }
        }
      } else if (value.isArray()) {
        ArrayNode arrayNode = (ArrayNode) value;
        for (JsonNode subNode : arrayNode) {
          if (subNode.isArray() || subNode.isObject())
            throw new InvalidQueryValueException(
                "Only primitive types are allowed inside arrays, not: " + subNode.getNodeType());
        }
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (field == null ? 0 : field.hashCode());
    result = prime * result + (operator == null ? 0 : operator.hashCode());
    result = prime * result + (value == null ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FieldCondition other = (FieldCondition) obj;
    if (field == null) {
      if (other.field != null)
        return false;
    } else if (!field.equals(other.field))
      return false;
    if (operator != other.operator)
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }
}
