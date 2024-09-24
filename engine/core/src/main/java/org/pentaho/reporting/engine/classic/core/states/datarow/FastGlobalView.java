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

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class FastGlobalView implements DataRow, MasterDataRowChangeHandler {
  private static final Log logger = LogFactory.getLog( FastGlobalView.class );

  private HashSet<String> duplicateColumns;
  private HashSet<String> invalidColumns;
  private boolean modifiableNameCache;
  private HashMap<String, Integer> nameCache;
  private String[] columnNames;
  private Boolean[] columnChanged;
  private Object[] columnValue;
  private Object[] columnOldValue;
  private int[] columnPrev;
  private int length;
  private boolean warnInvalidColumns;
  private MasterDataRowChangeEvent reusableEvent;

  public FastGlobalView( final FastGlobalView parent ) {
    if ( parent.modifiableNameCache ) {
      this.duplicateColumns = (HashSet<String>) parent.duplicateColumns.clone();
      this.nameCache = (HashMap<String, Integer>) parent.nameCache.clone();
      this.modifiableNameCache = false;
      this.columnNames = parent.columnNames.clone();
    } else {
      this.duplicateColumns = parent.duplicateColumns;
      this.nameCache = parent.nameCache;
      this.columnNames = parent.columnNames;
      this.modifiableNameCache = false;
    }

    this.reusableEvent = parent.reusableEvent;
    this.columnChanged = parent.columnChanged.clone();
    this.columnValue = parent.columnValue.clone();
    this.columnOldValue = parent.columnOldValue.clone();
    this.columnPrev = parent.columnPrev.clone();
    this.length = parent.length;
    this.warnInvalidColumns = parent.warnInvalidColumns;
    this.invalidColumns = parent.invalidColumns;
  }

  public FastGlobalView() {
    this.warnInvalidColumns =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.WarnInvalidColumns" ) );
    if ( warnInvalidColumns ) {
      this.invalidColumns = new HashSet<String>();
    }

    this.reusableEvent = new MasterDataRowChangeEvent();
    this.duplicateColumns = new HashSet<String>();
    this.nameCache = new HashMap<String, Integer>();
    this.modifiableNameCache = true;
    this.columnNames = new String[20];
    this.columnChanged = new Boolean[20];
    this.columnValue = new Object[20];
    this.columnOldValue = new Object[20];
    this.columnPrev = new int[20];
  }

  public MasterDataRowChangeEvent getReusableEvent() {
    return reusableEvent;
  }

  public void dataRowChanged( final MasterDataRowChangeEvent chEvent ) {
    // rebuild the global view and tracks changes ..
    final int type = chEvent.getType();
    if ( type == MasterDataRowChangeEvent.COLUMN_ADDED ) {
      putField( chEvent.getColumnName(), chEvent.getColumnValue(), false, false );
    } else if ( type == MasterDataRowChangeEvent.COLUMN_UPDATED ) {
      putField( chEvent.getColumnName(), chEvent.getColumnValue(), true, chEvent.isOptional() );
    } else if ( type == MasterDataRowChangeEvent.COLUMN_REMOVED ) {
      removeColumn( chEvent.getColumnName() );
    }
  }

  public String[] getColumnNames() {
    final String[] columnNames = new String[length];
    System.arraycopy( this.columnNames, 0, columnNames, 0, length );
    return columnNames;
  }

  public Object get( final String col ) throws IllegalStateException {
    final int idx = findColumn( col );
    if ( idx < 0 ) {
      if ( warnInvalidColumns ) {
        if ( invalidColumns.add( col ) ) {
          logger.warn( "Warning: Data-Set does not contain a column with name '" + col + '\'' );
        }
      }
      return null;
    }

    if ( columnChanged[idx] != null ) {
      return columnValue[idx];
    }

    final String columnName = columnNames[idx];
    if ( duplicateColumns.contains( columnName ) ) {
      for ( int i = idx - 1; i >= 0; i-- ) {
        if ( columnNames[i].equals( columnName ) && columnChanged[i] != null ) {
          return columnValue[i];
        }
      }
    }

    return columnValue[idx];
  }

  private int findColumn( final String name ) {
    final Integer o = nameCache.get( name );
    if ( o == null ) {
      return -1;
    }
    return o.intValue();
  }

  public boolean isChanged( final String name ) {
    final int idx = findColumn( name );
    if ( idx < 0 ) {
      if ( warnInvalidColumns ) {
        if ( invalidColumns.add( name ) ) {
          logger.warn( "Warning: Data-Set does not contain a column with name '" + name + '\'' );
        }
      }
      return false;
    }
    return isChanged( idx );
  }

  private boolean isChanged( final int col ) {
    if ( col < 0 || col >= length ) {
      throw new IndexOutOfBoundsException( "Column-Index " + col + " is invalid." );
    }
    final Boolean val = columnChanged[col];
    if ( val != null ) {
      return val.booleanValue();
    }

    final String columnName = columnNames[col];
    if ( duplicateColumns.contains( columnName ) ) {
      for ( int i = col - 1; i >= 0; i-- ) {
        if ( columnNames[col].equals( columnName ) && columnChanged[i] != null ) {
          return columnChanged[i].booleanValue();
        }
      }
    }

    // the 'isChanged' method may be called during the expression-evaluation before the expression that
    // is checked is actually filled in. In that case, the result would be non-deterministic.
    //
    // When called from a formula, the formula now catches the exception and returns 'error' instead.
    // When called from a expression, this has to be caught or the expression evaluates to a error state.
    throw new IllegalStateException( "Checking the 'isChanged' flag before all data for this row is known. "
        + "This is a error condition that must be checked by the caller." );
  }

  public FastGlobalView derive() {
    return new FastGlobalView( this );
  }

  public FastGlobalView advance() {
    final FastGlobalView advanced = new FastGlobalView( this );
    System.arraycopy( advanced.columnValue, 0, advanced.columnOldValue, 0, length );
    Arrays.fill( advanced.columnChanged, null );
    return advanced;
  }

  private void removeColumn( final String name ) {
    final boolean needToRebuildCache;
    int idx = -1;
    if ( duplicateColumns.contains( name ) ) {
      needToRebuildCache = true;
      // linear index search from the end ..
      for ( int i = columnNames.length - 1; i >= 0; i -= 1 ) {
        if ( ObjectUtilities.equal( name, columnNames[i] ) ) {
          idx = i;
          break;
        }
      }
      if ( idx < 0 ) {
        return;
      }
    } else {
      needToRebuildCache = false;
      final Integer o = nameCache.get( name );
      if ( o == null ) {
        return;
      }
      idx = o.intValue();
    }

    if ( logger.isTraceEnabled() ) {
      logger.trace( "Removing column " + name + " (Length: " + length + " NameCache: " + nameCache.size() + ", Idx: "
          + idx );
    }

    if ( modifiableNameCache == false ) {
      this.duplicateColumns = (HashSet<String>) duplicateColumns.clone();
      this.columnNames = columnNames.clone();
      this.nameCache = (HashMap<String, Integer>) nameCache.clone();
      this.modifiableNameCache = true;
    }

    if ( idx == ( length - 1 ) ) {
      columnChanged[idx] = null;
      columnNames[idx] = null;
      columnValue[idx] = null;
      if ( columnPrev[idx] == -1 ) {
        nameCache.remove( name );
      } else {
        nameCache.put( name, IntegerCache.getInteger( columnPrev[idx] ) );
      }
      // thats the easy case ..
      length -= 1;
      if ( needToRebuildCache ) {
        if ( columnPrev[idx] == -1 ) {
          logger.warn( "Column marked as duplicate but no duplicate index recorded: " + name );
        } else {
          if ( columnPrev[columnPrev[idx]] == -1 ) {
            duplicateColumns.remove( name );
          }
        }
      }
      return;
    }

    if ( logger.isTraceEnabled() ) {
      logger.warn( "Out of order removeal of a column: " + name );
    }

    if ( columnPrev[idx] == -1 ) {
      nameCache.remove( name );
    } else {
      nameCache.put( name, IntegerCache.getInteger( columnPrev[idx] ) );
    }

    final int moveStartIdx = idx + 1;
    final int moveLength = length - moveStartIdx;
    System.arraycopy( columnNames, moveStartIdx, columnNames, idx, moveLength );
    System.arraycopy( columnChanged, moveStartIdx, columnChanged, idx, moveLength );
    System.arraycopy( columnOldValue, moveStartIdx, columnOldValue, idx, moveLength );
    System.arraycopy( columnValue, moveStartIdx, columnValue, idx, moveLength );
    System.arraycopy( columnPrev, moveStartIdx, columnPrev, idx, moveLength );
    columnNames[length - 1] = null;
    columnChanged[length - 1] = null;
    columnOldValue[length - 1] = null;
    columnPrev[length - 1] = 0;

    // Now it gets expensive: Rebuild the namecache ..
    final int newLength = moveLength + idx;
    nameCache.clear();
    duplicateColumns.clear();
    for ( int i = 0; i < newLength; i++ ) {
      final String columnName = columnNames[i];
      final Integer oldVal = nameCache.get( columnName );
      if ( nameCache.containsKey( columnName ) ) {
        duplicateColumns.add( columnName );
      }

      nameCache.put( columnName, IntegerCache.getInteger( i ) );
      if ( oldVal != null ) {
        columnPrev[i] = oldVal.intValue();
      } else {
        columnPrev[i] = -1;
      }
    }
    length -= 1;
    if ( logger.isTraceEnabled() ) {
      logger.trace( "New Namecache: " + nameCache );
    }
  }

  private void putField( final String name, final Object value, final boolean update, final boolean optional ) {
    if ( logger.isTraceEnabled() ) {
      if ( update ) {
        logger.debug( "  +   : " + name );
      } else {
        logger.debug( "Adding: " + name );
      }

    }

    if ( update == false ) {
      addColumn( name, value );
    } else {
      // Updating an existing column ...
      updateColumn( name, value, optional );
    }
  }

  private void updateColumn( final String name, final Object value, final boolean optional ) {
    final Integer o = nameCache.get( name );
    if ( o == null ) {
      if ( optional ) {
        return;
      }
      throw new IllegalStateException( "Update to a non-existing column: " + name );
    }
    final String[] columnNames = this.columnNames;
    final Boolean[] columnChanged = this.columnChanged;
    final Object[] columnValue = this.columnValue;

    int idx = -1;
    if ( duplicateColumns.isEmpty() == false && duplicateColumns.contains( name ) ) {
      final int length = this.length;
      for ( int i = 0; i < length; i += 1 ) {
        if ( columnChanged[i] == null && ObjectUtilities.equal( name, columnNames[i] ) ) {
          idx = i;
          break;
        }
      }
      if ( idx < 0 ) {
        idx = o.intValue();
      }
    } else {
      idx = o.intValue();
    }

    columnNames[idx] = name;
    final Object oldValue = columnValue[idx];
    columnValue[idx] = value;
    if ( columnChanged[idx] == null ) {
      if ( ObjectUtilities.equal( oldValue, value ) ) {
        columnChanged[idx] = Boolean.FALSE;
      } else {
        columnChanged[idx] = Boolean.TRUE;
      }
    }
  }

  private void addColumn( final String name, final Object value ) {
    if ( modifiableNameCache == false ) {
      this.columnNames = columnNames.clone();
      this.nameCache = (HashMap<String, Integer>) nameCache.clone();
      this.modifiableNameCache = true;
    }

    // A new column ...
    ensureCapacity( length + 1 );
    columnNames[length] = name;
    columnValue[length] = value;
    final Integer o = nameCache.get( name );
    if ( o == null ) {
      columnPrev[length] = -1;
    } else {
      columnPrev[length] = o.intValue();
      duplicateColumns.add( name );
    }

    columnOldValue[length] = null;
    columnChanged[length] = Boolean.TRUE;
    nameCache.put( name, IntegerCache.getInteger( length ) );
    length += 1;
  }

  private void ensureCapacity( final int requestedSize ) {
    final int capacity = this.columnNames.length;
    if ( capacity > requestedSize ) {
      return;
    }
    final int newSize = Math.max( capacity << 1, requestedSize + 10 );

    final String[] newColumnNames = new String[newSize];
    System.arraycopy( columnNames, 0, newColumnNames, 0, length );
    this.columnNames = newColumnNames;

    final Boolean[] newColumnChanged = new Boolean[newSize];
    System.arraycopy( columnChanged, 0, newColumnChanged, 0, length );
    this.columnChanged = newColumnChanged;

    final int[] newColumnPrev = new int[newSize];
    System.arraycopy( columnPrev, 0, newColumnPrev, 0, length );
    this.columnPrev = newColumnPrev;

    final Object[] newColumnValue = new Object[newSize];
    System.arraycopy( columnValue, 0, newColumnValue, 0, length );
    this.columnValue = newColumnValue;

    final Object[] newOldColumnValue = new Object[newSize];
    System.arraycopy( columnOldValue, 0, newOldColumnValue, 0, length );
    this.columnOldValue = newOldColumnValue;
  }

  public void validateChangedFlags() {
    for ( int i = 0; i < length; i++ ) {
      final Boolean b = columnChanged[i];
      if ( b == null ) {
        throw new IllegalStateException( "Validate failed: " + columnNames[i] );
      }
    }
  }
}
