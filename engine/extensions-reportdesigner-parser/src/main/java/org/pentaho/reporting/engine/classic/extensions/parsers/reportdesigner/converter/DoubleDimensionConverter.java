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

import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;

import java.awt.geom.Dimension2D;

public class DoubleDimensionConverter implements ObjectConverter {
  public Object convertFromString( final String s, final Locator locator ) throws ParseException {
    if ( s == null ) {
      throw new ParseException( "s must not be null" );
    }

    int i = s.indexOf( ',' );
    if ( i < 0 ) {
      throw new ParseException( "IllegalFormat" );
    }
    double d1 = Double.parseDouble( s.substring( 0, i ).trim() );
    double d2 = Double.parseDouble( s.substring( i + 1 ).trim() );
    return new FloatDimension( (float) d1, (float) d2 );
  }

  public static Dimension2D getObject( String s ) throws ParseException {
    return (Dimension2D) new DoubleDimensionConverter().convertFromString( s, null );
  }
}
