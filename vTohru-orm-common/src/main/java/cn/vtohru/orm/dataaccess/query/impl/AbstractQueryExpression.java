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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import cn.vtohru.orm.dataaccess.query.IFieldCondition;
import cn.vtohru.orm.dataaccess.query.IFieldValueResolver;
import cn.vtohru.orm.dataaccess.query.ISearchCondition;
import cn.vtohru.orm.dataaccess.query.ISearchConditionContainer;
import cn.vtohru.orm.dataaccess.query.IVariableFieldCondition;
import cn.vtohru.orm.dataaccess.query.exception.InvalidQueryValueException;
import cn.vtohru.orm.dataaccess.query.exception.UnknownQueryLogicException;
import cn.vtohru.orm.dataaccess.query.exception.UnknownQueryOperatorException;
import cn.vtohru.orm.dataaccess.query.exception.UnknownSearchConditionException;
import cn.vtohru.orm.dataaccess.query.exception.VariableSyntaxException;
import cn.vtohru.orm.exception.QueryParameterException;
import cn.vtohru.orm.mapping.IMapper;
import io.vertx.core.*;

/**
 * Abstract implementation of {@link IQueryExpression}
 *
 *          the internal result type of the search condition building process
 *
 * @author sschmitt
 *
 */
public abstract class AbstractQueryExpression<T> implements IQueryExpression {

  private int limit;
  private int offset;
  private IMapper<?> mapper;

