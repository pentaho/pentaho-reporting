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
 * Maps a string read from a column into an other string. The possible mappings are given as (key, text) pairs. If the
 * string from the column is null or matches none of the defined keys, a fallback value is returned.
 * <p/>
 * If the fallback value is undefined, the original value is returned instead.
 *
 * @author Thomas Morgner
 */
public class MapStringExpression extends AbstractExpression {
  /**
   * The field from where to read the key value.
   */
  private String field;
  /**
   * The list of possible keys.
   */
  private ArrayList keys;
  /**
   * The list of mapped values.
   */
  private ArrayList values;
  /**
   * A flag defining, whether the key-lookup should be case-insensitive.
   */
  private boolean ignoreCase;
  /**
   * The fallback value is returned if none of the defined keys matches.
   */
  private String fallbackValue;
  /**
   * The null-value is returned if the key-field evaluates to <code>null</code>.
   */
  private String nullValue;

  /**
   * Default Constructor.
   */
  public MapStringExpression() {
    keys = new ArrayList();
    values = new ArrayList();
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
   * Returns the value that is returned if none of the predefined keys matches the lookup-value.
   *
   * @return the fallback value.
   */
  public String getFallbackValue() {
    return fallbackValue;
  }

  /**
   * Defines the value that is returned if none of the predefined keys matches the lookup-value.
   *
   * @param fallbackValue
   *          the fallback value.
   */
  public void setFallbackValue( final String fallbackValue ) {
    this.fallbackValue = fallbackValue;
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
   * Defines the mapped text for the key at the given position. This text is returned if the key matches the value read
   * from the lookup-field column.
   *
   * @param index
   *          the index of the entry.
   * @param value
   *          the text that is returned if the key is selected.
   */
  public void setText( final int index, final String value ) {
    if ( values.size() == index ) {
      values.add( value );
    } else {
      values.set( index, value );
    }
  }

  /**
   * Returns the mapped text for the key at the given position. This text is returned if the key matches the value read
   * from the lookup-field column.
   *
   * @param index
   *          the index of the entry.
   * @return the text that is returned if the key is selected.
   */
  public String getText( final int index ) {
    return (String) values.get( index );
  }

  /**
   * Returns the number of replacement text defined in this expression. This should match the number of keys defined.
   *
   * @return the number of texts defined.
   */
  public int getTextCount() {
    return values.size();
  }

  /**
   * Returns all defined texts as string-array.
   *
   * @return all texts.
   */
  public String[] getText() {
    return (String[]) values.toArray( new String[values.size()] );
  }

  /**
   * Defines all texts by using the values from the given text-array.
   *
   * @param texts
   *          the new text-values.
   */
  public void setText( final String[] texts ) {
    this.values.clear();
    this.values.addAll( Arrays.asList( texts ) );
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
    final MapStringExpression co = (MapStringExpression) super.getInstance();
    co.values = (ArrayList) values.clone();
    co.keys = (ArrayList) keys.clone();
    return co;
  }

  /**
   * Performs the lookup by first querying the given field, and then returning the defined text for the key-position.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object raw = getDataRow().get( getField() );
    if ( raw == null ) {
      return getNullValue();
    }
    final String text = String.valueOf( raw );
    final int length = Math.min( keys.size(), values.size() );
    for ( int i = 0; i < length; i++ ) {
      final String key = (String) keys.get( i );
      if ( isIgnoreCase() ) {
        if ( text.equalsIgnoreCase( key ) ) {
          return values.get( i );
        }
      } else {
        if ( text.equals( key ) ) {
          return values.get( i );
        }
      }
    }
    final String fallbackValue = getFallbackValue();
    if ( fallbackValue != null ) {
      return fallbackValue;
    }
    return raw;
  }
}
