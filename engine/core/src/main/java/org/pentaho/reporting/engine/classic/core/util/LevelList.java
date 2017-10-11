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

package org.pentaho.reporting.engine.classic.core.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * A list that associates a level (instance of <code>Integer</code>) with each element in the list.
 *
 * @author Thomas Morgner
 */
public class LevelList implements Cloneable {
  /**
   * A static object array of size zero.
   */
  private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  /**
   * A static object array of size zero.
   */
  private static final Integer[] EMPTY_INTEGER_ARRAY = new Integer[0];

  /**
   * Constant for level zero.
   */
  private static final Integer ZERO = new Integer( 0 );

  /**
   * A treeset to build the iterator.
   */
  private transient TreeSet iteratorSetAsc;

  /**
   * A treeset to build the iterator.
   */
  private transient TreeSet iteratorSetDesc;

  /**
   * A treeset to cache the level iterator.
   */
  private HashMap iteratorCache;

  /**
   * A comparator for levels in descending order.
   */
  private static final class DescendingComparator implements Comparator, Serializable {
    /**
     * Default constructor.
     */
    private DescendingComparator() {
    }

    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     * <p>
     *
     * @param o1
     *          the first object to be compared.
     * @param o2
     *          the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     *         than the second.
     * @throws ClassCastException
     *           if the arguments' types prevent them from being compared by this Comparator.
     */
    public int compare( final Object o1, final Object o2 ) {
      if ( ( o1 instanceof Comparable ) == false ) {
        throw new ClassCastException( "Need comparable Elements" );
      }
      if ( ( o2 instanceof Comparable ) == false ) {
        throw new ClassCastException( "Need comparable Elements" );
      }
      final Comparable c1 = (Comparable) o1;
      final Comparable c2 = (Comparable) o2;
      return -1 * c1.compareTo( c2 );
    }
  }

  /**
   * An list that caches all elements for a certain level.
   */
  private static final class ElementLevelList {
    /**
     * The level list.
     */
    private Object[] data;

    /**
     * Creates an iterator that provides access to all the elements in a list at the specified level.
     *
     * @param list
     *          the list (null not permitted).
     * @param level
     *          the level.
     */
    private ElementLevelList( final LevelList list, final int level ) {
      if ( list == null ) {
        throw new NullPointerException();
      }

      final Object[] rawElements = list.getRawElements();
      final Integer[] rawLevels = list.getRawLevels();

      final ArrayList datalist = new ArrayList( rawElements.length );
      for ( int i = 0; i < rawElements.length; i++ ) {
        final Object iNext = rawElements[i];
        final Integer iLevel = rawLevels[i];
        if ( iLevel.intValue() == level ) {
          datalist.add( iNext );
        }
      }
      data = datalist.toArray();
    }

    /**
     * Returns the data for this level as object array.
     *
     * @return the data for this level as object array.
     */
    protected Object[] getData() {
      return (Object[]) data.clone();
    }

    /**
     * Returns the data for this level as object array. Behaves like ArrayList.toArray(..)
     *
     * @param target
     *          object array that should receive the contents
     * @return the data for this level as object array.
     */
    protected Object[] getData( Object[] target ) {
      if ( target == null ) {
        target = new Object[data.length];
      } else if ( target.length < data.length ) {
        target = (Object[]) Array.newInstance( target.getClass().getComponentType(), data.length );
      }
      System.arraycopy( data, 0, target, 0, data.length );
      if ( target.length > data.length ) {
        target[data.length] = null;
      }
      return target;
    }

    /**
     * Returns the size if the list.
     *
     * @return the size.
     */
    protected int size() {
      return data.length;
    }
  }

  /**
   * The elements.
   */
  private ArrayList elements;

  /**
   * The levels.
   */
  private ArrayList levels;

  /**
   * Creates a new list (initially empty).
   */
  public LevelList() {
    this.elements = new ArrayList();
    this.levels = new ArrayList();
    this.iteratorCache = new HashMap();
  }

  /**
   * Returns the number of elements in the list.
   *
   * @return the element count.
   */
  public int size() {
    return elements.size();
  }

  /**
   * Returns an iterator that iterates through the levels in ascending order.
   *
   * @return an iterator.
   */
  public Iterator getLevelsAscending() {
    if ( iteratorSetAsc == null ) {
      iteratorSetAsc = new TreeSet();
      final Integer[] ilevels = (Integer[]) levels.toArray( new Integer[levels.size()] );
      for ( int i = 0; i < ilevels.length; i++ ) {
        if ( iteratorSetAsc.contains( ilevels[i] ) == false ) {
          iteratorSetAsc.add( ilevels[i] );
        }
      }
    }
    return iteratorSetAsc.iterator();
  }

  /**
   * Returns the levels of the elements in the list in descending order.
   *
   * @return the levels in descending order.
   */
  public Integer[] getLevelsDescendingArray() {
    if ( levels.isEmpty() ) {
      return LevelList.EMPTY_INTEGER_ARRAY;
    }
    if ( iteratorSetDesc == null ) {
      final Integer[] ilevels = (Integer[]) levels.toArray( new Integer[levels.size()] );
      iteratorSetDesc = new TreeSet( new DescendingComparator() );
      for ( int i = 0; i < ilevels.length; i++ ) {
        if ( iteratorSetDesc.contains( ilevels[i] ) == false ) {
          iteratorSetDesc.add( ilevels[i] );
        }
      }
    }
    return (Integer[]) iteratorSetDesc.toArray( new Integer[iteratorSetDesc.size()] );
  }

