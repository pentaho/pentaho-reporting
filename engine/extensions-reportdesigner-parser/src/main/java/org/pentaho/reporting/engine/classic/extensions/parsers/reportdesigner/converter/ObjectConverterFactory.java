/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter;

import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class ObjectConverterFactory {
  private static ObjectConverterFactory instance;

  private HashMap<Class, ObjectConverter> converters;

  private ObjectConverterFactory() {
    converters = new HashMap<Class, ObjectConverter>();
    converters.put( Double.class, new DoubleConverter() );
    converters.put( Double.TYPE, new DoubleConverter() );
    converters.put( Number.class, new DoubleConverter() );
    converters.put( Integer.class, new IntegerConverter() );
    converters.put( Integer.TYPE, new IntegerConverter() );
    converters.put( Float.class, new FloatConverter() );
    converters.put( Float.TYPE, new FloatConverter() );
    converters.put( Boolean.class, new BooleanConverter() );
    converters.put( Boolean.TYPE, new BooleanConverter() );
    converters.put( String.class, new StringConverter() );
    converters.put( Long.class, new LongConverter() );
    converters.put( Long.TYPE, new LongConverter() );
    converters.put( TimeZone.class, new TimezoneConverter() );
    converters.put( Date.class, new DateConverter() );
    converters.put( Dimension2D.class, new DoubleDimensionConverter() );
    converters.put( Point2D.Double.class, new Point2DConverter() );
    converters.put( Rectangle2D.Double.class, new Rectangle2DConverter() );
    converters.put( Locale.class, new LocaleConverter() );
    converters.put( Color.class, new ColorConverter() );
    converters.put( URL.class, new URLConverter() );
  }

  public static Object convert( final Class c, final String s, final Locator locator ) throws ParseException {
    if ( instance == null ) {
      instance = new ObjectConverterFactory();
    }

    final ObjectConverter o = instance.converters.get( c );
    if ( o == null ) {
      throw new ParseException( "Invalid type: " + c );
    }
    return o.convertFromString( s, locator );
  }
}
