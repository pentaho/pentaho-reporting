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
 * A exception that indicates that a new ContentEntry could not be created in the repository.
 *
 * @author Thomas Morgner
 */
public class ContentCreationException extends ContentIOException {
  private static final long serialVersionUID = 8817127426694197164L;

  /**
   * Creates a ContentCreationException with no message and no parent.
   */
  public ContentCreationException() {
  }

  /**
   * Creates an ContentCreationException.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ContentCreationException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an ContentCreationException.
   *
   * @param message the exception message.
   */
  public ContentCreationException( final String message ) {
    super( message );
  }
}
