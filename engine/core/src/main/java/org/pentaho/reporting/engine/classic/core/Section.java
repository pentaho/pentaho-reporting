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

import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ReportSectionDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A section is a small-scale band that allows to access the child elements but does not define how childs get added or
 * organized in the section. Defining a sensible order is left to the subclass-implementation.
 *
 * @author Thomas Morgner
 */
public abstract class Section extends Element implements Iterable<Element> {
  private class SectionIterator implements Iterator<Element> {
    private int pos;

    private SectionIterator() {
    }

    public boolean hasNext() {
      return pos < getElementCount();
    }

    public Element next() {
      if ( pos >= getElementCount() ) {
        throw new NoSuchElementException();
      }
      final Element e = getElement( pos );
      pos += 1;
      return e;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Default Constructor.
   */
  protected Section() {
  }

  protected Section( final InstanceID id ) {
    super( id );
  }

  /**
   * Creates the global stylesheet for this element type. The global stylesheet is an immutable stylesheet that provides
   * reasonable default values for some of the style keys.
   * <p/>
   * The global default stylesheet is always the last stylesheet that will be queried for values.
   *
   * @return the global stylesheet.
   */
  public ElementStyleSheet getDefaultStyleSheet() {
    return ReportSectionDefaultStyleSheet.getSectionDefault();
  }

  /**
   * Returns the element stored add the given index.
   *
   * @param index
   *          the element position within this section
   * @return the element
   * @throws IndexOutOfBoundsException
   *           if the index is invalid.
   */
  public abstract Element getElement( int index );

  /**
   * Returns the number of elements in this section.
   *
   * @return the number of elements of this section.
   */
  public abstract int getElementCount();

  /**
   * Unregisters the given element from its parent. If the element is already a direct child of this section, this
   * operation does nothing and returns 'true' to indicate that all the work is already done.
   * <p/>
   * This is a helper function and not meant to be used by the grand public.
   *
   * @param element
   *          the element to be unregistered from its current parent.
   * @return true, if the element is a child of this section, false otherwise.
   */
  protected boolean unregisterParent( final Element element ) {
    // remove the element from its old parent ..
    // this is the default AWT behaviour when adding Components to Container
    final Section parentSection = element.getParentSection();
    if ( parentSection != null ) {
      if ( parentSection == this ) {
        // already a child, wont add twice ...
        return true;
      }

      parentSection.removeElement( element );
    }
    return false;
  }

  /**
   * Checks whether the element given is a parent of this section. Adding that element to the section would cause
   * infinite loops later, so we prevent it early in the game.
   *
   * @param element
   *          the element to be checked for loops.
   */
  protected void validateLooping( final Element element ) {
    // check for component loops ...
    if ( element instanceof Section ) {
      Section band = this;
      while ( band != null ) {
        if ( band == element ) {
          throw new IllegalArgumentException( "adding container's parent to itself" );
        }
        band = band.getParentSection();
      }
    }
  }

  public abstract void setElementAt( final int position, final Element element );

  /**
   * Removes an element from the section.
   *
   * @param element
   *          the element to be section.
   * @throws NullPointerException
   *           if the given element is null.
   */
  protected abstract void removeElement( final Element element );

  protected void unregisterAsChild( final Element element ) {
    element.setParent( null );
  }

  protected void registerAsChild( final Element element ) {
    element.setParent( this );
  }

  public Iterator<Element> iterator() {
    return new SectionIterator();
  }

  public Section derive( final boolean preserveElementInstanceIds ) {
    return (Section) super.derive( preserveElementInstanceIds );
  }

  public Section clone() {
    return (Section) super.clone();
  }
}
