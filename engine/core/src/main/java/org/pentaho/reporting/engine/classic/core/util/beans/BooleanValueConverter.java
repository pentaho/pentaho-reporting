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

/**
 * A class that handles the conversion of {@link Boolean} attributes to and from their {@link String} representation.
 *
 * @author Thomas Morgner
 */
public class BooleanValueConverter implements ValueConverter {

  /**
   * Creates a new value converter.
   */
  public BooleanValueConverter() {
  }

  /**
   * Converts the attribute to a string.
   *
   * @param o
   *          the attribute ({@link Boolean} expected).
   * @return A string representing the {@link Boolean} value.
   */
  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof Boolean ) {
      return o.toString();
    }
    throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a boolean." );
  }

  /**
   * Converts a string to a {@link Boolean}.
   *
   * @param s
   *          the string.
   * @return a {@link Boolean}.
   */
  public Object toPropertyValue( final String s ) {
    if ( s == null ) {
      throw new NullPointerException();
    }
    return Boolean.valueOf( s.trim() );
  }
}
