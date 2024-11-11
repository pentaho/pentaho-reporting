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

import java.math.BigInteger;

/**
 * A class that handles the conversion of {@link java.math.BigInteger} attributes to and from their {@link String}
 * representation.
 *
 * @author Thomas Morgner
 */
public class BigIntegerValueConverter implements ValueConverter {

  /**
   * Creates a new value converter.
   */
  public BigIntegerValueConverter() {
  }

  /**
   * Converts the attribute to a string.
   *
   * @param o
   *          the attribute ({@link java.math.BigInteger} expected).
   * @return A string representing the {@link java.math.BigInteger} value.
   */
  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof BigInteger ) {
      return o.toString();
    }
    throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a big-integer." );
  }

  /**
   * Converts a string to a {@link java.math.BigInteger}.
   *
   * @param s
   *          the string.
   * @return a {@link java.math.BigInteger}.
   */
  public Object toPropertyValue( final String s ) throws BeanException {
    if ( s == null ) {
      throw new NullPointerException();
    }
    final String val = s.trim();
    if ( val.length() == 0 ) {
      throw BeanException.getInstance( "Failed to convert empty string to number", null );
    }

    try {
      return new BigInteger( val );
    } catch ( NumberFormatException be ) {
      throw BeanException.getInstance( "Failed to parse string as number", be );
    }
  }
}
