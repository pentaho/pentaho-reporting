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

package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;

/**
 * The KeyedComboBox model allows to define an internal key (the data element) for every entry in the model.
 * <p/>
 * This class is usefull in all cases, where the public text differs from the internal view on the data. A separation
 * between presentation data and processing data is a prequesite for localizing combobox entries. This model does not
 * allow selected elements, which are not in the list of valid elements.
 *
 * @author Thomas Morgner
 */
public class KeyedComboBoxModel<K, V> implements ComboBoxModel {

  /**
   * The internal data carrier to map keys to values and vice versa.
   */
  private static class ComboBoxItemPair<K, V> {
    /**
     * The key.
     */
    private K key;
    /**
     * The value for the key.
     */
    private V value;

    /**
     * Creates a new item pair for the given key and value. The value can be changed later, if needed.
     *
     * @param key   the key
     * @param value the value
     */
    private ComboBoxItemPair( final K key, final V value ) {
      this.key = key;
      this.value = value;
    }

    /**
     * Returns the key.
     *
     * @return the key.
     */
    public K getKey() {
      return key;
    }

    /**
     * Returns the value.
     *
     * @return the value for this key.
     */
    public V getValue() {
      return value;
    }

    /**
     * Redefines the value stored for that key.
     *
     * @param value the new value.
     */
    public void setValue( final V value ) {
      this.value = value;
    }
  }

  private int selectedItemIndex;
  private V selectedItemValue;
  private ArrayList<ComboBoxItemPair<K, V>> data;
  private ArrayList<ListDataListener> listdatalistener;
  private transient ListDataListener[] tempListeners;
  private boolean allowOtherValue;

  /**
   * Creates a new keyed combobox model.
   */
  public KeyedComboBoxModel() {
    data = new ArrayList<ComboBoxItemPair<K, V>>();
    listdatalistener = new ArrayList<ListDataListener>();
    selectedItemIndex = -1;
  }

  /**
   * Creates a new keyed combobox model for the given keys and values. Keys and values must have the same number of
   * items.
   *
   * @param keys   the keys
   * @param values the values
   */
  public KeyedComboBoxModel( final K[] keys, final V[] values ) {
    this();
    setData( keys, values );
  }

  /**
   * Replaces the data in this combobox model. The number of keys must be equals to the number of values.
   *
   * @param keys   the keys
   * @param values the values
   */
  public void setData( final K[] keys, final V[] values ) {
    if ( values.length != keys.length ) {
      throw new IllegalArgumentException( "Values and text must have the same length." );
    }

    data.clear();
    data.ensureCapacity( keys.length );

    for ( int i = 0; i < values.length; i++ ) {
      add( keys[ i ], values[ i ] );
    }

    selectedItemIndex = -1;
    final ListDataEvent evt = new ListDataEvent
      ( this, ListDataEvent.CONTENTS_CHANGED, 0, data.size() - 1 );
    fireListDataEvent( evt );
  }

  /**
   * Notifies all registered list data listener of the given event.
   *
   * @param evt the event.
   */
  protected synchronized void fireListDataEvent( final ListDataEvent evt ) {
    if ( tempListeners == null ) {
      tempListeners = listdatalistener.toArray
        ( new ListDataListener[ listdatalistener.size() ] );
    }

    final ListDataListener[] listeners = tempListeners;
    for ( int i = 0; i < listeners.length; i++ ) {
      final ListDataListener l = listeners[ i ];
      l.contentsChanged( evt );
    }
  }

  /**
   * Returns the selected item.
   *
   * @return The selected item or <code>null</code> if there is no selection
   */
  public V getSelectedItem() {
    return selectedItemValue;
  }

