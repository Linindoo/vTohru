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
 * Exception is raised when a problem occurs during access of a method
 *
 * @author Michael Remme
 *
 */

public class MethodAccessException extends RuntimeException {

  /**
   *
   */
  public MethodAccessException() {
    // empty
  }

  /**
   * @param message
   */
  public MethodAccessException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public MethodAccessException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public MethodAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public MethodAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
