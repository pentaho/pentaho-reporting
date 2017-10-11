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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import java.util.Arrays;

public class TableCutList implements Cloneable {
  private static final Boolean[] EMPTY_ENTRIES = new Boolean[0];
  private static final long[] EMPTY_KEYS = new long[0];
  private static final int LIN_VS_BIN = 16;

  private Boolean[] entries;
  private long[] keys;
  private int size;
  private int increment;
  private boolean enableQuickLookup;
  private long scaleFactor;

  public TableCutList( final int increment, final boolean enableQuickLookup ) {
    if ( increment < 1 ) {
      throw new IllegalArgumentException();
    }
    this.increment = increment;
    entries = TableCutList.EMPTY_ENTRIES;
    keys = TableCutList.EMPTY_KEYS;
    this.enableQuickLookup = enableQuickLookup;
  }

  public TableCutList clone() {
    try {
      TableCutList clone = (TableCutList) super.clone();
      clone.entries = clone.entries.clone();
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public void clear() {
    this.size = 0;
    this.scaleFactor = 0;
  }

  public boolean isEnableQuickLookup() {
    return enableQuickLookup;
  }

  public void setEnableQuickLookup( final boolean enableQuickLookup ) {
    this.enableQuickLookup = enableQuickLookup;
  }

  public int size() {
    return size;
  }

  /**
   * Ensures, that the list backend can store at least <code>c</code> elements. This method does nothing, if the new
   * capacity is less than the current capacity.
   *
   * @param c
   *          the new capacity of the list.
   */
  private void ensureCapacity( final int c ) {
    if ( keys.length <= c ) {
      final int newIncrement = Math.min( 25000, Math.max( keys.length / 2, increment ) );
      final long[] newKeys = new long[Math.max( keys.length + newIncrement, c + 1 )];
      System.arraycopy( keys, 0, newKeys, 0, size );
      keys = newKeys;

      final Boolean[] newCuts = new Boolean[Math.max( entries.length + newIncrement, c + 1 )];
      System.arraycopy( entries, 0, newCuts, 0, size );
      entries = newCuts;
    }
  }

  public static long bin;
  public static long lin;

  public boolean put( final long key, final Boolean entry ) {
    if ( entry == null ) {
      throw new NullPointerException();
    }

    // accessLogWriter.println("put - " + System.currentTimeMillis() + " - " + key);
    if ( size > 0 ) {
      // try a short-cut, which is usefull for y-coordinates (which are almost always sorted).
      if ( key > keys[size - 1] ) {
        ensureCapacity( size + 1 );
        keys[size] = key;
        entries[size] = entry;
        size += 1;
        scaleFactor = ( key - keys[0] ) / size;
        return true;
      }
    }

    int start = 0;
    int end = size;
    if ( enableQuickLookup && size > 0 && scaleFactor != 0 ) {
      if ( key < keys[0] ) {
        end = 1;
      } else {
        // assume a relatively uniform layout, all rows have roughly the same size ..
        // this means, we can guess-jump close to the target-position ..
        final int maxIdx = size - 1;
        final long lastVal = keys[maxIdx];
        if ( lastVal > 0 ) {
          final int targetIdx = (int) ( ( key - keys[0] ) / scaleFactor );
          final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
          final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

          final long minKey = keys[minTgtIdx];
          final long maxKey = keys[maxTgtIdx];
          final boolean minLessPos = key >= minKey;
          final boolean maxMorePos = key <= maxKey;
          if ( minLessPos ) {
            start = minTgtIdx;
          }
          if ( maxMorePos ) {
            end = maxTgtIdx + 1;
          }
        }
      }
    }

    // ok, check, whether this is a new key ..
    final int position;
    if ( ( end - start ) < LIN_VS_BIN ) {
      lin += 1;
      position = linearSearch( keys, key, start, end );
    } else {
      bin += 1;
      position = binarySearch( keys, key, start, end );
    }
    if ( position >= 0 ) {
      final Boolean entryFromList = entries[position];
      if ( entryFromList == null ) {
        throw new IllegalStateException( "Must not happen" );
      }
      if ( Boolean.TRUE.equals( entryFromList ) ) {
        entries[position] = entry;
      }
      return false;
    }

    ensureCapacity( size + 1 );

    final int insertPoint = -( position + 1 );
    if ( insertPoint < size ) {
      // shift the contents ..
      System.arraycopy( keys, insertPoint, keys, insertPoint + 1, size - insertPoint );
      System.arraycopy( entries, insertPoint, entries, insertPoint + 1, size - insertPoint );
    }
    keys[insertPoint] = key;
    entries[insertPoint] = entry;
    size += 1;
    if ( insertPoint == ( size - 1 ) ) {
      // we modified the last entry, so update the lookup-scale-factor ..
      scaleFactor = ( key - keys[0] ) / size;
    } else {
      // we modified a inner entry, so update the lookup-scale-factor ..
      scaleFactor = ( keys[size - 1] - keys[0] ) / size;
    }
    return true;
  }

  /**
   * Performs a binary-search, but includes some optimizations in case we search for the same key all the time.
   *
   * @param pos
   *          the starting position of the box.
   * @return the position as positive integer or a negative integer indicating the insert-point.
   */
  private int findKeyInternal( final long pos ) {
    return findKeyInternal( pos, -1 );
  }

  private int findKeyInternal( final long pos, final int lastFoundPos ) {
    int start = 0;
    int end = size;

    if ( lastFoundPos != -1 && lastFoundPos < size ) {
      if ( keys[lastFoundPos] == pos ) {
        return lastFoundPos;
      }
    }

    if ( enableQuickLookup && size > 0 && scaleFactor != 0 ) {
      if ( pos < keys[0] ) {
        return -1;
      }

      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = size - 1;
      final long lastVal = keys[maxIdx];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( ( pos - keys[0] ) / scaleFactor );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = keys[minTgtIdx];
        final long maxKey = keys[maxTgtIdx];
        final boolean minLessPos = pos >= minKey;
        final boolean maxMorePos = pos <= maxKey;
        if ( minLessPos ) {
          start = minTgtIdx;
        }
        if ( maxMorePos ) {
          end = maxTgtIdx + 1;
        }

      }
    }

    final int i;
    if ( end - start < LIN_VS_BIN ) {
      lin += 1;
      i = linearSearch( keys, pos, start, end );
    } else {
      bin += 1;
      i = binarySearch( keys, pos, start, end );
    }
    if ( i > -1 ) {
      return i;
    }
    if ( i == -1 ) {
      return -1;
    }

    return i;

  }

  public boolean remove( final long key ) {
    final int position = findKeyInternal( key );
    // binarySearch(keys, key, 0, size);
    if ( position < 0 ) {
      return false;
    }

    final int shiftElements = size - position - 1;
    if ( shiftElements == 0 ) {
      keys[position] = 0;
      entries[position] = null;
      size -= 1;
      if ( size == 0 ) {
        scaleFactor = 0;
      } else {
        scaleFactor = ( keys[size - 1] - keys[0] ) / size;
      }
      return true;
    }

    size -= 1;
    System.arraycopy( keys, position + 1, keys, position, shiftElements );
    System.arraycopy( entries, position + 1, entries, position, shiftElements );

    keys[size] = 0;
    entries[size] = null;
    if ( size == 0 ) {
      scaleFactor = 0;
    } else {
      scaleFactor = ( keys[size - 1] - keys[0] ) / size;
    }
    return true;
  }

  public Boolean get( final long key ) {
    if ( size == 0 ) {
      return null;
    }

    if ( key > keys[size - 1] ) {
      return null;
    }

    final int position = findKeyInternal( key );
    // binarySearch(keys, key, 0, size);
    if ( position < 0 ) {
      return null;
    }
    return entries[position];
  }

  public Boolean getPrevious( final long key ) {
    if ( size == 0 ) {
      return null;
    }
    if ( key > keys[size - 1] ) {
      return entries[size - 1];
    }

    final int position = findKeyInternal( key );
    // binarySearch(keys, key, 0, size);
    if ( position == 0 ) {
      return null;
    }
    if ( position > 0 ) {
      return entries[position - 1];
    }

    final int insertPoint = -( position + 2 );
    return entries[insertPoint];
  }

  public boolean containsKey( final long key ) {
    if ( size > 0 ) {
      // try a short-cut, which is usefull for y-coordinates (which are almost always sorted).
      if ( key > keys[size - 1] ) {
        return false;
      }
    }
    return findKeyInternal( key ) >= 0;
  }

  private static int linearSearch( final long[] array, final long key, final int start, final int end ) {
    for ( int i = start; i < end; i++ ) {
      final long value = array[i];
      if ( value == key ) {
        return i;
      }
      if ( key < value ) {
        return -( i + 1 );
      }
    }
    return -( end + 1 );
  }

  private static int binarySearch( final long[] array, final long key, final int start, final int end ) {
    // int itCount = 0;
    int low = start;
    int high = end - 1;
    while ( low <= high ) {
      // itCount += 1;
      final int mid = ( low + high ) >>> 1;
      final long midVal = array[mid];

      if ( midVal < key ) {
        low = mid + 1;
      } else if ( midVal > key ) {
        high = mid - 1;
      } else {
        return mid; // key found
      }
    }
    return -( low + 1 ); // key not found.
  }

  public Boolean[] getRawEntries() {
    return entries;
  }

  /**
   * Copys the list contents into a new array.
   *
   * @return the list contents as array.
   * @deprecated Always provide a buffer for performance reasons.
   */
  public long[] getKeys() {
    if ( size == 0 ) {
      return TableCutList.EMPTY_KEYS;
    }

    if ( size == keys.length ) {
      return (long[]) keys.clone();
    }

    final long[] retval = new long[size];
    System.arraycopy( keys, 0, retval, 0, size );
    return retval;
  }

  public long[] getKeys( long[] retval ) {
    if ( retval == null || retval.length < size ) {
      retval = new long[size];
    }
    System.arraycopy( keys, 0, retval, 0, size );
    return retval;
  }

  /**
   * Tries to locate the key that matches the given key-parameter as closely as possible. If greater is set to true,
   * then - if the coordinate is not contained in the list - the next coordinate is given, else the previous one is
   * returned.
   *
   * @param coordinate
   * @param greater
   * @return
   */
  public int findKeyPosition( final long coordinate, final boolean greater ) {
    return findKeyPosition( coordinate, greater, -1 );
  }

  public int findKeyPosition( final long coordinate, final boolean greater, final int lastFoundPos ) {
    final int pos = findKeyInternal( coordinate, lastFoundPos );
    if ( pos == size ) {
      // warning: This might be stupid
      return size - 1;
    }
    if ( pos >= 0 ) {
      return pos;
    }

    // the coordinate is greater than the largest key in this list ..
    if ( pos == -( size + 1 ) ) {
      return size - 1;
    }

    // the coordinate is not a key, but smaller than the largest key in this list..
    if ( greater ) {
      return ( -pos - 1 );
    } else {
      return ( -pos - 2 );
    }
  }

  public long getKeyAt( final int indexPosition ) {
    if ( indexPosition >= size || indexPosition < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    return keys[indexPosition];
  }

  public Boolean getValueAt( final int indexPosition ) {
    if ( indexPosition >= size || indexPosition < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    return entries[indexPosition];
  }

  public long findKey( final long key, final boolean upperBounds ) {
    final int pos = findKeyPosition( key, upperBounds );
    return keys[pos];
  }

  /**
   * Expects a sorted (ascending) list of cut-entries that should be removed. You will run into troubles if the list is
   * not sorted.
   *
   * @param cutArray
   */
  public void removeAll( final long[] cutArray, final long cutSize ) {
    if ( cutSize == 0 ) {
      return;
    }

    int cutIndex = 0;
    long currentCut = cutArray[0];

    int targetPosition = 0;
    int sourcePosition = 0;
    for ( ; sourcePosition < size; sourcePosition++ ) {
      final long key = keys[sourcePosition];
      if ( key == currentCut ) {
        // do nothing ..
        cutIndex += 1;
        if ( cutIndex == cutSize ) {
          System.arraycopy( keys, sourcePosition + 1, keys, targetPosition, size - sourcePosition - 1 );
          System.arraycopy( entries, sourcePosition + 1, entries, targetPosition, size - sourcePosition - 1 );
          targetPosition = size - cutIndex;
          break;
        }
        currentCut = cutArray[cutIndex];
      } else {
        keys[targetPosition] = key;
        entries[targetPosition] = entries[sourcePosition];
        targetPosition += 1;
      }
    }

    Arrays.fill( keys, targetPosition, size, 0 );
    Arrays.fill( entries, targetPosition, size, null );
    size = targetPosition;
    if ( size != 0 ) {
      scaleFactor = ( keys[size - 1] - keys[0] ) / size;
    } else {
      scaleFactor = 0;
    }
  }
}
