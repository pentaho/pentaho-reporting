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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * The WeakReference list uses <code>java.lang.ref.WeakReference</code>s to store its contents. In contrast to the
 * WeakHashtable, this list knows how to restore missing content, so that garbage collected elements can be restored
 * when they are accessed.
 * <p/>
 * By default this list can contain 25 elements, where the first element is stored using a strong reference, which is
 * not garbage collected.
 * <p/>
 * Restoring the elements is not implemented, concrete implementations will have to override the
 * <code>restoreChild(int)</code> method. The <code>getMaxChildCount</code> method defines the maxmimum number of
 * children in the list. When more than <code>maxChildCount</code> elements are contained in this list, add will always
 * return false to indicate that adding the element failed.
 * <p/>
 * To customize the list, override createReference to create a different kind of reference.
 * <p/>
 * This list is able to add or replace elements, but inserting or removing of elements is not possible.
 *
 * @author Thomas Morgner
 */
public abstract class WeakReferenceList<T> implements Serializable, Cloneable {
  private static final Log logger = LogFactory.getLog( WeakReferenceList.class );
  /**
   * The master element.
   */
  private T master;

  /**
   * Storage for the references.
   */
  private transient Reference<T>[] childs;

  /**
   * The current number of elements.
   */
  private int size;

  /**
   * The maximum number of elements.
   */
  private final int maxChilds;

  /**
   * Creates a new weak reference list. The storage of the list is limited to getMaxChildCount() elements.
   *
   * @param maxChildCount
   *          the maximum number of elements.
   */
  protected WeakReferenceList( final int maxChildCount ) {
    this.maxChilds = maxChildCount;
    // noinspection unchecked
    this.childs = new Reference[maxChildCount - 1];
  }

  /**
   * Returns the maximum number of children in this list.
   *
   * @return the maximum number of elements in this list.
   */
  protected final int getMaxChildCount() {
    return maxChilds;
  }

  /**
   * Returns the master element of this list. The master element is the element stored by a strong reference and cannot
   * be garbage collected.
   *
   * @return the master element
   */
  protected Object getMaster() {
    return master;
  }

  protected void setMaster( final T master ) {
    this.master = master;
  }

  /**
   * Attempts to restore the child stored on the given index.
   *
   * @param index
   *          the index.
   * @return null if the child could not be restored or the restored child.
   */
  protected abstract T restoreChild( int index );

  /**
   * Returns the child stored at the given index. If the child has been garbage collected, it gets restored using the
   * restoreChild function.
   *
   * @param index
   *          the index.
   * @return the object.
   */
  public Object get( final int index ) {
    if ( isMaster( index ) ) {
      return master;
    } else {
      final Reference<T> ref = childs[getChildPos( index )];
      if ( ref == null ) {
        throw new IllegalStateException( "State: " + index );
      }
      T ob = ref.get();
      if ( ob == null ) {
        ob = restoreChild( index );
        childs[getChildPos( index )] = createReference( ob );
      }
      return ob;
    }
  }

  public T getRaw( final int index ) {
    if ( isMaster( index ) ) {
      return master;
    } else {
      final Reference<T> ref = childs[getChildPos( index )];
      if ( ref == null ) {
        throw new IllegalStateException( "State: " + index );
      }
      return ref.get();
    }
  }

  /**
   * Replaces the child stored at the given index with the new child which can be null.
   *
   * @param report
   *          the object.
   * @param index
   *          the index.
   */
  public void set( final T report, final int index ) {
    if ( isMaster( index ) ) {
      master = report;
    } else {
      childs[getChildPos( index )] = createReference( report );
    }
  }

  /**
   * Creates a new reference for the given object.
   *
   * @param o
   *          the object.
   * @return a WeakReference for the object o without any ReferenceQueue attached.
   */
  private Reference<T> createReference( final T o ) {
    return new WeakReference<T>( o );
  }

