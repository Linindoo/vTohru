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
 * This exception marks, that there was a trial to create or add an object, which exists already
 *
 * @author Michael Remme
 *
 */
public class DuplicateObjectException extends RuntimeException {

  /**
   *
   */
  public DuplicateObjectException() {
  }

  /**
   * @param message
   */
  public DuplicateObjectException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public DuplicateObjectException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public DuplicateObjectException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public DuplicateObjectException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