  /*
   * (non-Javadoc)
   *
   * @see
   * cn.vtohru.orm.dataaccess.query.impl.IQueryExpression#buildQueryExpression(de.braintags.vertx.
   * pojomapper.dataaccess.query.ISearchCondition, io.vertx.core.Handler)
   */
  @Override
  public void buildSearchCondition(final ISearchCondition searchCondition, final IFieldValueResolver resolver,
      final Handler<AsyncResult<Void>> handler) {
    internalBuildSearchCondition(searchCondition, resolver, result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        handleFinishedSearchCondition(result.result());
        handler.handle(Future.succeededFuture());
      }
    });
  }

  /**
   * Method to process the final result of the search condition building process, mostly to save it to a field and/or do
   * some final manipulation
   *
   * @param result
   *          the final result of the search condition build process
   */
  protected abstract void handleFinishedSearchCondition(T result);

  /**
   * Build the abstract search condition into the native search condition of the query. Can be used recursively for
   * conditions that contain more than one sub condition (AND, OR, ..)
   * If the condition is an {@link IFieldCondition}, and was already built before and cached, this method
   * directly returns the result without rebuilding it
   *
   * @param searchCondition
   *          the query search condition
   * @param handler
   *          returns the internal object that represents the given search condition
   */
  @SuppressWarnings("unchecked")
  protected void internalBuildSearchCondition(final ISearchCondition searchCondition,
      final IFieldValueResolver resolver,
      final Handler<AsyncResult<T>> handler) {
    if (searchCondition instanceof IFieldCondition) {
      IFieldCondition fieldCondition = (IFieldCondition) searchCondition;
      Object cachedResult = fieldCondition.getIntermediateResult(getClass());
      if (cachedResult != null) {
        /*
         * the returned result is specific to the query expression class, so only the correct
         * result type should be returned
         */
        handler.handle(Future.succeededFuture((T) cachedResult));
      } else {
        parseFieldCondition(fieldCondition, resolver, result -> {
          if (result.failed()) {
            handler.handle(Future.failedFuture(result.cause()));
          } else {
            // cache the result in the search condition before forwarding it to the handler
            fieldCondition.setIntermediateResult(getClass(), result.result());
            handler.handle(Future.succeededFuture(result.result()));
          }
        });
      }
    } else if (searchCondition instanceof ISearchConditionContainer) {
      parseSearchConditionContainer((ISearchConditionContainer) searchCondition, resolver, handler);
    } else {
      handler.handle(Future.failedFuture(new UnknownSearchConditionException(searchCondition)));
    }
  }

  /**
   * Parses a {@link IFieldCondition}. The operator and value will be transformed into a format fitting the concrete
   * database. The java field name will be converted to the matching column name of the database. If its an
   * {@link IVariableFieldCondition}, the variable will be resolved with the given resolver
   *
   * @param fieldCondition
   *          the field condition to parse
   * @param handler
   *          returns the internal object that represents the given field condition
   */
  protected void parseFieldCondition(final IFieldCondition fieldCondition, final IFieldValueResolver resolver,
      final Handler<AsyncResult<T>> handler) {
    String columnName = fieldCondition.getField().getColumnName(getMapper());
    JsonNode fieldValue = fieldCondition.getValue();
    if (fieldValue != null) {
      if (fieldCondition instanceof IVariableFieldCondition) {
        try {
          fieldValue = FieldCondition.transformObject(resolver.resolve(fieldValue.textValue()));
        } catch (VariableSyntaxException | InvalidQueryValueException e) {
          handler.handle(Future.failedFuture(e));
          return;
        }
      }
      try {
        T fieldConditionResult = buildFieldConditionResult(fieldCondition, columnName, fieldValue);
        handler.handle(Future.succeededFuture(fieldConditionResult));
      } catch (UnknownQueryOperatorException | QueryParameterException | InvalidQueryValueException e) {
        handler.handle(Future.failedFuture(e));
      }
    } else {
      handleNullConditionValue(fieldCondition, columnName, handler);
    }
  }

  /**
   * Build the result of a field condition with the parsed value
   *
   * @param fieldCondition
   *          the field condition to parse
   * @param columnName
   *          the column name of the database
   * @param parsedValue
   *          the value after being transformed by the matching type handler
   * @return
   */
  protected abstract T buildFieldConditionResult(IFieldCondition fieldCondition, String columnName,
      JsonNode parsedValue) throws UnknownQueryOperatorException;

  /**
   * Special case for null values, to avoid costly, unneeded transformation
   *
   * @param fieldCondition
   *          the field condition to parse
   * @param columnName
   *          the column name of the database
   * @param handler
   *          returns the result of the parse process, or an error if e.g. the operator or the database can not handle
   *          null values
   */
  protected abstract void handleNullConditionValue(IFieldCondition fieldCondition, final String columnName,
      Handler<AsyncResult<T>> handler);

  /**
   * Parses the container part of a search condition. Loops through the content of the container and connects each
   * resulting condition with the specified connector.
   *
   * @param container
   *          the container to parse
   * @param handler
   *          returns the result of the condition parsing
   */
  protected void parseSearchConditionContainer(final ISearchConditionContainer container,
      final IFieldValueResolver resolver,
      final Handler<AsyncResult<T>> handler) {
    @SuppressWarnings("rawtypes")
    List<Future> futures = new ArrayList<>();
    for (ISearchCondition searchCondition : container.getConditions()) {
      Promise<T> promise = Promise.promise();
      futures.add(promise.future());
      internalBuildSearchCondition(searchCondition, resolver, promise);
    }

    CompositeFuture.all(futures).onComplete(result -> {
      if (result.failed()) {
        handler.handle(Future.failedFuture(result.cause()));
      } else {
        List<T> parsedConditionList = result.result().list();
        T parsedContainer;
        try {
          parsedContainer = parseContainerContents(parsedConditionList, container);
          handler.handle(Future.succeededFuture(parsedContainer));
        } catch (UnknownQueryLogicException e) {
          handler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  /**
   * Builds the parse result containing all parsed conditions of a container
   *
   * @param parsedConditionList
   *          the parsed search conditions of the container
   * @param container
   *          the container to parse
   * @return
   */
  protected abstract T parseContainerContents(List<T> parsedConditionList, ISearchConditionContainer container)
      throws UnknownQueryLogicException;

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.query.impl.IQueryExpression#setLimit(int, int)
   */
  @Override
  public void setLimit(final int limit, final int offset) {
    this.limit = limit;
    this.offset = offset;
  }

  /**
   * @return the limit for this query
   */
  @Override
  public int getLimit() {
    return limit;
  }

  /**
   * @return the offset (starting position) for this query
   */
  @Override
  public int getOffset() {
    return offset;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * cn.vtohru.orm.dataaccess.query.impl.IQueryExpression#setMapper(cn.vtohru.orm.
   * mapping.IMapper)
   */
  @Override
  public void setMapper(final IMapper<?> mapper) {
    this.mapper = mapper;
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.query.impl.IQueryExpression#getMapper()
   */
  @Override
  public IMapper<?> getMapper() {
    return mapper;
  }
}