  /**
   * Adds the element to the list. If the maximum size of the list is exceeded, this function returns false to indicate
   * that adding failed.
   *
   * @param rs
   *          the object.
   * @return true, if the object was successfully added to the list, false otherwise
   */
  public boolean add( final T rs ) {
    if ( size == 0 ) {
      master = rs;
      size = 1;
      return true;
    } else {
      if ( size < getMaxChildCount() ) {
        childs[size - 1] = createReference( rs );
        size++;
        return true;
      } else {
        // was not able to add this to this list, maximum number of entries reached.
        return false;
      }
    }
  }

  /**
   * Returns true, if the given index denotes a master index of this list.
   *
   * @param index
   *          the index.
   * @return true if the index is a master index.
   */
  protected boolean isMaster( final int index ) {
    return index % getMaxChildCount() == 0;
  }

  /**
   * Returns the internal storage position for the child.
   *
   * @param index
   *          the index.
   * @return the internal storage index.
   */
  protected int getChildPos( final int index ) {
    return index % getMaxChildCount() - 1;
  }

  /**
   * Returns the size of the list.
   *
   * @return the size.
   */
  public int getSize() {
    return size;
  }

  /**
   * Serialisation support. The transient child elements were not saved.
   *
   * @param in
   *          the input stream.
   * @throws IOException
   *           if there is an I/O error.
   * @throws ClassNotFoundException
   *           if a serialized class is not defined on this system.
   * @noinspection unchecked
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    childs = new Reference[getMaxChildCount() - 1];
    for ( int i = 0; i < childs.length; i++ ) {
      childs[i] = createReference( null );
    }
  }

  /**
   * Creates and returns a copy of this object. The precise meaning of "copy" may depend on the class of the object. The
   * general intent is that, for any object <tt>x</tt>, the expression: <blockquote>
   * 
   * <pre>
   * x.clone() != x
   * </pre>
   * 
   * </blockquote> will be true, and that the expression: <blockquote>
   * 
   * <pre>
   * x.clone().getClass() == x.getClass()
   * </pre>
   * 
   * </blockquote> will be <tt>true</tt>, but these are not absolute requirements. While it is typically the case that:
   * <blockquote>
   * 
   * <pre>
   * x.clone().equals( x )
   * </pre>
   * 
   * </blockquote> will be <tt>true</tt>, this is not an absolute requirement.
   *
   * By convention, the returned object should be obtained by calling <tt>super.clone</tt>. If a class and all of its
   * superclasses (except <tt>Object</tt>) obey this convention, it will be the case that <tt>x.clone().getClass() ==
   * x.getClass()</tt>.
   *
   * By convention, the object returned by this method should be independent of this object (which is being cloned). To
   * achieve this independence, it may be necessary to modify one or more fields of the object returned by
   * <tt>super.clone</tt> before returning it. Typically, this means copying any mutable objects that comprise the
   * internal "deep structure" of the object being cloned and replacing the references to these objects with references
   * to the copies. If a class contains only primitive fields or references to immutable objects, then it is usually the
   * case that no fields in the object returned by <tt>super.clone</tt> need to be modified.
   *
   * The method <tt>clone</tt> for class <tt>Object</tt> performs a specific cloning operation. First, if the class of
   * this object does not implement the interface <tt>Cloneable</tt>, then a <tt>CloneNotSupportedException</tt> is
   * thrown. Note that all arrays are considered to implement the interface <tt>Cloneable</tt>. Otherwise, this method
   * creates a new instance of the class of this object and initializes all its fields with exactly the contents of the
   * corresponding fields of this object, as if by assignment; the contents of the fields are not themselves cloned.
   * Thus, this method performs a "shallow copy" of this object, not a "deep copy" operation.
   *
   * The class <tt>Object</tt> does not itself implement the interface <tt>Cloneable</tt>, so calling the <tt>clone</tt>
   * method on an object whose class is <tt>Object</tt> will result in throwing an exception at run time.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException
   *           if the object's class does not support the <code>Cloneable</code> interface. Subclasses that override the
   *           <code>clone</code> method can also throw this exception to indicate that an instance cannot be cloned.
   * @see Cloneable
   */
  protected Object clone() throws CloneNotSupportedException {
    final WeakReferenceList list = (WeakReferenceList) super.clone();
    list.childs = childs.clone();
    return list;
  }
}
