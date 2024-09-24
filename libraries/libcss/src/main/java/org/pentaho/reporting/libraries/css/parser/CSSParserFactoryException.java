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

package org.pentaho.reporting.libraries.css.parser;

/**
 * Creation-Date: 25.11.2005, 17:56:16
 *
 * @author Thomas Morgner
 */
public class CSSParserFactoryException extends Exception {
  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public CSSParserFactoryException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public CSSParserFactoryException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public CSSParserFactoryException( final String message ) {
    super( message );
  }
}
