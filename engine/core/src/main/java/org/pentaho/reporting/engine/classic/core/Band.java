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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.BandType;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A report band is a collection of other elements and bands, similiar to an AWT-Container.
 * <p/>
 * This implementation is not synchronized, to take care that you externally synchronize it when using multiple threads
 * to modify instances of this class.
 * <p/>
 * Trying to add a parent of an band as child to the band, will result in an exception.
 *
 * @author David Gilbert
 * @author Thomas Morgner
 */
public class Band extends Section {
  /**
   * An empty array to prevent object creation.
   */
  private static final Element[] EMPTY_ARRAY = new Element[0];

  /**
   * All the elements for this band.
   */
  private ArrayList<Element> allElements;

  /**
   * Cached elements.
   */
  private transient Element[] allElementsCached;

  /**
   * The prefix for anonymous bands, bands without an userdefined name.
   */
  public static final String ANONYMOUS_BAND_PREFIX = "anonymousBand@";

  /**
   * Constructs a new band (initially empty).
   */
  public Band() {
    setElementType( new BandType() );
  }

  public Band( final InstanceID id ) {
    super( id );
    setElementType( new BandType() );
  }

  /**
   * Constructs a new band with the given pagebreak attributes. Pagebreak attributes have no effect on subbands.
   *
   * @param pagebreakAfter
   *          defines, whether a pagebreak should be done after that band was printed.
   * @param pagebreakBefore
   *          defines, whether a pagebreak should be done before that band gets printed.
   */
  public Band( final boolean pagebreakBefore, final boolean pagebreakAfter ) {
    this();
    if ( pagebreakBefore ) {
      setPagebreakBeforePrint( pagebreakBefore );
    }
    if ( pagebreakAfter ) {
      setPagebreakAfterPrint( pagebreakAfter );
    }
  }

  /**
   * Returns the global stylesheet for all bands. This stylesheet provides the predefined default values for some of the
   * stylekeys.
   *
   * @return the global default stylesheet.
   */
  public ElementStyleSheet getDefaultStyleSheet() {
    return BandDefaultStyleSheet.getBandDefaultStyle();
  }

  /**
   * Adds a report element to the band.
   *
   * @param element
   *          the element that should be added
   * @throws NullPointerException
   *           if the given element is null
   * @throws IllegalArgumentException
   *           if the position is invalid, either negative or greater than the number of elements in this band or if the
   *           given element is a parent of this element.
   */
  public void addElement( final Element element ) {
    addElement( getElementCount(), element );
  }

  /**
   * Adds a report element to the band. The element will be inserted at the specified position.
   *
   * @param position
   *          the position where to insert the element
   * @param element
   *          the element that should be added
   * @throws NullPointerException
   *           if the given element is null
   * @throws IllegalArgumentException
   *           if the position is invalid, either negative or greater than the number of elements in this band or if the
   *           given element is a parent of this element.
   */
  public void addElement( final int position, final Element element ) {
    if ( position < 0 ) {
      throw new IllegalArgumentException( "Position < 0" );
    }
    if ( position > getElementCount() ) {
      throw new IllegalArgumentException( "Position < 0" );
    }
    if ( element == null ) {
      throw new NullPointerException( "Band.addElement(...): element is null." );
    }

    validateLooping( element );
    if ( unregisterParent( element ) ) {
      return;
    }

    if ( allElements == null ) {
      allElements = new ArrayList<Element>();
    }
    // add the element, update the childs Parent and the childs stylesheet.
    allElements.add( position, element );
    allElementsCached = null;

    // then add the parents, or the band's parent will be unregistered ..
    registerAsChild( element );
    notifyNodeChildAdded( element );
  }

  /**
   * Adds a collection of elements to the band.
   *
   * @param elements
   *          the element collection.
   * @throws NullPointerException
   *           if one of the given elements is null
   * @throws IllegalArgumentException
   *           if one of the given element is a parent of this element.
   */
  public void addElements( final Collection elements ) {
    if ( elements == null ) {
      throw new NullPointerException( "Band.addElements(...): collection is null." );
    }

    final Iterator iterator = elements.iterator();
    while ( iterator.hasNext() ) {
      final Element element = (Element) iterator.next();
      addElement( element );
    }
  }

