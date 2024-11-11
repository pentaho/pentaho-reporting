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
 * A report data factory exception is thrown whenever querying a datasource failed for some reason.
 *
 * @author Thomas Morgner
 */
public class ReportDataFactoryException extends ReportProcessingException {
  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public ReportDataFactoryException( final String message ) {
    super( message );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public ReportDataFactoryException( final String message, final Throwable ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param ex
   *          the parent exception.
   */
  public ReportDataFactoryException( final Throwable ex ) {
    super( ex );
  }

  /**
   * Creates an exception.
   *
   * @param ex
   *          the parent exception.
   */
  public ReportDataFactoryException( final Exception ex ) {
    super( ex );
  }
}
