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

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * A class that handles the conversion of {@link Integer} attributes to and from their {@link String} representation.
 *
 * @author Thomas Morgner
 */
public class ColorValueConverter implements ValueConverter {
  private static final HashMap knownColorNamesByColor;
  private static final HashMap knownColorsByName;

  static {
    knownColorNamesByColor = new HashMap();
    knownColorsByName = new HashMap();
    try {
      final Field[] fields = Color.class.getFields();
      for ( int i = 0; i < fields.length; i++ ) {
        final Field f = fields[i];
        if ( Modifier.isPublic( f.getModifiers() ) && Modifier.isFinal( f.getModifiers() )
            && Modifier.isStatic( f.getModifiers() ) ) {
          final String name = f.getName();
          final Object oColor = f.get( null );
          if ( oColor instanceof Color ) {
            knownColorNamesByColor.put( oColor, name.toLowerCase() );
            knownColorsByName.put( name.toLowerCase(), oColor );
          }
        }
      }
    } catch ( Exception e ) {
      // ignore ..
    }
  }

  /**
   * Creates a new value converter.
   */
  public ColorValueConverter() {
    super();
  }

  /**
   * Converts the attribute to a string.
   *
   * @param o
   *          the attribute ({@link Integer} expected).
   * @return A string representing the {@link Integer} value.
   */
  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      return null;
    }

    if ( !( o instanceof Color ) ) {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a Color." );
    }
    final Color c = (Color) o;
    return colorToString( c );
  }

  /**
   * Converts a string to a {@link Integer}.
   *
   * @param value
   *          the string.
   * @return a {@link Integer}.
   */
  public Object toPropertyValue( final String value ) throws BeanException {
    if ( value == null ) {
      throw new NullPointerException();
    }

    final Object o = ColorValueConverter.knownColorsByName.get( value.toLowerCase() );
    if ( o != null ) {
      return o;
    }

    try {
      // get color by hex or octal value
      return Color.decode( value.trim() );
    } catch ( NumberFormatException nfe ) {
      // if we can't decode lets try to get it by name
      throw new BeanException( "Failed to parse color text as RGB-number.", nfe );
    }
  }

  public static String colorToString( final Color c ) {
    if ( c == null ) {
      return null;
    }

    final String name = (String) ColorValueConverter.knownColorNamesByColor.get( c );
    if ( name != null ) {
      return name;
    }

    // no defined constant color, so this must be a user defined color
    final String color = Integer.toHexString( c.getRGB() & 0x00ffffff );
    final StringBuffer retval = new StringBuffer( 7 );
    retval.append( '#' );

    final int fillUp = 6 - color.length();
    for ( int i = 0; i < fillUp; i++ ) {
      retval.append( '0' );
    }

    retval.append( color );
    return retval.toString();
  }
}
