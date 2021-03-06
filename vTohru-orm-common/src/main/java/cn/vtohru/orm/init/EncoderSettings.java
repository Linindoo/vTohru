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
package cn.vtohru.orm.init;

import cn.vtohru.orm.impl.IEncoder;
import cn.vtohru.orm.util.ExceptionUtil;

import java.util.Map.Entry;
import java.util.Properties;

/**
 * EncoderSettings defines the properties of an {@link IEncoder}, which shall be used by the current application. The
 * definition is used in startup to initialize the encoders, so that they can be requested by the method
 * {@link IDataStore#getEncoder(String)}
 *
 * @author Michael Remme
 *
 */
public class EncoderSettings {
  private String name;
  private Class<? extends IEncoder> encoderClass;
  private Properties properties = new Properties();

  /**
   * The name is used to add the instance into the lookup in the {@link IDataStore}
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * The name is used to add the instance into the lookup in the {@link IDataStore}
   *
   * @param name
   *          the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * The class of the encoder
   *
   * @return the endoderClass
   */
  public Class<? extends IEncoder> getEncoderClass() {
    return encoderClass;
  }

  /**
   * The class of the encoder
   *
   * @param endoderClass
   *          the endoderClass to set
   */
  public void setEncoderClass(final Class<? extends IEncoder> endoderClass) {
    this.encoderClass = endoderClass;
  }

  /**
   * The properties, which are used to init the encoder
   *
   * @return the properties
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * The properties, which are used to init the encoder
   *
   * @param properties
   *          the properties to set
   */
  public void setProperties(final Properties properties) {
    this.properties = properties;
  }

  /**
   * Create an instance of {@link IEncoder} of the given information
   *
   * @return the created {@link IEncoder}
   */
  public IEncoder toEncoder() {
    try {
      IEncoder encoder = getEncoderClass().newInstance();
      encoder.init(getProperties());
      return encoder;
    } catch (Exception e) {
      throw ExceptionUtil.createRuntimeException(e);
    }
  }

  /**
   * Creates a deep (recursive) copy of the EncoderSettings
   */
  public EncoderSettings deepCopy() {
    EncoderSettings res = new EncoderSettings();
    res.name = this.name;
    res.encoderClass = this.encoderClass;
    res.properties = new Properties();
    for (Entry<Object, Object> property : properties.entrySet()) {
      res.properties.put(property.getKey(), property.getValue());
    }
    return res;
  }

}
