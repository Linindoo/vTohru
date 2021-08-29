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
 * An exception which is thrown, when a possible endless recursion is detected
 *
 * @author Michael Remme
 *
 */
public class EndlessRecursionException extends RuntimeException {

  /**
   *
   */
  public EndlessRecursionException() {
  }

  /**
   * @param message
   */
  public EndlessRecursionException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public EndlessRecursionException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public EndlessRecursionException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public EndlessRecursionException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
