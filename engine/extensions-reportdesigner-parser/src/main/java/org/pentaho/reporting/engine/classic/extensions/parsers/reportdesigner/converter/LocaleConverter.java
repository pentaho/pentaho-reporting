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

import java.util.Locale;

public class LocaleConverter implements ObjectConverter {
  public Object convertFromString( final String s, final Locator locator ) throws ParseException {
    if ( s == null ) {
      throw new IllegalArgumentException( "s must not be null" );
    }

    int i = s.indexOf( '_' );
    int i2 = s.indexOf( '_', i + 1 );
    if ( i < 0 || i2 < 0 ) {
      throw new ParseException( "Invalid locale string" );
    }
    return new Locale( s.substring( 0, i ).trim(), s.substring( i + 1, i2 ).trim(), s.substring( i2 + 1 ).trim() );
  }

  public static Locale getObject( String s ) throws ParseException {
    return (Locale) new LocaleConverter().convertFromString( s, null );
  }
}
