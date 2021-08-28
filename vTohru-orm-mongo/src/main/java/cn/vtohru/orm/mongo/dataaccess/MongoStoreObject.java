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

import cn.vtohru.orm.exception.MappingException;
import cn.vtohru.orm.mapping.IKeyGenerator;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.IStoreObject;
import cn.vtohru.orm.mapping.datastore.IColumnInfo;
import cn.vtohru.orm.mapping.impl.AbstractStoreObject;
import cn.vtohru.orm.mapping.IMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author Michael Remme
 */

public class MongoStoreObject<T> extends AbstractStoreObject<T,JsonObject> {
  private Object generatedId = null;

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   *
   * @param mapper
   *          the mapper to be used
   * @param entity
   *          the entity
   */
  public MongoStoreObject(final IMapper<T> mapper, final T entity, final JsonObject view) {
    super(mapper, entity, view);
  }

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   *
   * @param mapper
   *          the mapper to be used
   * @param entity
   *          the entity
   */
  public MongoStoreObject(final IMapper<T> mapper, final T entity) {
    super(mapper, entity, new JsonObject());
  }

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   *
   * @param json
   *          the json object coming from the datastore
   * @param mapper
   *          the mapper to be used
   */
  public MongoStoreObject(final JsonObject json, final IMapper<T> mapper) {
    super(json, mapper);
  }

  @Override
  public boolean hasProperty(IProperty field) {
    String colName = field.getColumnInfo().getName();
    return getContainer().containsKey(colName);
  }

  @Override
  public Object get(IProperty field) {
    String colName = field.getColumnInfo().getName();
    return getContainer().getValue(colName);
  }

  @Override
  public IStoreObject<T, JsonObject> put(IProperty field, Object value) {
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    if (ci == null) {
      throw new MappingException("Can't find columninfo for field " + field.getFullName());
    }
    if (field.isIdField() && value != null) {
      setNewInstance(false);
    }
    if (value == null) {
      getContainer().putNull(ci.getName());
    } else {
      getContainer().put(ci.getName(), value);
    }
    return this;
  }
  public void getNextId(final Handler<AsyncResult<Void>> handler) {
    IKeyGenerator gen = getMapper().getKeyGenerator();
    gen.generateKey(getMapper(), keyResult -> {
      if (keyResult.failed()) {
        handler.handle(Future.failedFuture(keyResult.cause()));
      } else {
        generatedId = keyResult.result().getKey();
        IProperty field = getMapper().getIdInfo().getField();
        this.put(field, String.valueOf(generatedId));
        setNewInstance(true);
        handler.handle(Future.succeededFuture());
      }
    });
  }


  public Object getGeneratedId() {
    return generatedId;
  }
}
