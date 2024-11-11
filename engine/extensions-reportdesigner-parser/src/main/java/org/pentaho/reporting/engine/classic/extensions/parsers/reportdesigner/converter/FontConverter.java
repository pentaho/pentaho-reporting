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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter;

import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;

import java.awt.*;

public class FontConverter implements ObjectConverter {
  public FontConverter() {
  }

  public Object convertFromString( final String s, final Locator locator ) throws ParseException {
    if ( s == null ) {
      throw new IllegalArgumentException( "s must not be null" );
    }

    int i = s.indexOf( ',' );
    if ( i == -1 ) {
      throw new ParseException( "Malformed format" );
    }
    int i2 = s.indexOf( ',', i + 1 );
    if ( i2 == -1 ) {
      throw new ParseException( "Malformed format" );
    }
    return new Font( s.substring( 0, i ).trim(),
      Integer.parseInt( s.substring( i2 + 1 ).trim() ), Integer.parseInt( s.substring( i + 1, i2 ).trim() ) );
  }
}
