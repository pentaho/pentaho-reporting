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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The HashNMap can be used to store multiple values by a single key value. The values stored can be retrieved using a
 * direct query or by creating an enumeration over the stored elements.
 *
 * @author Thomas Morgner
 */
public class HashNMap<K, V> implements Serializable, Cloneable {

  /**
   * Serialization support.
   */
  private static final long serialVersionUID = -670924844536074826L;

  /**
   * The underlying storage.
   */
  private HashMap<K, List<V>> table;

  /**
   * An empty array.
   */
  private static final Object[] EMPTY_ARRAY = new Object[ 0 ];

  /**
   * Default constructor.
   */
  public HashNMap() {
    this.table = new HashMap<K, List<V>>();
  }

  /**
   * Returns a new empty list.
   *
   * @return A new empty list.
   */
  protected List<V> createList() {
    return new ArrayList<V>();
  }

  /**
   * Inserts a new key/value pair into the map.  If such a pair already exists, it gets replaced with the given values.
   *
   * @param key the key.
   * @param val the value.
   * @return A boolean.
   */
  public boolean put( final K key, final V val ) {
    final List<V> v = this.table.get( key );
    if ( v == null ) {
      final List<V> newList = createList();
      newList.add( val );
      this.table.put( key, newList );
      return true;
    } else {
      v.clear();
      return v.add( val );
    }
  }

  /**
   * Adds a new key/value pair into this map. If the key is not yet in the map, it gets added to the map and the call is
   * equal to put(Object,Object).
   *
   * @param key the key.
   * @param val the value.
   * @return true, if  the value has been added, false otherwise
   */
  public boolean add( final K key, final V val ) {
    final List<V> v = this.table.get( key );
    if ( v == null ) {
      put( key, val );
      return true;
    } else {
      return v.add( val );
    }
  }

  /**
   * Retrieves the first value registered for an key or null if there was no such key in the list.
   *
   * @param key the key.
   * @return the value.
   */
  public V getFirst( final K key ) {
    return get( key, 0 );
  }

  /**
   * Retrieves the last value registered for an key or null if there was no such key in the list.
   *
   * @param key the key.
   * @return the value.
   */
  public V getLast( final K key ) {
    final List<V> v = this.table.get( key );
    if ( v == null ) {
      return null;
    }
    if ( v.size() == 0 ) {
      return null;
    }
    return v.get( v.size() - 1 );
  }

  /**
   * Retrieves the n-th value registered for an key or null if there was no such key in the list. An index out of bounds
   * exception is thrown if there are less than n elements registered to this key.
   *
   * @param key the key.
   * @param n   the index.
   * @return the object.
   */
  public V get( final K key, final int n ) {
    final List<V> v = this.table.get( key );
    if ( v == null ) {
      return null;
    }
    return v.get( n );
  }

  /**
   * Returns an iterator over all elements registered to the given key.
   *
   * @param key the key.
   * @return an iterator.
   */
  public Iterator<V> getAll( final K key ) {
    final List<V> v = this.table.get( key );
    if ( v == null ) {
      // cast is ok, the iterator is empty anyway
      return EmptyIterator.emptyIterator();
    }
    return v.iterator();
  }

  /**
   * Returns all registered keys as an enumeration.
   *
   * @return an enumeration of the keys.
   */
  public Iterator<K> keys() {
    return this.table.keySet().iterator();
  }

  /**
   * Returns all registered keys as set.
   *
   * @return a set of keys.
   */
  public Set<K> keySet() {
    return this.table.keySet();
  }

  /**
   * Removes the key/value pair from the map. If the removed entry was the last entry for this key, the key gets also
   * removed.
   *
   * @param key   the key.
   * @param value the value.
   * @return true, if removing the element was successfull, false otherwise.
   */
  public boolean remove( final K key, final V value ) {
    final List<V> v = this.table.get( key );
    if ( v == null ) {
      return false;
    }

    if ( !v.remove( value ) ) {
      return false;
    }
    if ( v.isEmpty() ) {
      this.table.remove( key );
    }
    return true;
  }

  /**
   * Removes all elements for the given key.
   *
   * @param key the key.
   */
  public void removeAll( final K key ) {
    this.table.remove( key );
  }

  /**
   * Clears all keys and values of this map.
   */
  public void clear() {
    this.table.clear();
  }

  /**
   * Tests whether this map contains the given key.
   *
   * @param key the key.
   * @return true if the key is contained in the map
   */
  public boolean containsKey( final K key ) {
    return this.table.containsKey( key );
  }

  /**
   * Tests whether this map contains the given value.
   *
   * @param value the value.
   * @return true if the value is registered in the map for an key.
   */
  public boolean containsValue( final V value ) {
    final Iterator<List<V>> e = this.table.values().iterator();
    boolean found = false;
    while ( e.hasNext() && !found ) {
      final List<V> v = e.next();
      found = v.contains( value );
    }
    return found;
  }

  /**
   * Tests whether this map contains the given value.
   *
   * @param value the value.
   * @param key   the key under which to find the value
   * @return true if the value is registered in the map for an key.
   */
  public boolean containsValue( final K key, final V value ) {
    final List<V> v = this.table.get( key );
    if ( v == null ) {
      return false;
    }
    return v.contains( value );
  }

  /**
   * Creates a deep copy of this HashNMap.
   *
   * @return a clone.
   * @throws CloneNotSupportedException this should never happen.
   * @noinspection unchecked
   */
  public Object clone() throws CloneNotSupportedException {
    final HashNMap<K, V> map = (HashNMap) super.clone();
    map.table = (HashMap) table.clone();
    final Iterator iterator = map.table.entrySet().iterator();
    while ( iterator.hasNext() ) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final List list = (List) entry.getValue();
      if ( list != null ) {
        entry.setValue( ObjectUtilities.clone( list ) );
      }
    }
    return map;
  }

  /**
   * Returns the contents for the given key as object array. If there were no objects registered with that key, an empty
   * object array is returned.
   *
   * @param key  the key.
   * @param data the object array to receive the contents.
   * @return the contents.
   */
  public V[] toArray( final K key, final V[] data ) {
    if ( key == null ) {
      throw new NullPointerException( "Key must not be null." );
    }
    final List<V> list = this.table.get( key );
    if ( list != null ) {
      return list.toArray( data );
    }
    if ( data.length > 0 ) {
      data[ 0 ] = null;
    }
    return data;
  }

  /**
   * Returns the contents for the given key as object array. If there were no objects registered with that key, an empty
   * object array is returned.
   *
   * @param key the key.
   * @return the contents.
   */
  public Object[] toArray( final K key ) {
    if ( key == null ) {
      throw new NullPointerException( "Key must not be null." );
    }
    final List<V> list = this.table.get( key );
    if ( list != null ) {
      return list.toArray();
    }
    return EMPTY_ARRAY;
  }

  /**
   * Returns the number of elements registered with the given key.
   *
   * @param key the key.
   * @return the number of element for this key, or 0 if there are no elements registered.
   */
  public int getValueCount( final K key ) {
    if ( key == null ) {
      throw new NullPointerException( "Key must not be null." );
    }
    final List<V> list = this.table.get( key );
    if ( list != null ) {
      return list.size();
    }
    return 0;
  }

  /**
   * Checks, whether the map is empty.
   *
   * @return true, if the map does not contain any keys.
   */
  public boolean isEmpty() {
    return table.isEmpty();
  }
}
