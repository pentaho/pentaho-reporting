/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.model;

import java.util.Arrays;

/**
 * Creation-Date: 26.08.2007, 15:06:45
 *
 * @author Thomas Morgner
 */
public final class HorizontalPositionsList {
  private static final long[] EMPTY_KEYS = new long[ 0 ];
  private static final long[] EMPTY_AGES = new long[ 0 ];

  private long[] keys;
  private long[] age;
  private int size;
  private int increment;
  private boolean enableQuickLookup;
  private long scaleFactor;

  public HorizontalPositionsList() {
    this( 25, true );
  }

  public HorizontalPositionsList( final int increment,
                                  final boolean enableQuickLookup ) {
    if ( increment < 1 ) {
      throw new IllegalArgumentException();
    }
    this.increment = increment;
    this.keys = HorizontalPositionsList.EMPTY_KEYS;
    this.age = EMPTY_AGES;
    this.enableQuickLookup = enableQuickLookup;
  }

  public void clear() {
    Arrays.fill( age, -1 );
    Arrays.fill( keys, 0 );
    scaleFactor = 0;
    size = 0;
  }

  //
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
   * @param c the new capacity of the list.
   */
  public void ensureCapacity( final int c ) {
    if ( keys.length <= c ) {
      final long[] newKeys = new long[ Math.max( keys.length + increment, c + 1 ) ];
      System.arraycopy( keys, 0, newKeys, 0, size );
      keys = newKeys;
      final long[] newAges = new long[ Math.max( age.length + increment, c + 1 ) ];
      System.arraycopy( age, 0, newAges, 0, size );
      age = newAges;
    }
  }

  public long getAge( final long key ) {
    final int pos = findKeyInternal( key );
    if ( pos >= 0 ) {
      return age[ pos ];
    }
    return -1;
  }

