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


package org.pentaho.reporting.engine.classic.core.layout.output;

/**
 * Creation-Date: 08.05.2007, 20:08:45
 *
 * @author Thomas Morgner
 */
public class ContentProcessingException extends Exception {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public ContentProcessingException() {
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public ContentProcessingException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public ContentProcessingException( final String message ) {
    super( message );
  }

  public ContentProcessingException( final Throwable cause ) {
    super( cause );
  }
}
