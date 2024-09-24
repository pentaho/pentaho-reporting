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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.engine.classic.core.util.PropertyLookupParser;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Anonymizer {
  private static class MessageParser extends PropertyLookupParser {
    private Random r;

    private MessageParser( final Random r ) {
      setMarkerChar( '$' );
      setOpeningBraceChar( '(' );
      setClosingBraceChar( ')' );
      this.r = r;
    }

    protected String lookupVariable( final String property ) {
      return property;
    }

    protected char postProcessCharacter( final char c ) {
      if ( Character.isWhitespace( c ) ) {
        return ' ';
      } else if ( Character.isUpperCase( c ) ) {
        return (char) ( r.nextInt( 'Z' - 'A' ) + 'A' );
      } else if ( Character.isLetter( c ) ) {
        return (char) ( r.nextInt( 'z' - 'a' ) + 'a' );
      } else {
        return ( c );
      }
    }
  }

  private Random r;
  private HashMap<Object, Object> mappedValues;

  public Anonymizer() {
    this.r = new Random();
    this.mappedValues = new HashMap<Object, Object>();
  }

  public Object anonymizeMessage( final Object attribute ) throws BeanException {
    if ( attribute instanceof String == false ) {
      return anonymize( attribute );
    }

    final String message = (String) attribute;
    final MessageParser mp = new MessageParser( r );
    return mp.translateAndLookup( message );
  }

  public Object anonymize( final Object value ) throws BeanException {
    final Object anonymized = anonymizeInternal( value );
    if ( value != anonymized ) {
      final Object reused = mappedValues.get( value );
      if ( reused != null ) {
        return reused;
      } else {
        mappedValues.put( value, anonymized );
        return anonymized;
      }
    } else {
      return anonymized;
    }
  }

  private Object anonymizeInternal( final Object value ) throws BeanException {
    if ( value == null ) {
      return null;
    }
    if ( value instanceof String ) {
      return generateRandomText( String.valueOf( value ) );
    }
    if ( value instanceof Number ) {
      final Number n = generateRandomNumber( (Number) value );
      return ConverterRegistry.toPropertyValue( ConverterRegistry.toAttributeValue( n ), value.getClass() );
    }
    if ( value instanceof Date ) {
      final Date n = generateRandomDate( (Date) value );
      return ConverterRegistry.toPropertyValue( ConverterRegistry.toAttributeValue( n ), value.getClass() );
    }
    // else admit defeat and return the ordinary value.
    return value;
  }

  private Date generateRandomDate( final Date value ) {
    final GregorianCalendar c = new GregorianCalendar();
    c.setTime( value );
    final int min = c.getActualMinimum( GregorianCalendar.DAY_OF_YEAR );
    final int max = c.getActualMaximum( GregorianCalendar.DAY_OF_YEAR );
    c.set( GregorianCalendar.DAY_OF_YEAR, r.nextInt( max - min ) + min );
    if ( value instanceof java.sql.Date ) {
      return c.getTime();
    }

    c.set( GregorianCalendar.HOUR_OF_DAY, 0 );
    c.set( GregorianCalendar.MINUTE, 0 );
    c.set( GregorianCalendar.SECOND, 0 );
    c.set( GregorianCalendar.MILLISECOND, 0 );
    final long time = c.getTime().getTime();
    return new Date( time + r.nextInt( 24 * 60 * 60 * 1000 ) );
  }

  private Number generateRandomNumber( final Number value )
    throws BeanException {
    final double v = value.doubleValue();
    final NumberFormat fmt;
    if ( v == value.longValue() ) {
      fmt = NumberFormat.getIntegerInstance( Locale.US );
    } else {
      fmt = NumberFormat.getNumberInstance( Locale.US );
    }

    final String format = fmt.format( value );
    final StringBuilder b = new StringBuilder( format );
    for ( int i = 0; i < b.length(); i += 1 ) {
      final char c = b.charAt( i );
      if ( Character.isDigit( c ) ) {
        b.setCharAt( i, (char) ( r.nextInt( '9' - '0' ) + '0' ) );
      } else {
        b.setCharAt( i, c );
      }
    }
    try {
      return fmt.parse( b.toString() );
    } catch ( ParseException e ) {
      throw new BeanException( "Failed to parse text", e );
    }
  }

  private Object generateRandomText( final String s ) {
    final StringBuilder b = new StringBuilder( s );
    for ( int i = 0; i < s.length(); i += 1 ) {
      final char c = s.charAt( i );
      if ( Character.isWhitespace( c ) ) {
        b.setCharAt( i, ' ' );
      } else if ( Character.isUpperCase( c ) ) {
        b.setCharAt( i, (char) ( r.nextInt( 'Z' - 'A' ) + 'A' ) );
      } else if ( Character.isLetter( c ) ) {
        b.setCharAt( i, (char) ( r.nextInt( 'z' - 'a' ) + 'a' ) );
      } else if ( Character.isDigit( c ) ) {
        b.setCharAt( i, (char) ( r.nextInt( '9' - '0' ) + '0' ) );
      } else {
        b.setCharAt( i, c );
      }
    }
    return b.toString();
  }
}
