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


package org.pentaho.reporting.libraries.fonts;

/**
 * Creation-Date: 06.05.2006, 15:51:29
 *
 * @author Thomas Morgner
 */
public class FontException extends Exception {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public FontException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public FontException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public FontException( final String message ) {
    super( message );
  }
}
