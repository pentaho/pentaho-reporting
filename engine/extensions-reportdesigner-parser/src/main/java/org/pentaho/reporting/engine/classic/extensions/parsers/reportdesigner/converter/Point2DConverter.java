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

import java.awt.geom.Point2D;

public class Point2DConverter implements ObjectConverter {

  public Object convertFromString( final String s, final Locator locator ) throws ParseException {
    if ( s == null ) {
      throw new IllegalArgumentException( "s must not be null" );
    }

    int i = s.indexOf( ',' );
    if ( i == -1 ) {
      throw new ParseException( "Malformed format" );
    }

    double d1 = Double.parseDouble( s.substring( 0, i ).trim() );
    double d2 = Double.parseDouble( s.substring( i + 1 ).trim() );
    return new Point2D.Double( d1, d2 );
  }
}
