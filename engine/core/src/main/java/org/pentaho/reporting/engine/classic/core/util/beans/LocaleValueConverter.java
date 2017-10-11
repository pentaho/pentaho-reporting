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

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Creation-Date: 24.01.2006, 19:19:03
 *
 * @author Thomas Morgner
 */
public class LocaleValueConverter implements ValueConverter {
  public LocaleValueConverter() {
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
    if ( o instanceof Locale == false ) {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a Locale." );
    }

    final Locale l = (Locale) o;
    if ( "".equals( l.getCountry() ) ) {
      return l.getLanguage();
    } else if ( "".equals( l.getVariant() ) ) {
      return l.getLanguage() + '_' + l.getCountry();
    } else {
      return l.getLanguage() + '_' + l.getCountry() + '_' + l.getVariant();
    }
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
    final StringTokenizer strtok = new StringTokenizer( s.trim(), "_" );
    if ( strtok.hasMoreElements() == false ) {
      throw new BeanException( "This is no valid locale specification." );
    }
    final String language = strtok.nextToken();
    String country = "";
    if ( strtok.hasMoreTokens() ) {
      country = strtok.nextToken();
    }
    String variant = "";
    if ( strtok.hasMoreTokens() ) {
      variant = strtok.nextToken();
    }
    return new Locale( language, country, variant );
  }
}
