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
package cn.vtohru.orm.exception;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class NoSuchMapperException extends RuntimeException {

  public NoSuchMapperException() {

  }

  public NoSuchMapperException(String mapperName) {
    super("Mapper does not exist with name '" + mapperName + "'");
  }

  public NoSuchMapperException(String mapperName, Throwable e) {
    super("Mapper does not exist with name '" + mapperName + "'", e);
  }

}
