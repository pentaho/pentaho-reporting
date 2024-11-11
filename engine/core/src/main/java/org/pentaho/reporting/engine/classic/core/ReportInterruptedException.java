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
 * This exception is thrown when the current thread received the Interrupt-signal while the report is beeing processed.
 * Depending on the ReportProcessor implementation such an signal would abort the report generation.
 *
 * @author Thomas Morgner
 */
public class ReportInterruptedException extends ReportProcessingException {
  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public ReportInterruptedException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public ReportInterruptedException( final String message ) {
    super( message );
  }
}
