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

package org.pentaho.reporting.engine.classic.core.util.beans;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Creation-Date: 09.10.2007, 19:00:02
 *
 * @author Thomas Morgner
 */
public class SQLTimestampValueConverter implements ValueConverter {
  private SimpleDateFormat dateFormat;

  public SQLTimestampValueConverter() {
    dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US );
    dateFormat.setLenient( false );
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof Timestamp == false ) {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a SQL-Timestamp." );
    }
    final DateFormat format = (DateFormat) dateFormat.clone();
    return format.format( (Timestamp) o );
  }

  public Object toPropertyValue( final String s ) throws BeanException {
    if ( s == null ) {
      throw new NullPointerException();
    }
    try {
      final DateFormat format = (DateFormat) dateFormat.clone();
      final java.util.Date date = format.parse( s );
      return new Timestamp( date.getTime() );
    } catch ( ParseException e ) {
      throw new BeanException( "Not a parsable SQL-Timestamp" );
    }
  }
}
