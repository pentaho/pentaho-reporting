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


package org.pentaho.reporting.libraries.fonts.itext;

/**
 * The BaseFontCreateException is thrown if there are problemns while creating iText fonts.
 *
 * @author Thomas Morgner
 */
public class BaseFontCreateException extends RuntimeException {
  /**
   * Creates a new BaseFontCreateException with no message.
   */
  public BaseFontCreateException() {
  }

  /**
   * Creates a new BaseFontCreateException with the given message and base exception.
   *
   * @param s the message for this exception
   * @param e the exception that caused this exception.
   */
  public BaseFontCreateException( final String s, final Exception e ) {
    super( s, e );
  }

  /**
   * Creates a new BaseFontCreateException with the given message.
   *
   * @param s the message for this exception
   */
  public BaseFontCreateException( final String s ) {
    super( s );
  }
}
