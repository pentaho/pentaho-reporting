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

import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * An ValueConverter that handles Arrays. Conversion to arrays is done using a CSV string.
 *
 * @author Thomas Morgner
 */
public class ArrayValueConverter implements ValueConverter {
  /**
   * The converter for the array elements.
   */
  private ValueConverter elementConverter;
  /**
   * The element type.
   */
  private Class elementType;

  /**
   * Creates a new ArrayValueConverter for the given element type and array type.
   *
   * @param arrayClass
   *          the array type
   * @param elementConverter
   *          the value converter for the array elements.
   */
  public ArrayValueConverter( final Class arrayClass, final ValueConverter elementConverter ) {
    if ( elementConverter == null ) {
      throw new NullPointerException( "elementConverter must not be null" );
    }
    if ( arrayClass == null ) {
      throw new NullPointerException( "arrayClass must not be null" );
    }
    this.elementType = arrayClass;
    this.elementConverter = elementConverter;
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
      throw new NullPointerException( "Value must not be null" );
    }
    if ( o.getClass().isArray() == false ) {
      throw new BeanException( "Value must be a array" );
    }

    final int size = Array.getLength( o );
    final StringBuilder buffer = new StringBuilder( size * 25 );
    final CSVQuoter quoter = new CSVQuoter( ',', '"' );
    for ( int i = 0; i < size; i++ ) {
      if ( i != 0 ) {
        buffer.append( ',' );
      }
      final Object o1 = Array.get( o, i );
      if ( o1 != null ) {
        final String original = elementConverter.toAttributeValue( o1 );
        if ( original.length() == 0 ) {
          buffer.append( "\"\"" );
        } else {
          buffer.append( quoter.doQuoting( original ) );
        }
      }
    }
    return buffer.toString();
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

    final CSVTokenizer tokenizer = new CSVTokenizer( s, false );
    final ArrayList<Object> elements = new ArrayList<Object>();
    while ( tokenizer.hasMoreTokens() ) {
      final String token = tokenizer.nextToken();
      if ( token == null || token.length() == 0 ) {
        elements.add( null );
      } else {
        elements.add( elementConverter.toPropertyValue( token ) );
      }
    }

    final Object retval = Array.newInstance( elementType, elements.size() );
    for ( int i = 0; i < elements.size(); i++ ) {
      final Object o = elements.get( i );
      Array.set( retval, i, o );
    }
    return retval;
  }
}
