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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a static datarow holding a value for each name in the datarow. This datarow does not hold dataflags and thus
 * does not track the changes done to the data inside.
 * <p/>
 * The StaticDataRow is a derived view and is used to provide a safe collection of the values of the previous datarow.
 *
 * @author Thomas Morgner
 */
public class StaticDataRow implements DataRow {
  private static final String[] EMPTY_NAMES = new String[0];
  private String[] names;
  private Map<String, Object> values;

  public StaticDataRow() {
    values = Collections.emptyMap();
    names = StaticDataRow.EMPTY_NAMES;
  }

  protected StaticDataRow( final StaticDataRow dataRow ) {
    if ( dataRow == null ) {
      throw new NullPointerException();
    }

    this.names = dataRow.names;
    this.values = dataRow.values;
  }

  public StaticDataRow( final DataRow dataRow ) {
    if ( dataRow == null ) {
      throw new NullPointerException();
    }

    synchronized ( dataRow ) {
      final String[] columnNames = dataRow.getColumnNames();
      final int columnCount = columnNames.length;
      this.names = columnNames.clone();
      final HashMap<String, Object> values = new HashMap<String, Object>();
      for ( int i = 0; i < columnCount; i++ ) {
        final String name = columnNames[i];
        values.put( name, dataRow.get( name ) );
      }
      this.values = Collections.unmodifiableMap( values );
    }
  }

  public StaticDataRow( final String[] names, final Object[] values ) {
    setData( names, values );
  }

  public StaticDataRow( final Map<String, Object> parameterValues ) {
    final String[] names = parameterValues.keySet().toArray( new String[parameterValues.size()] );
    setData( names, parameterValues.values().toArray() );
  }

  public String[] getColumnNames() {
    if ( names == null ) {
      return StaticDataRow.EMPTY_NAMES;
    }
    return names.clone();
  }

  protected void setData( final String[] names, final Object[] values ) {
    if ( names == null ) {
      throw new NullPointerException();
    }
    if ( values == null ) {
      throw new NullPointerException();
    }
    if ( names.length == values.length ) {
      this.names = names.clone();
      final int length = names.length;
      final HashMap<String, Object> valueMap = new HashMap<String, Object>();
      for ( int i = 0; i < length; i++ ) {
        final String name = names[i];
        valueMap.put( name, values[i] );
      }
      this.values = Collections.unmodifiableMap( valueMap );
    } else {
      final int length = Math.min( names.length, values.length );
      this.names = new String[length];
      System.arraycopy( names, 0, this.names, 0, length );
      final HashMap<String, Object> valueMap = new HashMap<String, Object>();
      for ( int i = 0; i < length; i++ ) {
        final String name = names[i];
        valueMap.put( name, values[i] );
      }
      this.values = Collections.unmodifiableMap( valueMap );

    }
  }

  protected void updateData( final Object[] values ) {
    if ( values.length != this.values.size() ) {
      throw new IllegalArgumentException( "You should preserve the number of columns." );
    }

    final HashMap<String, Object> valueMap = new HashMap<String, Object>();
    final int length = Math.min( names.length, values.length );
    for ( int i = 0; i < length; i++ ) {
      final String name = names[i];
      valueMap.put( name, values[i] );
    }
    this.values = Collections.unmodifiableMap( valueMap );
  }

  /**
   * Returns the value of the function, expression or column using its specific name. The given name is translated into
   * a valid column number and the the column is queried. For functions and expressions, the <code>getValue()</code>
   * method is called and for columns from the tablemodel the tablemodel method <code>getValueAt(row, column)</code>
   * gets called.
   *
   * @param col
   *          the item index.
   * @return the value.
   * @throws IllegalStateException
   *           if the datarow detected a deadlock.
   */
  public Object get( final String col ) {
    return values.get( col );
  }

  public boolean isChanged( final String name ) {
    return false;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof StaticDataRow ) ) {
      return false;
    }

    final StaticDataRow that = (StaticDataRow) o;

    if ( !Arrays.equals( names, that.names ) ) {
      return false;
    }
    if ( !equalsMap( that.values ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = hashCodeMap();
    for ( int i = 0; i < names.length; i++ ) {
      final String name = names[i];
      if ( name != null ) {
        result = 31 * result + name.hashCode();
      } else {
        result = 31 * result;
      }
    }
    return result;
  }

  private boolean equalsMap( final Map otherValues ) {
    if ( otherValues.size() != values.size() ) {
      return false;
    }
    for ( int i = 0; i < names.length; i++ ) {
      final String key = names[i];
      final Object value = values.get( key );
      final Object otherValue = otherValues.get( key );

      if ( value == null && otherValue == null ) {
        continue;
      }

      if ( value instanceof Object[] && otherValue instanceof Object[] ) {
        if ( ObjectUtilities.equalArray( (Object[]) value, (Object[]) otherValue ) == false ) {
          return false;
        }
      } else if ( ObjectUtilities.equal( value, otherValue ) == false ) {
        return false;
      }
    }
    return true;
  }

  private int hashCodeMap() {
    int hashCode = values.size();

    for ( int i = 0; i < names.length; i++ ) {
      final String key = names[i];
      final Object value = values.get( key );

      hashCode = 31 * hashCode + ( key != null ? key.hashCode() : 0 );
      if ( value == null ) {
        hashCode = 31 * hashCode;
      } else if ( value instanceof Object[] ) {
        hashCode = 31 * hashCode + ObjectUtilities.hashCode( (Object[]) value );
      } else {
        hashCode = 31 * hashCode + value.hashCode();
      }
    }
    return hashCode;
  }
}
