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

package org.pentaho.reporting.libraries.base.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * A fast linked-hashmap that avoids any unneccessay work. It is slightly slower than an ordinary hashmap but faster
 * than a combined hashmap and list-index that would be needed to get this functionality on JDK 1.2. The map is as fast
 * as the LinkedHashMap of JDK 1.4+.
 *
 * @author Thomas Morgner
 * @noinspection ProtectedField
 */
public class LinkedMap implements Cloneable, Serializable {
  /**
   * A cache map entry class holding both the key and value and acting as member of a linked list.
   */
  protected static final class MapEntry implements Serializable {
    /**
     * The precomputed hashkey of the key.
     */
    protected final int hashKey;
    /**
     * The key object, which is never null and which never changes.
     */
    protected final Object key;
    /**
     * The current value object (can be null).
     */
    protected Object value;
    /**
     * The link to the previous entry in the list.
     */
    protected MapEntry previous;
    /**
     * The link to the next entry in the list.
     */
    protected MapEntry next;
    /**
     * The link to the next entry in the bucket that has the same hashkey.
     */
    protected MapEntry collisionNext;

    /**
     * Creates a new map-entry for the given key and value.
     *
     * @param key     the key, never null.
     * @param hashKey the precomputed hashkey for the key.
     * @param value   the value, never null.
     */
    protected MapEntry( final Object key, final int hashKey, final Object value ) {
      if ( key == null ) {
        throw new NullPointerException();
      }
      this.key = key;
      this.hashKey = hashKey;
      this.value = value;
    }

    /**
     * Returns the previous entry in the list or null if this is the first entry.
     *
     * @return the previous entry.
     */
    public MapEntry getPrevious() {
      return previous;
    }

    /**
     * Redefines the previous entry in the list or null if this is the first entry.
     *
     * @param previous the previous entry.
     */
    public void setPrevious( final MapEntry previous ) {
      this.previous = previous;
    }

    /**
     * Returns the next entry in the list or null if this is the last entry.
     *
     * @return the next entry.
     */
    public MapEntry getNext() {
      return next;
    }

    /**
     * Redefines the next entry in the list or null if this is the last entry.
     *
     * @param next the next entry.
     */
    public void setNext( final MapEntry next ) {
      this.next = next;
    }

    /**
     * Returns the current value.
     *
     * @return the value, never null.
     */
    public Object getValue() {
      return value;
    }

    /**
     * Redefines the current value.
     *
     * @param value the value, never null.
     */
    public void setValue( final Object value ) {
      this.value = value;
    }

    /**
     * Returns the next map-entry in the bucket. If more than one object maps into the same hash-bucket, this map stores
     * the entries as linked list.
     *
     * @return the next entry.
     */
    public MapEntry getCollisionNext() {
      return collisionNext;
    }

    /**
     * Defines the next map-entry in the bucket. If more than one object maps into the same hash-bucket, this map stores
     * the entries as linked list.
     *
     * @param collisionNext the next entry.
     */
    public void setCollisionNext( final MapEntry collisionNext ) {
      if ( collisionNext == this ) {
        throw new IllegalStateException();
      }
      this.collisionNext = collisionNext;
    }
  }

  private static final int MAXIMUM_CAPACITY = 1 << 30;
  private static final Object NULL_MARKER = new Object();
  private int size;
  private int mask;
  private float loadFactor;
  private int capacity;
  private MapEntry[] backend;
  private MapEntry firstEntry;
  private MapEntry lastEntry;

  /**
   * Default constructor. Creates a map for 16 entries with a default load-factor of 0.75.
   */
  public LinkedMap() {
    this( 16, 0.75f );
  }

  /**
   * Creates a new map with the given initial number of buckets and the given loadfactor. A load factor greater 1 will
   * always cause hash-collisions, while lower loadfactors reduce the likelyhood of collisions.
   *
   * @param initialCapacity the initial capacity.
   * @param loadFactor      the load factor of the bucket-array.
   */
  public LinkedMap( int initialCapacity, final float loadFactor ) {
    if ( initialCapacity > MAXIMUM_CAPACITY ) {
      initialCapacity = MAXIMUM_CAPACITY;
    }
    if ( loadFactor <= 0 || Float.isNaN( loadFactor ) ) {
      throw new IllegalArgumentException( "Illegal load factor: " + loadFactor );
    }

    int capacity = 1;
    int mask = 0;
    while ( capacity < initialCapacity ) {
      mask = ( mask << 1 ) | 1;
      capacity <<= 1;
    }
    this.mask = mask;
    this.loadFactor = loadFactor;
    this.backend = new MapEntry[ capacity ];
    this.capacity = (int) Math.ceil( capacity * loadFactor );
  }

