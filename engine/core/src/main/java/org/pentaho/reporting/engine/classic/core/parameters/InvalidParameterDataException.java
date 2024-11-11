/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.parameters;

/**
 * This exception is thrown to indicate that the value entered is wrong, not understood or otherwise invalid.
 *
 * @author Thomas Morgner
 */
public class InvalidParameterDataException extends Exception {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public InvalidParameterDataException() {
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public InvalidParameterDataException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public InvalidParameterDataException( final String message ) {
    super( message );
  }
}