  public boolean add( final long key, final long age ) {

    //    accessLogWriter.println("put - " + System.currentTimeMillis() + " - " + key);
    if ( size > 0 ) {
      // try a short-cut, which is usefull for y-coordinates (which are almost always sorted).
      if ( key > keys[ size - 1 ] ) {
        ensureCapacity( size + 1 );
        keys[ size ] = key;
        this.age[ size ] = age;
        size += 1;
        scaleFactor = key / size;
        return true;
      }
    }

    int start = 0;
    int end = size;
    if ( enableQuickLookup && size > 0 ) {
      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = size - 1;
      final long lastVal = keys[ maxIdx ];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( key / scaleFactor );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = keys[ minTgtIdx ];
        final long maxKey = keys[ maxTgtIdx ];
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

    // ok, check, whether this is a new key ..
    final int position = binarySearch( keys, key, start, end );
    if ( position >= 0 ) {
      // not a new key, but a multi-age thing
      this.age[ position ] = age;
      return false;
    }

    ensureCapacity( size + 1 );

    final int insertPoint = -( position + 1 );
    if ( insertPoint < size ) {
      // shift the contents ..
      System.arraycopy( keys, insertPoint, keys, insertPoint + 1, size - insertPoint );
      System.arraycopy( this.age, insertPoint, this.age, insertPoint + 1, size - insertPoint );
    }
    keys[ insertPoint ] = key;
    this.age[ insertPoint ] = age;
    size += 1;
    if ( insertPoint == ( size - 1 ) ) {
      // we modified the last entry, so update the lookup-scale-factor ..
      scaleFactor = key / size;
    } else {
      // we modified a inner entry, so update the lookup-scale-factor ..
      scaleFactor = keys[ size - 1 ] / size;
    }
    return true;
  }

  //private int foundDirect;

  /**
   * Performs a binary-search, but includes some optimizations in case we search for the same key all the time.
   *
   * @param pos the starting position of the box.
   * @return the position as positive integer or a negative integer indicating the insert-point.
   */
  private int findKeyInternal( final long pos ) {
    int start = 0;
    int end = size;

    if ( enableQuickLookup && size > 0 ) {

      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = size - 1;
      final long lastVal = keys[ maxIdx ];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( pos / scaleFactor );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = keys[ minTgtIdx ];
        final long maxKey = keys[ maxTgtIdx ];
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

    final int i = binarySearch( keys, pos, start, end );
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
    //binarySearch(keys, key, 0, size);
    if ( position < 0 ) {
      return false;
    }

    final int shiftElements = size - position - 1;
    if ( shiftElements == 0 ) {
      keys[ position ] = 0;
      size -= 1;
      scaleFactor = keys[ size - 1 ] / size;
      return true;
    }

    size -= 1;
    System.arraycopy( keys, position + 1, keys, position, shiftElements );

    keys[ size ] = 0;
    scaleFactor = keys[ size - 1 ] / size;
    return true;
  }

  public long getNext( final long key ) {
    if ( size == 0 ) {
      return key;
    }

    if ( key > keys[ size - 1 ] ) {
      // param greater than the greatest value in list
      return keys[ size - 1 ];
    }

    final int position = findKeyInternal( key );
    if ( position == -1 ) {
      // param smaller than the smallest key in list
      return keys[ 0 ];
    } else if ( position < 0 ) {
      final int insertPoint = -( position + 1 );
      return keys[ insertPoint ];
    }
    return keys[ position ];
  }

  /**
   * Returns the entry that is either equal or less than this key.
   *
   * @param key
   * @return
   */
  public long getPrevious( final long key ) {
    if ( size == 0 ) {
      return key;
    }

    if ( key > keys[ size - 1 ] ) {
      return keys[ size - 1 ];
    }

    final int position = findKeyInternal( key );
    if ( position >= 0 ) {
      return keys[ position ];
    }
    if ( position == -1 ) {
      // param smaller than the smallest key in list
      return keys[ 0 ];
    }

    final int insertPoint = -( position + 2 );
    return keys[ insertPoint ];
  }

  public boolean containsKey( final long key ) {
    if ( size > 0 ) {
      // try a short-cut, which is usefull for y-coordinates (which are almost always sorted).
      if ( key > keys[ size - 1 ] ) {
        return false;
      }
    }
    return findKeyInternal( key ) >= 0;
  }

  private int binarySearch( final long[] array, final long key, final int start, final int end ) {
    //    int itCount = 0;
    int low = start;
    int high = end - 1;
    while ( low <= high ) {
      //      itCount += 1;
      final int mid = ( low + high ) >>> 1;
      final long midVal = array[ mid ];

      if ( midVal < key ) {
        low = mid + 1;
      } else if ( midVal > key ) {
        high = mid - 1;
      } else {
        return mid; // key found
      }
    }
    return -( low + 1 );  // key not found.
  }

  /**
   * Copys the list contents into a new array.
   *
   * @return the list contents as array.
   */
  public long[] getKeys() {
    if ( size == 0 ) {
      return HorizontalPositionsList.EMPTY_KEYS;
    }

    if ( size == keys.length ) {
      return keys.clone();
    }

    final long[] retval = new long[ size ];
    System.arraycopy( keys, 0, retval, 0, size );
    return retval;
  }

  public long[] getKeys( long[] retval ) {
    if ( retval == null || retval.length < size ) {
      retval = new long[ size ];
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
    final int pos = findKeyInternal( coordinate );
    //binarySearch(keys, coordinate, 0, size);
    if ( pos == size ) {
      //return xMaxBounds;
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
    return keys[ indexPosition ];
  }

  public long findKey( final long key, final boolean upperBounds ) {
    final int pos = findKeyPosition( key, upperBounds );
    return keys[ pos ];
  }

  /**
   * Expects a sorted (ascending) list of cut-entries that should be removed. You will run into troubles if the list is
   * not sorted.
   *
   * @param age the age. All keys with a age smaller than that will be eliminated.
   * @return true, if at least one key was removed, false otherwise.
   */
  public boolean removeAll( final long age ) {
    int targetPosition = 0;
    int sourcePosition = 0;
    for (; sourcePosition < size; sourcePosition++ ) {
      final long key = keys[ sourcePosition ];
      if ( this.age[ sourcePosition ] >= age ) {
        keys[ targetPosition ] = key;
        targetPosition += 1;
      }
    }

    if ( targetPosition == sourcePosition ) {
      return false;
    }
    Arrays.fill( keys, targetPosition, size, 0 );
    size = targetPosition;
    if ( size > 0 ) {
      scaleFactor = keys[ size - 1 ] / size;
    } else {
      scaleFactor = 0;
    }
    return true;
  }

}
