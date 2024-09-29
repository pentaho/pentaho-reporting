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
 * Creation-Date: 23.11.2005, 12:57:15
 *
 * @author Thomas Morgner
 */
public class CSSParserInstantiationException extends Exception {
  public CSSParserInstantiationException() {
  }

  public CSSParserInstantiationException( final String message, final Exception ex ) {
    super( message, ex );
  }

  public CSSParserInstantiationException( final String message ) {
    super( message );
  }
}
