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
 * A class that handles the conversion of {@link Character} attributes to and from their {@link String} representation.
 *
 * @author Thomas Morgner
 */
public class CharacterValueConverter implements ValueConverter {

  /**
   * Creates a new value converter.
   */
  public CharacterValueConverter() {
  }

  /**
   * Converts the attribute to a string.
   *
   * @param o
   *          the attribute ({@link Character} expected).
   * @return A string representing the {@link Character} value.
   */
  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof Character ) {
      return o.toString();
    }
    throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a character." );
  }

  /**
   * Converts a string to a {@link Character}.
   *
   * @param s
   *          the string.
   * @return a {@link Character}.
   */
  public Object toPropertyValue( final String s ) throws BeanException {
    if ( s == null ) {
      throw new NullPointerException();
    }
    if ( s.length() == 0 ) {
      throw new BeanException( "A empty string cannot be converted into a char" );
    }
    return new Character( s.charAt( 0 ) );
  }
}