  /**
   * Returns an iterator that iterates through the levels in descending order.
   *
   * @return an iterator.
   */
  public Iterator getLevelsDescending() {
    if ( iteratorSetDesc == null ) {
      iteratorSetDesc = new TreeSet( new DescendingComparator() );
      final Integer[] ilevels = (Integer[]) levels.toArray( new Integer[levels.size()] );
      for ( int i = 0; i < ilevels.length; i++ ) {
        if ( iteratorSetDesc.contains( ilevels[i] ) == false ) {
          iteratorSetDesc.add( ilevels[i] );
        }
      }
    }
    return iteratorSetDesc.iterator();
  }

  /**
   * Returns the elements as an array.
   *
   * @return the array.
   */
  public Object[] toArray() {
    return elements.toArray();
  }

  /**
   * Returns an iterator for all the elements at a given level.
   *
   * @param level
   *          the level.
   * @param target
   *          the target array that should receive the contentes
   * @return the data for the level as object array.
   */
  public Object[] getElementArrayForLevel( final int level, final Object[] target ) {
    ElementLevelList it = (ElementLevelList) iteratorCache.get( IntegerCache.getInteger( level ) );
    if ( it == null ) {
      it = new ElementLevelList( this, level );
      iteratorCache.put( IntegerCache.getInteger( level ), it );
    }
    if ( target == null ) {
      return it.getData();
    } else {
      return it.getData( target );
    }
  }

  /**
   * Returns an iterator for all the elements at a given level.
   *
   * @param level
   *          the level.
   * @return the data for the level as object array.
   */
  public Object[] getElementArrayForLevel( final int level ) {
    return getElementArrayForLevel( level, null );
  }

  /**
   * Returns the numer of elements registered for an certain level.
   *
   * @param level
   *          the level that should be queried
   * @return the numer of elements in that level
   */
  public int getElementCountForLevel( final int level ) {
    ElementLevelList it = (ElementLevelList) iteratorCache.get( IntegerCache.getInteger( level ) );
    if ( it == null ) {
      it = new ElementLevelList( this, level );
      iteratorCache.put( IntegerCache.getInteger( level ), it );
    }
    return it.size();
  }

  /**
   * Creates an iterator for the elements in the list at the given level.
   *
   * @param level
   *          the level.
   * @return An iterator.
   * @deprecated use the array methods for best performance.
   */
  protected Iterator getElementsForLevel( final int level ) {
    final List list = Arrays.asList( getElementArrayForLevel( level ) );
    return Collections.unmodifiableList( list ).iterator();
  }

  /**
   * Returns the element with the given index.
   *
   * @param index
   *          the index.
   * @return the element.
   */
  public Object get( final int index ) {
    return elements.get( index );
  }

  /**
   * Adds an element at level zero.
   *
   * @param o
   *          the element.
   */
  public void add( final Object o ) {
    elements.add( o );
    levels.add( LevelList.ZERO );
    iteratorSetAsc = null;
    iteratorSetDesc = null;
    iteratorCache.remove( LevelList.ZERO );
  }

  /**
   * Adds an element at a given level.
   *
   * @param o
   *          the element.
   * @param level
   *          the level.
   */
  public void add( final Object o, final int level ) {
    elements.add( o );
    final Integer i = IntegerCache.getInteger( level );
    levels.add( i );
    iteratorCache.remove( i );
    iteratorSetAsc = null;
    iteratorSetDesc = null;
  }

  /**
   * Sets the level for an element.
   *
   * @param index
   *          the element index.
   * @param level
   *          the level.
   */
  public void setLevel( final int index, final int level ) {
    levels.set( index, IntegerCache.getInteger( level ) );
  }

  /**
   * Returns the level for an element.
   *
   * @param index
   *          the element index.
   * @return the level.
   */
  public int getLevel( final int index ) {
    return ( (Integer) levels.get( index ) ).intValue();
  }

  /**
   * Returns the index of an element.
   *
   * @param o
   *          the element.
   * @return the index.
   */
  public int indexOf( final Object o ) {
    return elements.indexOf( o );
  }

  /**
   * Returns the level of an element.
   *
   * @param o
   *          the element.
   * @return the level.
   */
  public int getLevel( final Object o ) {
    return getLevel( indexOf( o ) );
  }

  /**
   * Sets the level of an element.
   *
   * @param o
   *          the element.
   * @param level
   *          the level.
   */
  public void setLevel( final Object o, final int level ) {
    setLevel( indexOf( o ), level );
  }

  /**
   * Clones the list.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final LevelList l = (LevelList) super.clone();
    l.elements = (ArrayList) elements.clone();
    l.levels = (ArrayList) levels.clone();
    l.iteratorCache = (HashMap) iteratorCache.clone();
    return l;
  }

  /**
   * Clears the list.
   */
  public void clear() {
    elements.clear();
    levels.clear();
    iteratorCache.clear();
    iteratorSetAsc = null;
    iteratorSetDesc = null;
  }

  /**
   * Returns all stored objects as object array.
   *
   * @return all elements as object array.
   */
  protected Object[] getRawElements() {
    if ( elements.isEmpty() ) {
      return LevelList.EMPTY_OBJECT_ARRAY;
    }
    return elements.toArray( new Object[elements.size()] );
  }

  /**
   * Returns all active levels as java.lang.Integer array.
   *
   * @return all levels as Integer array.
   */
  protected Integer[] getRawLevels() {
    if ( levels.isEmpty() ) {
      return LevelList.EMPTY_INTEGER_ARRAY;
    }
    return (Integer[]) levels.toArray( new Integer[levels.size()] );
  }
}