  /**
   * A helper to ensure that null-keys are maped into a special marker object.
   *
   * @param o the potential key.
   * @return the null-marker.
   */
  private static Object ensureKey( final Object o ) {
    if ( o == null ) {
      return NULL_MARKER;
    }
    return o;
  }

  /**
   * Ensures that the hashcode produced by the key is sane. This does some bit-juggeling to avoid incorrect hashkey
   * implementations.
   *
   * @param h the original hashcode.
   * @return the cleaned hashcode.
   */
  private static int cleanHash( int h ) {
    h ^= ( h >>> 20 ) ^ ( h >>> 12 );
    return h ^ ( h >>> 7 ) ^ ( h >>> 4 );
  }

  /**
   * Ensures that the map contains enough space to store the next entry.
   */
  private void ensureSize() {
    final MapEntry[] backend = this.backend;
    if ( size <= ( capacity ) ) {
      return;
    }

    // expand ..
    final MapEntry[] newBackend = new MapEntry[ ( backend.length << 1 ) ];
    final int newMask = ( mask << 1 ) | 1;
    transferEntry( newBackend, newMask );

    this.mask = newMask;
    this.backend = newBackend;
    this.capacity = (int) Math.ceil( loadFactor * backend.length );
  }

  private void transferEntry( final MapEntry[] newBackend, final int newMask ) {
    for ( int i = 0; i < this.backend.length; i++ ) {
      MapEntry entry = this.backend[ i ];
      while ( entry != null ) {
        final MapEntry next = entry.collisionNext;
        final int insertIndex = entry.hashKey & newMask;
        entry.setCollisionNext( newBackend[ insertIndex ] );
        newBackend[ insertIndex ] = entry;
        entry = next;
      }
    }

    for ( int i = 0; i < newBackend.length; i++ ) {
      MapEntry mapEntry = newBackend[ i ];
      while ( mapEntry != null ) {
        final int insertIndex = mapEntry.hashKey & newMask;
        if ( i != insertIndex ) {
          throw new IllegalStateException();
        }
        mapEntry = mapEntry.collisionNext;
      }

    }
  }

  /**
   * Returns the number of entries in the map.
   *
   * @return the number of entries.
   */
  public int size() {
    return size;
  }

  /**
   * Stores the given value in the map using the provided key. Both key and value can be null.
   *
   * @param key   the key.
   * @param value the value to be stored under the key.
   * @return the previous value stored under this key or null of the entry is new.
   */
  public Object put( final Object key, final Object value ) {
    final Object realKey = ensureKey( key );
    final int hashKey = cleanHash( realKey.hashCode() );

    ensureSize();

    final int index = hashKey & mask;
    final MapEntry existingEntry = backend[ index ];
    if ( existingEntry == null ) {
      final MapEntry entry = new MapEntry( realKey, hashKey, value );
      addNewRecord( index, entry );
      return null;
    }


    MapEntry colEntry = existingEntry;
    while ( true ) {
      // The root entry exists and matches the current key.
      if ( colEntry.hashKey == hashKey &&
        colEntry.key.equals( realKey ) ) {
        // that means, we just have to update the value inside and move the entry to the last position
        // in the list (to make it look like a remove/add operation.
        return updateRecord( value, colEntry );
      }

      if ( colEntry.collisionNext == null ) {
        // create a new entry in the backend-array ...
        final MapEntry entry = new MapEntry( realKey, hashKey, value );
        addCollisionRecord( index, entry );
        return null;
      }

      colEntry = colEntry.collisionNext;
    }
  }

