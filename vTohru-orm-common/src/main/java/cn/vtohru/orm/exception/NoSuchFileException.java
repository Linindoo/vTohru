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
 * Thrown when a certain file wasn't found
 *
 * @author Michael Remme
 *
 */
public class NoSuchFileException extends Exception {

  /**
   * Create a new exception with the path of a file
   *
   * @param path
   *          the path of the file which wasn't found
   */
  public NoSuchFileException(String path) {
    super("File wasn't found: " + path);
  }

  /**
   * Create a new exception with the path of a file
   *
   * @param message
   *          the message to be set
   * @param cause
   *          the original exception
   */
  public NoSuchFileException(String message, Throwable cause) {
    super(message, cause);
  }

}
