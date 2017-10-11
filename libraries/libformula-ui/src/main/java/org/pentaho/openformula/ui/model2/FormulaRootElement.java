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

package org.pentaho.openformula.ui.model2;

import javax.swing.text.Element;
import java.util.ArrayList;

public class FormulaRootElement extends FormulaElement {
  private ArrayList<FormulaElement> elements;

  public FormulaRootElement( final FormulaDocument document ) {
    super( document, null );
    this.elements = new ArrayList<FormulaElement>();
    this.elements.add( new FormulaTextElement( document, this, "" ) );
  }

  public String getText() {
    final StringBuilder b = new StringBuilder( getEndOffset() );
    for ( int i = 0; i < elements.size(); i++ ) {
      final FormulaElement element = elements.get( i );
      if ( element != null ) {
        b.append( element.getText() );
      }
    }
    return b.toString();
  }

  /**
   * Gets the child element index closest to the given offset. The offset is specified relative to the beginning of the
   * document.  Returns <code>-1</code> if the <code>Element</code> is a leaf, otherwise returns the index of the
   * <code>Element</code> that best represents the given location.  Returns <code>0</code> if the location is less than
   * the start offset. Returns <code>getElementCount() - 1</code> if the location is greater than or equal to the end
   * offset.
   *
   * @param offset the specified offset >= 0
   * @return the element index >= 0
   */
  public int getElementIndex( final int offset ) {
    for ( int i = 0; i < elements.size(); i++ ) {
      final FormulaElement node = elements.get( i );
      final int nodeStartOffset = node.getStartOffset();
      final int nodeEndOffset = node.getEndOffset();

      if ( ( nodeStartOffset > offset ) &&
        ( ( nodeEndOffset > offset ) || ( nodeStartOffset == nodeEndOffset ) ) ) {
        return i;
      }

    }
    return elements.size() - 1;
  }

  /**
   * Gets the number of child elements contained by this element. If this element is a leaf, a count of zero is
   * returned.
   *
   * @return the number of child elements >= 0
   */
  public int getElementCount() {
    return elements.size();
  }

  /**
   * Fetches the child element at the given index.
   *
   * @param index the specified index >= 0
   * @return the child element.  If index is invalid, return null
   */
  public Element getElement( final int index ) {
    if ( index < elements.size() ) {
      return elements.get( index );
    } else {
      return null;
    }
  }

  /**
   * Is this element a leaf element? An element that <i>may</i> have children, even if it currently has no children,
   * would return <code>false</code>.
   *
   * @return true if a leaf element else false
   */
  public boolean isLeaf() {
    return false;
  }

  /**
   * Fetches the name of the element.  If the element is used to represent some type of structure, this would be the
   * type name.
   *
   * @return the element name
   */
  public String getName() {
    return "<root>"; // NON-NLS
  }

  public void setElements( final FormulaElement[] elements ) {
    this.elements.clear();
    for ( int i = 0; i < elements.length; i++ ) {
      final FormulaElement element = elements[ i ];
      if ( ( element != null ) && ( element.getParentElement() != this ) ) {
        throw new IllegalArgumentException();
      }
      this.elements.add( element );
    }
    if ( this.elements.isEmpty() ) {
      this.elements.add( new FormulaTextElement( (FormulaDocument) getDocument(), this, "" ) );
    }
    revalidateNodePositions();
  }

  public void setElement( final int index, final FormulaElement element ) {
    if ( element.getParentElement() != this ) {
      throw new IllegalArgumentException();
    }
    this.elements.set( index, element );
  }

  public void insertElement( final int index, final FormulaElement element ) {
    if ( element.getParentElement() != this ) {
      throw new IllegalArgumentException();
    }
    this.elements.add( index, element );
  }

  public void removeElement( final int index ) {
    this.elements.remove( index );
    if ( this.elements.isEmpty() ) {
      this.elements.add( new FormulaTextElement( (FormulaDocument) getDocument(), this, "" ) );
    }
  }

  public void revalidateStructure() {
    final FormulaElement[] formulaElements = this.elements.toArray( new FormulaElement[ this.elements.size() ] );
    final FormulaElement[] normalized = FormulaParser.normalizeDocument
      ( (FormulaDocument) getDocument(), formulaElements );
    setElements( normalized );
  }

  public void revalidateNodePositions() {
    setStartOffset( 0 );
    int cursor = 0;
    final int count = getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final FormulaElement node = (FormulaElement) getElement( i );
      if ( node != null ) {
        node.setStartOffset( cursor );
        cursor += node.getText().length();
        node.setEndOffset( cursor );
      }
    }
    setEndOffset( cursor );
  }

  public void print() {
    final FormulaElement[] formulaElements = this.elements.toArray( new FormulaElement[ this.elements.size() ] );
    for ( int i = 0; i < formulaElements.length; i++ ) {
      final FormulaElement element = formulaElements[ i ];
      System.out.println( i + " Name=" + element.getName() + "; Text='" + element.getText() + '\'' );
    }
  }

  public void clear() {
    elements.clear();
  }

  public void replace( final FormulaElement oldElement, final FormulaTextElement formulaTextElement,
                       final boolean hasDummyParams ) {
    final int idx = elements.indexOf( oldElement );
    if ( idx == -1 ) {
      throw new IllegalStateException();
    }

    final FormulaElement replacementElement;
    final Element origElement = getElement( idx );
    if ( ( hasDummyParams ) && ( origElement instanceof FormulaTextElement ) ) {
      // Replace the dummy parameter with the user specified parameter
      replacementElement = new FormulaTextElement( (FormulaDocument) formulaTextElement.getDocument(),
        (FormulaRootElement) origElement.getParentElement(),
        formulaTextElement.getText() );
    } else {
      replacementElement = formulaTextElement;
    }

    setElement( idx, replacementElement );
  }

  public void insert( final FormulaElement oldElement, final FormulaTextElement formulaTextElement ) {
    final int idx = elements.indexOf( oldElement );
    if ( idx == -1 ) {
      throw new IllegalStateException();
    }

    insertElement( idx, formulaTextElement );
  }
}