  /**
   * Updates an existing record and reinserts the record at the end of the linked list.
   *
   * @param value    the new value value.
   * @param colEntry the entry record that should be updated.
   * @return the old value in the entry or null, if there is no old entry.
   */
  private Object updateRecord( final Object value, final MapEntry colEntry ) {
    final Object oldValue = colEntry.value;
    // replace the value ..
    colEntry.value = ( value );
    // and reconnect the entry at the end of the queue ..
    final MapEntry firstEntry = this.firstEntry;
    final MapEntry lastEntry = this.lastEntry;
    if ( lastEntry == colEntry ) {
      // also covers the case where last = first = col
      return oldValue;
    }
    if ( firstEntry == colEntry ) {
      this.firstEntry = colEntry.next;
      if ( this.firstEntry != null ) {
        this.firstEntry.previous = null;
      }
      colEntry.previous = ( lastEntry );
      colEntry.next = ( null );
      lastEntry.next = ( colEntry );
      this.lastEntry = colEntry;

      asserta();
      return oldValue;
    }

    final MapEntry prevEntry = colEntry.previous;
    final MapEntry nextEntry = colEntry.next;
    prevEntry.next = ( nextEntry );
    nextEntry.previous = ( prevEntry );
    colEntry.previous = ( lastEntry );
    colEntry.next = ( null );
    lastEntry.next = ( colEntry );
    this.lastEntry = colEntry;

    asserta();
    return oldValue;
  }

  /**
   * Adds a new map-entry to an already filled bucket.
   *
   * @param index where to add the new record in the map.
   * @param entry the new entry to be added.
   */
  private void addCollisionRecord( final int index, final MapEntry entry ) {
    entry.setCollisionNext( backend[ index ] );
    backend[ index ] = entry;

    final MapEntry lastEntry = this.lastEntry;
    entry.previous = ( lastEntry );
    entry.next = ( null );
    lastEntry.next = ( entry );
    this.lastEntry = entry;
    size += 1;

    asserta();
  }

  /**
   * Adds a completely new record to an previously empty bucket.
   *
   * @param index the index of the bucket to be updated.
   * @param entry the new map-entry.
   */
  private void addNewRecord( final int index, final MapEntry entry ) {
    // thats easy ...
    final MapEntry lastEntry = this.lastEntry;
    if ( lastEntry == null ) {
      firstEntry = entry;
    } else {
      entry.previous = ( lastEntry );
      lastEntry.next = ( entry );
    }
    this.lastEntry = entry;
    backend[ index ] = entry;
    size += 1;

    asserta();
  }

  /**
   * Retrieves the object stored under the given key from the map.
   *
   * @param key the key for which a value should be located.
   * @return the value or null, if the map did not contain a value for the key.
   */
  public Object get( final Object key ) {
    final Object realKey = ensureKey( key );
    final int hashKey = cleanHash( realKey.hashCode() );

    final int index = hashKey & mask;
    final MapEntry existingEntry = backend[ index ];
    if ( existingEntry == null ) {
      return null;
    }

    MapEntry colEntry = existingEntry;
    while ( colEntry != null ) {
      // The root entry exists and matches the current key.
      if ( colEntry.hashKey == hashKey && colEntry.key.equals( realKey ) ) {
        return colEntry.value;
      }
      colEntry = colEntry.collisionNext;
    }
    return null;
  }

  /**
   * Removes the object stored under the given key from the map.
   *
   * @param key the key for which a value should be located.
   * @return the value or null, if the map did not contain a value for the key.
   */
  public Object remove( final Object key ) {
    final Object realKey = ensureKey( key );
    final int hashKey = cleanHash( realKey.hashCode() );

    final int index = hashKey & mask;
    final MapEntry existingEntry = backend[ index ];
    if ( existingEntry == null ) {
      return null;
    }

    MapEntry prevEntry = null;
    MapEntry colEntry = existingEntry;
    while ( colEntry != null ) {
      // The root entry exists and matches the current key.
      if ( colEntry.hashKey == hashKey && colEntry.key.equals( realKey ) ) {
        final Object value = colEntry.value;
        if ( prevEntry == null ) {
          // this is a root level entry ..
          backend[ index ] = colEntry.collisionNext;
        } else {
          prevEntry.setCollisionNext( colEntry.collisionNext );
        }

        // now check the first and last entry ...
        if ( firstEntry == lastEntry ) {
          // there is ony one entry.
          firstEntry = null;
          lastEntry = null;
          size -= 1;

          asserta();
          return value;
        }

        if ( firstEntry == colEntry ) {
          final MapEntry nextfirstEntry = colEntry.next;
          if ( nextfirstEntry != null ) {
            nextfirstEntry.previous = ( null );
            colEntry.next = null;
          }
          firstEntry = nextfirstEntry;
        } else if ( lastEntry == colEntry ) {
          final MapEntry nextLastEntry = colEntry.previous;
          if ( nextLastEntry != null ) {
            nextLastEntry.next = ( null );
            colEntry.previous = null;
          }
          lastEntry = nextLastEntry;
        }

        if ( colEntry.previous != null ) {
          colEntry.previous.next = colEntry.next;
        }
        if ( colEntry.next != null ) {
          colEntry.next.previous = colEntry.previous;
        }
        size -= 1;

        asserta();
        return value;
      }

      prevEntry = colEntry;
      colEntry = colEntry.collisionNext;
    }

    asserta();
    return null;
  }

