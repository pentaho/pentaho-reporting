/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

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
