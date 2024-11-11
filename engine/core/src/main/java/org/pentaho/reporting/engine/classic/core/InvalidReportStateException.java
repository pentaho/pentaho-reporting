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


package org.pentaho.reporting.engine.classic.core;

/**
 * An exception that can be thrown during report processing, if an error occurs. This unconditionally aborts the report
 * processing.
 *
 * @author Thomas Morgner
 */
public class InvalidReportStateException extends RuntimeException {
  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public InvalidReportStateException( final String message, final Throwable ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public InvalidReportStateException( final String message ) {
    super( message );
  }

  public InvalidReportStateException( final Throwable cause ) {
    super( cause );
  }

  public InvalidReportStateException() {
  }
}
