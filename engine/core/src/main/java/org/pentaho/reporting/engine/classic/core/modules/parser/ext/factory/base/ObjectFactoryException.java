/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

/**
 * An exception that is thrown, if the creation of an Object failed in the ObjectFactory implementation.
 *
 * @author Thomas Morgner.
 */
public class ObjectFactoryException extends Exception {

  /**
   * Constructs a new exception with <code>null</code> as its detail message. The cause is not initialized, and may
   * subsequently be initialized by a call to {@link #initCause}.
   */
  public ObjectFactoryException() {
    super();
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently be
   * initialized by a call to {@link #initCause}.
   *
   * @param message
   *          the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
   */
  public ObjectFactoryException( final String message ) {
    super( message );
  }

  /**
   * Creates a new exception.
   *
   * @param message
   *          the message.
   * @param cause
   *          the cause of the exception.
   */
  public ObjectFactoryException( final String message, final Exception cause ) {
    super( message, cause );
  }
}
