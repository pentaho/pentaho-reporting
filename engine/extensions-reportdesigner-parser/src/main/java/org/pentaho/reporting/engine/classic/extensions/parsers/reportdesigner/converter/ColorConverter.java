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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter;

import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;

import java.awt.*;

public class ColorConverter implements ObjectConverter {
  public static Color getObject( String s ) throws ParseException {
    return (Color) new ColorConverter().convertFromString( s, null );
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
    int i3 = s.indexOf( ',', i2 + 1 );
    if ( i3 == -1 ) {
      throw new ParseException( "Malformed format" );
    }

    int d1 = Integer.parseInt( s.substring( 0, i ).trim() );
    int d2 = Integer.parseInt( s.substring( i + 1, i2 ).trim() );
    int d3 = Integer.parseInt( s.substring( i2 + 1, i3 ).trim() );
    int d4 = Integer.parseInt( s.substring( i3 + 1 ).trim() );
    return new Color( d1, d2, d3, d4 );
  }
}
