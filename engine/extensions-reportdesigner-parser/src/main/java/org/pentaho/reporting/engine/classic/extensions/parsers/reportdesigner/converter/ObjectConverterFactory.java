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
