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
 * An exception that can be thrown during report processing, if an error occurs.
 *
 * @author Thomas Morgner
 */
public class ReportProcessingException extends Exception {
  /**
   *
   */
  private static final long serialVersionUID = 3416682538157508440L;

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public ReportProcessingException( final String message, final Throwable ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   * @param ex
   *          the parent exception.
   */
  public ReportProcessingException( final String message, final Exception ex ) {
    this( message, (Throwable) ex );
  }

  /**
   * Creates an exception.
   *
   * @param message
   *          the exception message.
   */
  public ReportProcessingException( final String message ) {
    super( message );
  }

  /**
   * Creates an exception.
   *
   * @param ex
   *          the parent exception.
   */
  public ReportProcessingException( final Throwable ex ) {
    super( ex );
  }

  /**
   * Creates an exception.
   *
   * @param ex
   *          the parent exception.
   */
  public ReportProcessingException( final Exception ex ) {
    super( ex );
  }

}
