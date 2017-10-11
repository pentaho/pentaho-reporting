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

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;

public abstract class FormulaElement implements Element {
  private Document document;
  private int endOffset;
  private int startOffset;
  private AttributeSet attributes;
  private FormulaRootElement parentElement;

  protected FormulaElement( final FormulaDocument document, final FormulaRootElement parentElement ) {
    this.document = document;
    this.parentElement = parentElement;
    this.attributes = new SimpleAttributeSet();
  }

  public void setEndOffset( final int endOffset ) {
    this.endOffset = endOffset;
  }

  public void setStartOffset( final int startOffset ) {
    this.startOffset = startOffset;
  }

  public abstract String getText();

  /**
   * Fetches the document associated with this element.
   *
   * @return the document
   */
  public Document getDocument() {
    return document;
  }

  /**
   * Fetches the parent element.  If the element is a root level element returns <code>null</code>.
   *
   * @return the parent element
   */
  public Element getParentElement() {
    return parentElement;
  }

  /**
   * Fetches the collection of attributes this element contains.
   *
   * @return the attributes for the element
   */
  public AttributeSet getAttributes() {
    return attributes;
  }

  /**
   * Fetches the offset from the beginning of the document that this element begins at.  If this element has children,
   * this will be the offset of the first child. As a document position, there is an implied forward bias.
   *
   * @return the starting offset >= 0 and < getEndOffset();
   * @see Document
   * @see AbstractDocument
   */
  public int getStartOffset() {
    return startOffset;
  }

  /**
   * Fetches the offset from the beginning of the document that this element ends at.  If this element has children,
   * this will be the end offset of the last child. As a document position, there is an implied backward bias.
   * <p/>
   * All the default <code>Document</code> implementations descend from <code>AbstractDocument</code>.
   * <code>AbstractDocument</code> models an implied break at the end of the document. As a result of this, it is
   * possible for this to return a value greater than the length of the document.
   *
   * @return the ending offset > getStartOffset() and <= getDocument().getLength() + 1
   * @see Document
   * @see AbstractDocument
   */
  public int getEndOffset() {
    return endOffset;
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
    return -1;
  }

  /**
   * Gets the number of child elements contained by this element. If this element is a leaf, a count of zero is
   * returned.
   *
   * @return the number of child elements >= 0
   */
  public int getElementCount() {
    return 0;
  }

  /**
   * Fetches the child element at the given index.
   *
   * @param index the specified index >= 0
   * @return the child element
   */
  public Element getElement( final int index ) {
    throw new IndexOutOfBoundsException();
  }

  /**
   * Is this element a leaf element? An element that <i>may</i> have children, even if it currently has no children,
   * would return <code>false</code>.
   *
   * @return true if a leaf element else false
   */
  public boolean isLeaf() {
    return true;
  }
}
