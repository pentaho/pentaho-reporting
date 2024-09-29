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


package org.pentaho.reporting.libraries.repository;

/**
 * A Exception that indicates repository related errors.
 *
 * @author Thomas Morgner
 */
public class ContentIOException extends Exception {
  private static final long serialVersionUID = 2947843470210463760L;

  /**
   * Creates a ContentIOException with no message and no parent.
   */
  public ContentIOException() {
  }

  /**
   * Creates an ContentIOException.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ContentIOException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an ContentIOException.
   *
   * @param message the exception message.
   */
  public ContentIOException( final String message ) {
    super( message );
  }
}
