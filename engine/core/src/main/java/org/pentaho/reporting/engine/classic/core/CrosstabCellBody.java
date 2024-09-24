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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabCellBodyType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import java.util.ArrayList;

public class CrosstabCellBody extends GroupBody {
  private static final CrosstabCell[] EMPTY_ARRAY = new CrosstabCell[0];
  private ArrayList<CrosstabCell> allElements;
  private transient CrosstabCell[] allElementsCached;
  private DetailsHeader detailsHeader;

  public CrosstabCellBody() {
    setElementType( CrosstabCellBodyType.INSTANCE );
    getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, Boolean.FALSE );

    detailsHeader = new DetailsHeader();
    registerAsChild( detailsHeader );
  }

  public Group getGroup() {
    return null;
  }

  /**
   * Returns the group header.
   * <P>
   * The group header is a report band that contains elements that should be printed at the start of a group.
   *
   * @return the group header.
   */
  public DetailsHeader getHeader() {
    return detailsHeader;
  }

  /**
   * Sets the header for the group.
   *
   * @param header
   *          the header (null not permitted).
   * @throws NullPointerException
   *           if the given header is null
   */
  public void setHeader( final DetailsHeader header ) {
    if ( header == null ) {
      throw new NullPointerException( "Header must not be null" );
    }
    validateLooping( header );
    if ( unregisterParent( header ) ) {
      return;
    }

    final Element element = this.detailsHeader;
    this.detailsHeader.setParent( null );
    this.detailsHeader = header;
    this.detailsHeader.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.detailsHeader );
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
  public void addElement( final CrosstabCell element ) {
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
  public void addElement( final int position, final CrosstabCell element ) {
    if ( position < 1 ) {
      throw new IllegalArgumentException( "Position < 1" );
    }
    if ( position > getElementCount() ) {
      throw new IllegalArgumentException( "Position > ElementCount" );
    }
    if ( element == null ) {
      throw new NullPointerException( "Band.addElement(...): element is null." );
    }

    validateLooping( element );
    if ( unregisterParent( element ) ) {
      return;
    }

    if ( allElements == null ) {
      allElements = new ArrayList<CrosstabCell>();
    }
    // add the element, update the childs Parent and the childs stylesheet.
    allElements.add( position - 1, element );
    allElementsCached = null;

    // then add the parents, or the band's parent will be unregistered ..
    registerAsChild( element );
    notifyNodeChildAdded( element );
  }

  /**
   * Returns the matching crosstab-cell for the given key set. When searching a detail cell, give an empty set. When
   * searching for a column-summary cell, give the column group field. Same for row-summary-cells. For total cells, give
   * both the column and row field.
   *
   * @param rowKeys
   *          the known row-keys for the lookup.
   * @param colKeys
   *          the known col-keys for the lookup.
   * @return the first element with the specified name, or <code>null</code> if there is no such element.
   * @throws NullPointerException
   *           if the given name is null.
   */
  public CrosstabCell findElement( final String rowKeys, final String colKeys ) {
    final CrosstabCell[] elements = internalGetElementArray();
    final int elementsSize = elements.length;

    for ( int i = 0; i < elementsSize; i++ ) {
      final CrosstabCell e = elements[i];
      if ( e == null ) {
        continue;
      }

      final String cellColField = e.getColumnField();
      final String cellRowField = e.getRowField();

      final boolean colFieldMatch = equalString( cellColField, colKeys );
      final boolean rowFieldMatch = equalString( cellRowField, rowKeys );
      if ( colFieldMatch && rowFieldMatch ) {
        return e;
      }
    }

    return null;
  }

  private boolean equalString( final String s1, final String s2 ) {
    if ( s1 != null ) {
      return s1.equals( s2 );
    } else {
      return s2 == null;
    }
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
    // noinspection SuspiciousMethodCalls
    allElements.remove( e );
    allElementsCached = null;
    notifyNodeChildRemoved( e );
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
    if ( position == 0 ) {
      if ( element instanceof DetailsHeader == false ) {
        throw new IllegalArgumentException();
      }

      setHeader( (DetailsHeader) element );
      return;
    }

    if ( element instanceof CrosstabCell == false ) {
      throw new IllegalArgumentException();
    }

    validateLooping( element );
    if ( unregisterParent( element ) ) {
      return;
    }

    final int insertPosition = position - 1;

    if ( allElements == null ) {
      throw new IllegalStateException( "The throws above should have caught that state" );
    }
    // add the element, update the childs Parent and the childs stylesheet.
    final Element o = allElements.set( insertPosition, (CrosstabCell) element );
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
      return 1;
    }
    return 1 + allElements.size();
  }

  /**
   * An internal method that allows other internal methods to work with the uncloned backend.
   *
   * @return the elements as array.
   */
  private CrosstabCell[] internalGetElementArray() {
    if ( allElementsCached == null ) {
      if ( allElements == null || allElements.isEmpty() ) {
        allElementsCached = EMPTY_ARRAY;
      } else {
        CrosstabCell[] elements = new CrosstabCell[allElements.size()];
        elements = allElements.toArray( elements );
        allElementsCached = elements;
      }
    }
    return allElementsCached;
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
    if ( index == 0 ) {
      return detailsHeader;
    }

    if ( allElements == null ) {
      throw new IndexOutOfBoundsException( "This index is invalid." );
    }
    return allElements.get( index - 1 );
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
  public CrosstabCellBody clone() {
    final CrosstabCellBody b = (CrosstabCellBody) super.clone();
    b.detailsHeader = (DetailsHeader) detailsHeader.clone();
    b.registerAsChild( b.detailsHeader );

    if ( allElements != null ) {
      final int elementSize = allElements.size();
      b.allElements = (ArrayList<CrosstabCell>) allElements.clone();
      b.allElements.clear();
      b.allElementsCached = new CrosstabCell[elementSize];

      if ( allElementsCached != null ) {
        for ( int i = 0; i < elementSize; i++ ) {
          final CrosstabCell eC = (CrosstabCell) allElementsCached[i].clone();
          b.allElements.add( eC );
          b.allElementsCached[i] = eC;
          eC.setParent( b );
        }
      } else {
        for ( int i = 0; i < elementSize; i++ ) {
          final CrosstabCell e = allElements.get( i );
          final CrosstabCell eC = (CrosstabCell) e.clone();
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
  public CrosstabCellBody derive( final boolean preserveElementInstanceIds ) {
    final CrosstabCellBody b = (CrosstabCellBody) super.derive( preserveElementInstanceIds );
    b.detailsHeader = (DetailsHeader) detailsHeader.derive( preserveElementInstanceIds );
    b.registerAsChild( b.detailsHeader );

    if ( allElements != null ) {
      final int elementSize = allElements.size();
      b.allElements = (ArrayList<CrosstabCell>) allElements.clone();
      b.allElements.clear();
      b.allElementsCached = new CrosstabCell[elementSize];

      if ( allElementsCached != null ) {
        for ( int i = 0; i < elementSize; i++ ) {
          final CrosstabCell eC = (CrosstabCell) allElementsCached[i].derive( preserveElementInstanceIds );
          b.allElements.add( eC );
          b.allElementsCached[i] = eC;
          eC.setParent( b );
        }
      } else {
        for ( int i = 0; i < elementSize; i++ ) {
          final CrosstabCell e = allElements.get( i );
          final CrosstabCell eC = (CrosstabCell) e.derive( preserveElementInstanceIds );
          b.allElements.add( eC );
          b.allElementsCached[i] = eC;
          eC.setParent( b );
        }
      }
    }
    return b;
  }
}