  /**
   * Defines the selected key. If the object is not in the list of values, no item gets selected.
   *
   * @param anItem the new selected item.
   */
  public void setSelectedKey( final K anItem ) {
    final int oldSelectedItem = this.selectedItemIndex;
    final int newSelectedItem = findDataElementIndex( anItem );
    if ( newSelectedItem == -1 ) {
      selectedItemIndex = -1;
      selectedItemValue = null;
    } else {
      selectedItemIndex = newSelectedItem;
      selectedItemValue = getElementAt( selectedItemIndex );
    }
    if ( oldSelectedItem != this.selectedItemIndex ) {
      fireListDataEvent( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, -1, -1 ) );
    }
  }

  /**
   * Set the selected item. The implementation of this  method should notify all registered
   * <code>ListDataListener</code>s that the contents have changed.
   *
   * @param anItem the list object to select or <code>null</code> to clear the selection
   */
  public final void setSelectedItem( final Object anItem ) {
    //noinspection unchecked
    setSelectedValue( (V) anItem );
  }

  public void setSelectedValue( final V anItem ) {
    final int oldSelectedItem = this.selectedItemIndex;
    final int newSelectedItem = findElementIndex( anItem );
    if ( newSelectedItem == -1 ) {
      if ( isAllowOtherValue() ) {
        selectedItemIndex = -1;
        selectedItemValue = anItem;
      } else {
        selectedItemIndex = -1;
        selectedItemValue = null;
      }
    } else {
      selectedItemIndex = newSelectedItem;
      selectedItemValue = getElementAt( selectedItemIndex );
    }
    if ( oldSelectedItem != this.selectedItemIndex ) {
      fireListDataEvent( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, -1, -1 ) );
    }
  }

  private boolean isAllowOtherValue() {
    return allowOtherValue;
  }

  public void setAllowOtherValue( final boolean allowOtherValue ) {
    this.allowOtherValue = allowOtherValue;
  }

  /**
   * Adds a listener to the list that's notified each time a change to the data model occurs.
   *
   * @param l the <code>ListDataListener</code> to be added
   */
  public synchronized void addListDataListener( final ListDataListener l ) {
    if ( l == null ) {
      throw new NullPointerException();
    }
    listdatalistener.add( l );
    tempListeners = null;
  }

  /**
   * Returns the value at the specified index.
   *
   * @param index the requested index
   * @return the value at <code>index</code>
   */
  public V getElementAt( final int index ) {
    if ( index == -1 || index >= data.size() ) {
      return null;
    }

    final ComboBoxItemPair<K, V> datacon = data.get( index );
    if ( datacon == null ) {
      return null;
    }
    return datacon.getValue();
  }

  /**
   * Returns the key from the given index.
   *
   * @param index the index of the key.
   * @return the the key at the specified index.
   */
  public K getKeyAt( final int index ) {
    if ( index >= data.size() ) {
      return null;
    }

    if ( index < 0 ) {
      return null;
    }

    final ComboBoxItemPair<K, V> datacon = data.get( index );
    if ( datacon == null ) {
      return null;
    }
    return datacon.getKey();
  }

  /**
   * Returns the selected data element or null if none is set.
   *
   * @return the selected data element.
   */
  public K getSelectedKey() {
    return getKeyAt( selectedItemIndex );
  }

  /**
   * Returns the length of the list.
   *
   * @return the length of the list
   */
  public int getSize() {
    return data.size();
  }

  /**
   * Removes a listener from the list that's notified each time a change to the data model occurs.
   *
   * @param l the <code>ListDataListener</code> to be removed
   */
  public void removeListDataListener( final ListDataListener l ) {
    listdatalistener.remove( l );
    tempListeners = null;
  }

  /**
   * Searches an element by its key value. This method is called from setSelectedKey(..).
   *
   * @param anItem the item
   * @return the index of the item or -1 if not found.
   */
  private int findDataElementIndex( final K anItem ) {
    for ( int i = 0; i < data.size(); i++ ) {
      final ComboBoxItemPair<K, V> datacon = data.get( i );
      final K key = datacon.getKey();
      if ( anItem == key ) {
        return i;
      }
      if ( anItem != null && anItem.equals( key ) ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Tries to find the index of element with the given value. This method is called by the setSelectedItem method and
   * returns the first occurence of the element.
   *
   * @param anItem the key for the element to be searched.
   * @return the index of the key, or -1 if not found.
   */
  public int findElementIndex( final V anItem ) {
    for ( int i = 0; i < data.size(); i++ ) {
      final ComboBoxItemPair<K, V> datacon = data.get( i );
      final Object value = datacon.getValue();
      if ( anItem == value ) {
        return i;
      }
      if ( anItem != null && anItem.equals( value ) ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Removes an entry from the model.
   *
   * @param key the key
   */
  public void removeDataElement( final K key ) {
    final int idx = findDataElementIndex( key );
    if ( idx == -1 ) {
      return;
    }

    data.remove( idx );
    final ListDataEvent evt = new ListDataEvent
      ( this, ListDataEvent.INTERVAL_REMOVED, idx, idx );
    fireListDataEvent( evt );
  }

  /**
   * Adds a new entry to the model.
   *
   * @param key    the key
   * @param cbitem the display value.
   */
  public void add( final K key, final V cbitem ) {
    final ComboBoxItemPair<K, V> con = new ComboBoxItemPair<K, V>( key, cbitem );
    data.add( con );
    final ListDataEvent evt = new ListDataEvent
      ( this, ListDataEvent.INTERVAL_ADDED, data.size() - 2, data.size() - 2 );
    fireListDataEvent( evt );
  }

  public void update( final int index, final K key, final V cbitem ) {
    final ComboBoxItemPair<K, V> con = new ComboBoxItemPair<K, V>( key, cbitem );
    data.set( index, con );
    final ListDataEvent evt = new ListDataEvent
      ( this, ListDataEvent.CONTENTS_CHANGED, index, index );
    fireListDataEvent( evt );
  }

  /**
   * Removes all entries from the model.
   */
  public void clear() {
    final int size = getSize();
    data.clear();
    fireListDataEvent( new ListDataEvent( this, ListDataEvent.INTERVAL_REMOVED, 0, size - 1 ) );
    selectedItemIndex = -1;
    selectedItemValue = null;
    fireListDataEvent( new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, -1, -1 ) );
  }

  public int getSelectedItemIndex() {
    return selectedItemIndex;
  }

  public void remove( final int index ) {
    data.remove( index );
    final ListDataEvent evt = new ListDataEvent( this, ListDataEvent.INTERVAL_REMOVED, index, index );
    fireListDataEvent( evt );
  }
}
