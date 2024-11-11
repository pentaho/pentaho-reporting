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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements ObjectConverter {
  public Object convertFromString( final String s, final Locator locator ) throws ParseException {
    if ( s == null ) {
      throw new ParseException( "s must not be null" );
    }

    try {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss.SSSZ" );
      simpleDateFormat.setLenient( false );
      return simpleDateFormat.parse( s );
    } catch ( Exception e ) {
      throw new ParseException( e, locator );
    }
  }

  public static Date getObject( String s ) throws ParseException {
    return (Date) new DateConverter().convertFromString( s, null );
  }


}
