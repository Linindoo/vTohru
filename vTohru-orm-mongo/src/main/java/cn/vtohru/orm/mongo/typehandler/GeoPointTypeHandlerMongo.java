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
package cn.vtohru.orm.mongo.typehandler;

import cn.vtohru.orm.dataaccess.query.impl.GeoSearchArgument;
import cn.vtohru.orm.datatypes.geojson.GeoPoint;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.typehandler.ITypeHandlerFactory;
import cn.vtohru.orm.typehandler.ITypeHandlerResult;
import cn.vtohru.orm.typehandler.stringbased.handlers.GeoPointTypeHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * @author Michael Remme
 */
public class GeoPointTypeHandlerMongo extends GeoPointTypeHandler {

  /**
   * @param typeHandlerFactory
   */
  public GeoPointTypeHandlerMongo(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  @Override
  public final void intoStore(Object source, IProperty field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source == null) {
      success(null, resultHandler);
    } else if (source instanceof GeoPoint) {
      success(encode((GeoPoint) source), resultHandler);
    } else if (source instanceof GeoSearchArgument) {
      success(encode((GeoSearchArgument) source), resultHandler);
    } else {
      fail(new UnsupportedOperationException("unsupported type: " + source.getClass().getName()), resultHandler);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see de.braintags.vertx.jomnigate.typehandler.stringbased.handlers.PointTypeHandler#fromStore(java.lang.Object,
   * de.braintags.vertx.jomnigate.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public void fromStore(Object source, IProperty field, Class<?> cls,
                        Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    success(source == null ? source : parse((JsonObject) source), resultHandler);
  }

}
