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


package org.pentaho.reporting.engine.classic.core;

/**
 * The EmptyReportException is thrown, it the report processing generated no content.
 *
 * @author Thomas Morgner.
 */
public class EmptyReportException extends ReportProcessingException {
  /**
   * Creates an EmptyReportException.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public EmptyReportException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an EmptyReportException.
   *
   * @param message
   *          the exception message.
   */
  public EmptyReportException( final String message ) {
    super( message );
  }
}
