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

package org.pentaho.reporting.engine.classic.core.function.strings;

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Returns the value read from a mapped field. The field's value is used as a key to the field-mapping. The expression
 * maps the value into a new column name and returns the value read from this column.
 * <p/>
 * If the mapping does not exist, then the fallback forward is used instead.
 *
 * @author Thomas Morgner
 */
public class MapIndirectExpression extends AbstractExpression {
  /**
   * The field from where to read the key value.
   */
  private String field;
  /**
   * The list of possible keys.
   */
  private ArrayList keys;
  /**
   * The list of target-fields.
   */
  private ArrayList forwards;
  /**
   * A flag defining, whether the key-lookup should be case-insensitive.
   */
  private boolean ignoreCase;
  /**
   * The fallback forward is used if none of the defined keys matches.
   */
  private String fallbackForward;
  /**
   * The null-value is returned if the key-field evaluates to <code>null</code>.
   */
  private String nullValue;

  /**
   * Default Constructor.
   */
  public MapIndirectExpression() {
    keys = new ArrayList();
    forwards = new ArrayList();
  }

  /**
   * Returns the name of the field from where to read the key value.
   *
   * @return the field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the field from where to read the key value.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Returns the value that is returned if the key-field evaluates to <code>null</code>.
   *
   * @return the null-value.
   */
  public String getNullValue() {
    return nullValue;
  }

  /**
   * Defines the value that is returned if the key-field evaluates to <code>null</code>.
   *
   * @param nullValue
   *          the null-value.
   */
  public void setNullValue( final String nullValue ) {
    this.nullValue = nullValue;
  }

  /**
   * Returns the name of the field that is returned if none of the predefined keys matches the lookup-value.
   *
   * @return the fallback forward field name.
   */
  public String getFallbackForward() {
    return fallbackForward;
  }

  /**
   * Defines the name of the field that is returned if none of the predefined keys matches the lookup-value.
   *
   * @param fallbackForward
   *          the fallback forward field name.
   */
  public void setFallbackForward( final String fallbackForward ) {
    this.fallbackForward = fallbackForward;
  }

  /**
   * Defines a key value to which the lookup-field's value is compared. If the key is defined, a matching value must be
   * defined too.
   *
   * @param index
   *          the index position of the key in the list.
   * @param key
   *          the key value.
   */
  public void setKey( final int index, final String key ) {
    if ( keys.size() == index ) {
      keys.add( key );
    } else {
      keys.set( index, key );
    }
  }

  /**
   * Returns a key value at the given index.
   *
   * @param index
   *          the index position of the key in the list.
   * @return the key value.
   */
  public String getKey( final int index ) {
    return (String) keys.get( index );
  }

  /**
   * Returns the number of keys defined in the expression.
   *
   * @return the number of keys.
   */
  public int getKeyCount() {
    return keys.size();
  }

  /**
   * Returns all defined keys as string array.
   *
   * @return all defined keys.
   */
  public String[] getKey() {
    return (String[]) keys.toArray( new String[keys.size()] );
  }

  /**
   * Defines all keys using the values from the string array.
   *
   * @param keys
   *          all defined keys.
   */
  public void setKey( final String[] keys ) {
    this.keys.clear();
    this.keys.addAll( Arrays.asList( keys ) );
  }

  /**
   * Defines the forward-fieldname for the key at the given position. The forward-field is read, if the lookup value
   * matches the key at this position. The forward-value must be a valid data-row column name.
   *
   * @param index
   *          the index of the entry.
   * @param value
   *          the name of the datarow-column that is read if the key is selected.
   */
  public void setForward( final int index, final String value ) {
    if ( forwards.size() == index ) {
      forwards.add( value );
    } else {
      forwards.set( index, value );
    }
  }

  /**
   * Retrieves the forward-fieldname for the key at the given position. The forward-field is read, if the lookup value
   * matches the key at this position. The forward-value must be a valid data-row column name.
   *
   * @param index
   *          the index of the entry.
   * @return the name of the datarow-column that is read if the key is selected.
   */
  public String getForward( final int index ) {
    return (String) forwards.get( index );
  }

  /**
   * Returns the number of forward-definitions that have been defined. This should match the number of keys.
   *
   * @return the number of forward definitions.
   */
  public int getForwardCount() {
    return forwards.size();
  }

  /**
   * Returns all forward-definitions as string-array.
   *
   * @return all forward-definitions.
   */
  public String[] getForward() {
    return (String[]) forwards.toArray( new String[forwards.size()] );
  }

  /**
   * Defiens all forward-definitions using the values of the string-array. The positions in the array must match the key
   * positions, or funny things will happen.
   *
   * @param forwards
   *          the forward-name array.
   */
  public void setForward( final String[] forwards ) {
    this.forwards.clear();
    this.forwards.addAll( Arrays.asList( forwards ) );
  }

  /**
   * Returns, whether the key-lookup should be case-insensitive.
   *
   * @return true, if the key comparison is case-insensitive, false otherwise.
   */
  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  /**
   * Defines, whether the key-lookup should be case-insensitive.
   *
   * @param ignoreCase
   *          true, if the key comparison is case-insensitive, false otherwise.
   */
  public void setIgnoreCase( final boolean ignoreCase ) {
    this.ignoreCase = ignoreCase;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final MapIndirectExpression co = (MapIndirectExpression) super.getInstance();
    co.forwards = (ArrayList) forwards.clone();
    co.keys = (ArrayList) keys.clone();
    return co;
  }

  /**
   * Performs the lookup by first querying the given field, and then mapping the retrived value into one of the field
   * names.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object raw = getDataRow().get( getField() );
    if ( raw == null ) {
      return getNullValue();
    }
    final String text = String.valueOf( raw );
    final int length = Math.min( keys.size(), forwards.size() );
    for ( int i = 0; i < length; i++ ) {
      final String key = (String) keys.get( i );
      if ( isIgnoreCase() ) {
        if ( text.equalsIgnoreCase( key ) ) {
          final String target = (String) forwards.get( i );
          return getDataRow().get( target );
        }
      } else {
        if ( text.equals( key ) ) {
          final String target = (String) forwards.get( i );
          return getDataRow().get( target );
        }
      }
    }
    final String fallbackValue = getFallbackForward();
    if ( fallbackValue != null ) {
      return getDataRow().get( fallbackValue );
    }
    return raw;
  }
}
