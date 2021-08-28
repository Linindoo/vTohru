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
package cn.vtohru.orm.typehandler.stringbased;

import java.lang.annotation.Annotation;

import cn.vtohru.orm.typehandler.AbstractTypeHandlerFactory;
import cn.vtohru.orm.typehandler.ITypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.BigDecimalTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.BigIntegerTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.BooleanTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.ByteTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.CalendarTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.CharSequenceTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.CharacterTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.ClassTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.DateTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.DoubleTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.EnumTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.FloatTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.GeoPointTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.IntegerTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.JsonTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.LocaleTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.LongTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.ObjectTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.PriceTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.ShortTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.TimeTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.TimestampTypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.URITypeHandler;
import cn.vtohru.orm.typehandler.stringbased.handlers.URLTypeHandler;

/**
 * Creates {@link ITypeHandler} which are creating String from Objects and back
 * 
 * @author Michael Remme
 * 
 */

public class StringTypeHandlerFactory extends AbstractTypeHandlerFactory {
  private final ITypeHandler defaultHandler = new ObjectTypeHandler(this);

  /**
   * The default constructor for a String base factory
   */
  public StringTypeHandlerFactory() {
    init();
  }

  protected void init() {
    add(new BooleanTypeHandler(this));
    add(new JsonTypeHandler(this));
    add(new CharacterTypeHandler(this));
    add(new PriceTypeHandler(this));
    add(new BigDecimalTypeHandler(this));
    add(new BigIntegerTypeHandler(this));
    add(new FloatTypeHandler(this));
    add(new DoubleTypeHandler(this));
    add(new ShortTypeHandler(this));
    add(new IntegerTypeHandler(this));
    add(new LongTypeHandler(this));
    add(new TimestampTypeHandler(this));
    add(new TimeTypeHandler(this));
    add(new DateTypeHandler(this));
    add(new CalendarTypeHandler(this));
    add(new CharSequenceTypeHandler(this));
    add(new ByteTypeHandler(this));
    add(new URITypeHandler(this));
    add(new URLTypeHandler(this));
    add(new ClassTypeHandler(this));
    add(new LocaleTypeHandler(this));
    add(new EnumTypeHandler(this));
    add(new GeoPointTypeHandler(this));

  }

  @Override
  public ITypeHandler getDefaultTypeHandler(Annotation embedRef) {
    return defaultHandler;
  }

}