  /**
   * Returns the first element in the list that is known by the given name. Functions should use
   * {@link org.pentaho.reporting.engine.classic.core.function.FunctionUtilities#findAllElements(Band, String)} or
   * {@link org.pentaho.reporting.engine.classic.core.function.FunctionUtilities#findElement(Band, String)} instead.
   *
   * @param name
   *          the element name.
   * @return the first element with the specified name, or <code>null</code> if there is no such element.
   * @throws NullPointerException
   *           if the given name is null.
   */
  public Element getElement( final String name ) {
    if ( name == null ) {
      throw new NullPointerException( "Band.getElement(...): name is null." );
    }

    final Element[] elements = internalGetElementArray();
    final int elementsSize = elements.length;
    for ( int i = 0; i < elementsSize; i++ ) {
      final Element e = elements[i];
      final String elementName = e.getName();
      if ( elementName != null ) {
        if ( elementName.equals( name ) ) {
          return e;
        }
      }
    }
    return null;
  }

  /**
   * Removes an element from the band.
   *
   * @param e
   *          the element to be removed.
   * @throws NullPointerException
   *           if the given element is null.
   */
  public void removeElement( final Element e ) {
    if ( e == null ) {
      throw new NullPointerException();
    }
    if ( e.getParentSection() != this ) {
      // this is none of my childs, ignore the request ...
      return;
    }
    if ( allElements == null ) {
      return;
    }

    e.setParent( null );
    allElements.remove( e );
    allElementsCached = null;
    notifyNodeChildRemoved( e );
  }

  public void removeElement( int index ) {
    removeElement( getElement( index ) );
  }

  public void setElementAt( final int position, final Element element ) {
    if ( position < 0 ) {
      throw new IllegalArgumentException( "Position < 0" );
    }
    if ( position >= getElementCount() ) {
      throw new IllegalArgumentException( "Position >= size" );
    }
    if ( element == null ) {
      throw new NullPointerException( "Band.addElement(...): element is null." );
    }

    validateLooping( element );
    if ( unregisterParent( element ) ) {
      return;
    }

    if ( allElements == null ) {
      throw new IllegalStateException( "The throws above should have caught that state" );
    }
    // add the element, update the childs Parent and the childs stylesheet.
    final Element o = allElements.set( position, element );
    o.setParent( null );
    allElementsCached = null;

    // then add the parents, or the band's parent will be unregistered ..
    registerAsChild( element );
    notifyNodeChildRemoved( o );
    notifyNodeChildAdded( element );

  }

