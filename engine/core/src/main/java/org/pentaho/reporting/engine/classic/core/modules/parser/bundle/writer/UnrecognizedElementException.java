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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class UnrecognizedElementException extends BundleWriterException {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public UnrecognizedElementException() {
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public UnrecognizedElementException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public UnrecognizedElementException( final String message ) {
    super( message );
  }
}
