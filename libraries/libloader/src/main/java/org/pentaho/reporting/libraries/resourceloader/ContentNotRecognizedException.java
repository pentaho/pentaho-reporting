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


package org.pentaho.reporting.libraries.resourceloader;

/**
 * The ContentNotRecognizedException signals that none of the selected resource factories was able to handle the
 * request.
 *
 * @author Thomas Morgner
 */
public class ContentNotRecognizedException extends ResourceCreationException {
  private static final long serialVersionUID = 816828118909665976L;

  /**
   * Creates a ContentNotRecognizedException  with no message and no parent.
   */
  public ContentNotRecognizedException() {
  }

  /**
   * Creates an ContentNotRecognizedException.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ContentNotRecognizedException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an ContentNotRecognizedException.
   *
   * @param message the exception message.
   */
  public ContentNotRecognizedException( final String message ) {
    super( message );
  }
}
