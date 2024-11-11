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


package org.pentaho.reporting.engine.classic.core.parameters;

/**
 * This exception is thrown to indicate that the parameter needs more data to produce a sensible value.
 *
 * @author Thomas Morgner
 */
public class NeedMoreDataException extends Exception {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public NeedMoreDataException() {
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public NeedMoreDataException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public NeedMoreDataException( final String message ) {
    super( message );
  }
}
