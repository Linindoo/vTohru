/*
 * #%L
 * Vert.x utilities from Braintags
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
 * Exception which is thrown when an error occured with a property
 *
 * @author Michael Remme
 *
 */

public class PropertyAccessException extends RuntimeException {

  /**
   *
   */
  public PropertyAccessException() {
    // empty
  }

  /**
   * @param message
   */
  public PropertyAccessException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public PropertyAccessException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public PropertyAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public PropertyAccessException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
