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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * An object-description for a <code>Date</code> object.
 *
 * @author Thomas Morgner
 */
public class DateObjectDescription extends AbstractObjectDescription {
  private SimpleDateFormat dateFormat;

  /**
   * Creates a new object description.
   */
  public DateObjectDescription() {
    super( Date.class );
    dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US );

    setParameterDefinition( "year", Integer.class );
    setParameterDefinition( "month", Integer.class );
    setParameterDefinition( "day", Integer.class );
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final Object value = getParameter( "value" );
    if ( value != null ) {
      final String svalue = String.valueOf( value );
      try {
        return dateFormat.parse( svalue );
      } catch ( Exception e ) {
        return null;
      }
    } else {
      final int y = getIntParameter( "year" );
      final int m = getIntParameter( "month" );
      final int d = getIntParameter( "day" );

      return new GregorianCalendar( y, m, d ).getTime();
    }
  }

  /**
   * Returns a parameter value as an <code>int</code>.
   *
   * @param param
   *          the parameter name.
   * @return The parameter value.
   */
  private int getIntParameter( final String param ) {
    final Integer p = (Integer) getParameter( param );
    if ( p == null ) {
      return 0;
    }
    return p.intValue();
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object (should be an instance of <code>Date</code>).
   * @throws ObjectFactoryException
   *           if the object is not an instance of <code>Date</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( o instanceof Date ) {
      // final GregorianCalendar gc = new GregorianCalendar();
      // gc.setTime((Date) o);
      // final int year = gc.get(Calendar.YEAR);
      // final int month = gc.get(Calendar.MONTH);
      // final int day = gc.get(Calendar.DAY_OF_MONTH);
      //
      // setParameter("year", new Integer(year));
      // setParameter("month", new Integer(month));
      // setParameter("day", new Integer(day));
      setParameter( "value", dateFormat.format( (Date) o ) );
    } else {
      throw new ObjectFactoryException( "Is no instance of java.util.Date" );
    }

  }
}
