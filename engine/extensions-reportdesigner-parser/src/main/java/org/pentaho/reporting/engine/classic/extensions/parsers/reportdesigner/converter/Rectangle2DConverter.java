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

import java.awt.geom.Rectangle2D;

public class Rectangle2DConverter implements ObjectConverter {

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

    double d1 = Double.parseDouble( s.substring( 0, i ).trim() );
    double d2 = Double.parseDouble( s.substring( i + 1, i2 ).trim() );
    double d3 = Double.parseDouble( s.substring( i2 + 1, i3 ).trim() );
    double d4 = Double.parseDouble( s.substring( i3 + 1 ).trim() );
    return new Rectangle2D.Double( d1, d2, d3, d4 );
  }
}
