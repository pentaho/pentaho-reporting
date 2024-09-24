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

package org.pentaho.reporting.libraries.css.resolver;

/**
 * Creation-Date: 16.04.2006, 15:54:16
 *
 * @author Thomas Morgner
 */
public class FunctionEvaluationException extends Exception {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public FunctionEvaluationException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public FunctionEvaluationException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public FunctionEvaluationException( final String message ) {
    super( message );
  }
}
