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

public class BundleWriterException extends Exception {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public BundleWriterException() {
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public BundleWriterException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public BundleWriterException( final String message ) {
    super( message );
  }
}
