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
package cn.vtohru.orm.mapping;

/**
 * An accessor to a property within a Java class, to read and write the value of a field. The exact location and access
 * of the property is left to the implementation class.
 * 
 * @author Michael Remme
 * 
 */

public interface IPropertyAccessor {

  /**
   * The name of the underlaying property
   * 
   * @return the name
   */
  public String getName();

  /**
   * Reads the content from the given object
   * 
   * @param record
   *          the record from which to read the content
   * @return the content read
   * @exception PropertyAccessException
   *              thrown if the property cannot be accessed
   */
  Object readData(Object record);

  /**
   * Writes the content into the given record
   * 
   * @param record
   *          the record from which to read the content
   * @param data
   *          the data to be written
   * @exception PropertyAccessException
   *              thrown if the property cannot be accessed
   */
  void writeData(Object record, Object data);

}