  public void clear() {
    final Element[] elements = internalGetElementArray();
    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = elements[i];
      removeElement( element );
    }
  }

  /**
   * Returns the number of elements in this band.
   *
   * @return the number of elements of this band.
   */
  public int getElementCount() {
    if ( allElements == null ) {
      return 0;
    }
    return allElements.size();
  }

  /**
   * Returns an array of the elements in the band. If the band is empty, an empty array is returned.
   * <p/>
   * Implementation note: The array returned is a copy of the internal backend. Any modification of the array will no
   * longer result in modifications of the internal object state. To avoid unneccessary object creations, you can use
   * the {@link Band#unsafeGetElementArray()} method now.
   *
   * @return the elements.
   */
  public Element[] getElementArray() {
    return internalGetElementArray().clone();
  }

  /**
   * An internal method that allows other internal methods to work with the uncloned backend.
   *
   * @return the elements as array.
   */
  private Element[] internalGetElementArray() {
    if ( allElementsCached == null ) {
      if ( allElements == null || allElements.isEmpty() ) {
        allElementsCached = Band.EMPTY_ARRAY;
      } else {
        Element[] elements = new Element[allElements.size()];
        elements = allElements.toArray( elements );
        allElementsCached = elements;
      }
    }
    return allElementsCached;
  }

  public final Element[] unsafeGetElementArray() {
    return internalGetElementArray();
  }

  /**
   * Returns the element stored add the given index.
   *
   * @param index
   *          the element position within this band
   * @return the element
   * @throws IndexOutOfBoundsException
   *           if the index is invalid.
   */
  public Element getElement( final int index ) {
    if ( allElements == null ) {
      throw new IndexOutOfBoundsException( "This index is invalid." );
    }
    return allElements.get( index );
  }

  /**
   * Returns a string representation of the band, useful mainly for debugging purposes.
   *
   * @return a string representation of this band.
   */
  public String toString() {
    final StringBuilder b = new StringBuilder( 100 );
    b.append( this.getClass().getName() );
    b.append( "={name=\"" );
    b.append( getName() );
    b.append( "\", size=\"" );
    b.append( getElementCount() );
    b.append( "\", layout=\"" );
    b.append( getStyle().getStyleProperty( BandStyleKeys.LAYOUT ) );
    b.append( "\"}" );
    return b.toString();
  }

  /**
   * Clones this band and all elements contained in this band. After the cloning the band is no longer connected to a
   * report definition.
   *
   * @return the clone of this band.
   */
  public Band clone() {
    final Band b = (Band) super.clone();
    if ( allElements != null ) {
      final int elementSize = allElements.size();
      b.allElements = (ArrayList<Element>) allElements.clone();
      b.allElements.clear();
      b.allElementsCached = new Element[elementSize];

      if ( allElementsCached != null ) {
        for ( int i = 0; i < elementSize; i++ ) {
          final Element eC = (Element) allElementsCached[i].clone();
          b.allElements.add( eC );
          b.allElementsCached[i] = eC;
          eC.setParent( b );
        }
      } else {
        for ( int i = 0; i < elementSize; i++ ) {
          final Element e = allElements.get( i );
          final Element eC = (Element) e.clone();
          b.allElements.add( eC );
          b.allElementsCached[i] = eC;
          eC.setParent( b );
        }
      }
    }
    return b;
  }

  /**
   * Creates a deep copy of this element and regenerates all instance-ids.
   *
   * @return the copy of the element.
   */
  public Band derive( final boolean preserveElementInstanceIds ) {
    final Band b = (Band) super.derive( preserveElementInstanceIds );
    if ( allElements != null ) {
      final int elementSize = allElements.size();
      b.allElements = (ArrayList<Element>) allElements.clone();
      b.allElements.clear();
      b.allElementsCached = new Element[elementSize];

      if ( allElementsCached != null ) {
        for ( int i = 0; i < elementSize; i++ ) {
          final Element eC = allElementsCached[i].derive( preserveElementInstanceIds );
          b.allElements.add( eC );
          b.allElementsCached[i] = eC;
          eC.setParent( b );
        }
      } else {
        for ( int i = 0; i < elementSize; i++ ) {
          final Element e = allElements.get( i );
          final Element eC = e.derive( preserveElementInstanceIds );
          b.allElements.add( eC );
          b.allElementsCached[i] = eC;
          eC.setParent( b );
        }
      }
    }
    return b;
  }

  /**
   * Returns, whether the page layout manager should perform a pagebreak before this page is printed. This will have no
   * effect on empty pages or if the band is no root-level band.
   *
   * @return true, if to force a pagebreak before this band is printed, false otherwise
   */
  public boolean isPagebreakBeforePrint() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE );
  }

  /**
   * Defines, whether the page layout manager should perform a pagebreak before this page is printed. This will have no
   * effect on empty pages or if the band is no root-level band.
   *
   * @param pagebreakBeforePrint
   *          set to true, if to force a pagebreak before this band is printed, false otherwise
   */
  public void setPagebreakBeforePrint( final boolean pagebreakBeforePrint ) {
    getStyle().setBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, pagebreakBeforePrint );
    notifyNodePropertiesChanged();
  }

  /**
   * Returns, whether the page layout manager should perform a pagebreak before this page is printed. This will have no
   * effect on empty pages or if the band is no root-level band.
   *
   * @return true, if to force a pagebreak before this band is printed, false otherwise
   */
  public boolean isPagebreakAfterPrint() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER );
  }

  /**
   * Defines, whether the page layout manager should perform a pagebreak before this page is printed. This will have no
   * effect on empty pages or if the band is no root-level band.
   *
   * @param pagebreakAfterPrint
   *          set to true, if to force a pagebreak before this band is printed, false otherwise
   */
  public void setPagebreakAfterPrint( final boolean pagebreakAfterPrint ) {
    getStyle().setBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, pagebreakAfterPrint );
    notifyNodePropertiesChanged();
  }

  public void setLayout( final String layout ) {
    getStyle().setStyleProperty( BandStyleKeys.LAYOUT, layout );
    notifyNodePropertiesChanged();
  }

  public String getLayout() {
    return (String) getStyle().getStyleProperty( BandStyleKeys.LAYOUT );
  }

}