  private void asserta() {
    if ( firstEntry == null ) {
      return;
    }
    if ( firstEntry.previous != null ) {
      throw new NullPointerException();
    }
  }

  /**
   * Checks, whether the map contains an entry for the key.
   *
   * @param key the key for which a value should be located.
   * @return true if the map contains a value for the key, false otherwise.
   */
  public boolean containsKey( final Object key ) {
    final Object realKey = ensureKey( key );
    final int hashKey = cleanHash( realKey.hashCode() );
    final int index = hashKey & mask;
    final MapEntry existingEntry = backend[ index ];
    if ( existingEntry == null ) {
      return false;
    }

    MapEntry colEntry = existingEntry;
    while ( colEntry != null ) {
      // The root entry exists and matches the current key.
      if ( colEntry.hashKey == hashKey && colEntry.key.equals( realKey ) ) {
        return true;
      }
      colEntry = colEntry.collisionNext;
    }
    return false;
  }

  /**
   * Returns the keys used in this map as array. The keys are returned in the insertation order.
   *
   * @param data the object array that should receive the keys.
   * @return the array filled with the keys.
   */
  public Object[] keys( final Object[] data ) {
    final Object[] list;
    if ( data.length < size ) {
      list = (Object[]) Array.newInstance( data.getClass().getComponentType(), size );
    } else {
      list = data;
    }

    int index = 0;
    MapEntry entry = firstEntry;
    while ( entry != null ) {
      final Object o = entry.key;
      if ( o == NULL_MARKER ) {
        list[ index ] = ( null );
      } else {
        list[ index ] = ( o );
      }
      entry = entry.getNext();
      index += 1;
    }
    return list;
  }

  /**
   * Returns the keys used in this map as array. The keys are returned in the insertation order.
   *
   * @return the array filled with the keys.
   */
  public Object[] keys() {
    return keys( new Object[ size ] );
  }

  /**
   * Returns the values used in this map as array. The values are returned in the insertation order.
   *
   * @return the array filled with the values.
   */
  public Object[] values() {
    return values( new Object[ size ] );
  }

  /**
   * Returns the values used in this map as array. The values are returned in the insertation order.
   *
   * @param data the object array that should receive the values.
   * @return the array filled with the values.
   */
  public Object[] values( final Object[] data ) {
    final Object[] list;
    if ( data.length < size ) {
      list = (Object[]) Array.newInstance( data.getClass().getComponentType(), size );
    } else {
      list = data;
    }
    int index = 0;
    MapEntry entry = firstEntry;
    while ( entry != null ) {
      final Object o = entry.value;
      list[ index ] = ( o );
      entry = entry.getNext();
      index += 1;
    }
    return list;
  }

  /**
   * Clears the map and removes all map records.
   */
  public void clear() {
    if ( firstEntry == null ) {
      return;
    }
    firstEntry = null;
    lastEntry = null;
    Arrays.fill( backend, null );
    size = 0;
  }

  /**
   * Clones this map.
   *
   * @return the cloned map.
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException {
    final LinkedMap map = (LinkedMap) super.clone();
    map.backend = backend.clone();
    Arrays.fill( map.backend, null );
    map.firstEntry = null;
    map.lastEntry = null;
    map.size = 0;
    MapEntry entry = firstEntry;
    while ( entry != null ) {
      map.put( entry.key, entry.value );
      entry = entry.getNext();
    }
    return map;
  }

  /**
   * Checks whether this collection is empty.
   *
   * @return true, if the collection is empty, false otherwise.
   */
  public boolean isEmpty() {
    return size == 0;
  }
}
