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

package org.pentaho.reporting.libraries.fonts.encoding;

/**
 * Creation-Date: 20.04.2006, 16:44:13
 *
 * @author Thomas Morgner
 */
public class EncodingErrorType {
  public static final EncodingErrorType IGNORE = new EncodingErrorType( "IGNORE" );
  public static final EncodingErrorType FAIL = new EncodingErrorType( "FAIL" );
  public static final EncodingErrorType REPLACE = new EncodingErrorType( "REPLACE" );

  private final String myName; // for debug only

  private EncodingErrorType( final String name ) {
    myName = name;
  }

  public String toString() {
    return myName;
  }
}
