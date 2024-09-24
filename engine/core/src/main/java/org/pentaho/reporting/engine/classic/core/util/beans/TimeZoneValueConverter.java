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
