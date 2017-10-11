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

import java.util.TimeZone;

/**
 * Creation-Date: 24.01.2006, 19:19:03
 *
 * @author Thomas Morgner
 */
public class TimeZoneValueConverter implements ValueConverter {
  public TimeZoneValueConverter() {
  }

  /**
   * Converts an object to an attribute value.
   *
   * @param o
   *          the object.
   * @return the attribute value.
   * @throws BeanException
   *           if there was an error during the conversion.
   */
  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof TimeZone == false ) {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a timezone." );
    }
    final TimeZone tz = (TimeZone) o;
    return tz.getID();
  }

  /**
   * Converts a string to a property value.
   *
   * @param s
   *          the string.
   * @return a property value.
   * @throws BeanException
   *           if there was an error during the conversion.
   */
  public Object toPropertyValue( final String s ) throws BeanException {
    if ( s == null ) {
      throw new NullPointerException();
    }
    return TimeZone.getTimeZone( s );
  }
}
