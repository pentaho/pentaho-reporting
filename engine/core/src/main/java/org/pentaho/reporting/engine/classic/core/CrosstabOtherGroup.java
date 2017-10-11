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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupType;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;

import java.util.Collections;
import java.util.List;

/**
 * Can have either a row- or a column body.
 *
 * @author Thomas Morgner
 */
public class CrosstabOtherGroup extends Group {
  private GroupHeader header;
  private GroupFooter footer;

  public CrosstabOtherGroup() {
    init();
  }

  public CrosstabOtherGroup( final GroupBody body ) {
    super( body );
    validateBody( body );
    init();
  }

  public CrosstabOtherGroup( final CrosstabCellBody body ) {
    super( body );

    init();
  }

  public CrosstabOtherGroup( final CrosstabColumnGroupBody body ) {
    super( body );

    init();
  }

  private void init() {
    setElementType( new CrosstabOtherGroupType() );

    this.footer = new GroupFooter();
    this.header = new GroupHeader();

    registerAsChild( footer );
    registerAsChild( header );
  }

  /**
   * Returns the group header.
   * <P>
   * The group header is a report band that contains elements that should be printed at the start of a group.
   *
   * @return the group header.
   */
  public GroupHeader getHeader() {
    return header;
  }

  /**
   * Sets the header for the group.
   *
   * @param header
   *          the header (null not permitted).
   * @throws NullPointerException
   *           if the given header is null
   */
  public void setHeader( final GroupHeader header ) {
    if ( header == null ) {
      throw new NullPointerException( "Header must not be null" );
    }
    validateLooping( header );
    if ( unregisterParent( header ) ) {
      return;
    }

    final Element element = this.header;
    this.header.setParent( null );
    this.header = header;
    this.header.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.header );
  }

  /**
   * Returns the group footer.
   *
   * @return the footer.
   */
  public GroupFooter getFooter() {
    return footer;
  }

  /**
   * Sets the footer for the group.
   *
   * @param footer
   *          the footer (null not permitted).
   * @throws NullPointerException
   *           if the given footer is null.
   */
  public void setFooter( final GroupFooter footer ) {
    if ( footer == null ) {
      throw new NullPointerException( "The footer must not be null" );
    }
    validateLooping( footer );
    if ( unregisterParent( footer ) ) {
      return;
    }

    final Element element = this.footer;
    this.footer.setParent( null );
    this.footer = footer;
    this.footer.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.footer );
  }

  /**
   * Clones this Element.
   *
   * @return a clone of this element.
   */
  public CrosstabOtherGroup clone() {
    final CrosstabOtherGroup g = (CrosstabOtherGroup) super.clone();
    g.footer = (GroupFooter) footer.clone();
    g.header = (GroupHeader) header.clone();

    g.registerAsChild( g.footer );
    g.registerAsChild( g.header );
    return g;
  }

  public CrosstabOtherGroup derive( final boolean preserveElementInstanceIds ) {
    final CrosstabOtherGroup g = (CrosstabOtherGroup) super.derive( preserveElementInstanceIds );
    g.footer = (GroupFooter) footer.derive( preserveElementInstanceIds );
    g.header = (GroupHeader) header.derive( preserveElementInstanceIds );

    g.registerAsChild( g.footer );
    g.registerAsChild( g.header );
    return g;
  }

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( footer == element ) {
      this.footer.setParent( null );
      this.footer = new GroupFooter();
      this.footer.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.footer );

    } else if ( header == element ) {
      this.header.setParent( null );
      this.header = new GroupHeader();
      this.header.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.header );
    } else {
      super.removeElement( element );
    }
    // Else: Ignore the request, none of my childs.
  }

  public Element getElement( final int index ) {
    switch ( index ) {
      case 0:
        return header;
      case 1:
        return getBody();
      case 2:
        return footer;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public int getElementCount() {
    return 3;
  }

  public void setElementAt( final int index, final Element element ) {
    switch ( index ) {
      case 0:
        setHeader( (GroupHeader) element );
        break;
      case 1:
        setBody( (GroupBody) element );
        break;
      case 2:
        setFooter( (GroupFooter) element );
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public String getField() {
    final Object o = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
    if ( o == null ) {
      return null;
    }
    return o.toString();
  }

  public void setField( final String field ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, field );
    notifyNodePropertiesChanged();
  }

  protected GroupBody createDefaultBody() {
    return new CrosstabRowGroupBody();
  }

  public void setBody( final GroupBody body ) {
    validateBody( body );
    super.setBody( body );
  }

  private void validateBody( final GroupBody body ) {
    if ( body instanceof CrosstabRowGroupBody == false && body instanceof CrosstabOtherGroupBody == false ) {
      throw new IllegalArgumentException();
    }
  }

  public boolean isGroupChange( final DataRow dataRow ) {
    final String fieldName = getField();
    if ( fieldName == null ) {
      return false;
    }
    if ( dataRow.isChanged( fieldName ) ) {
      return true;
    }
    return false;
  }

  public List<SortConstraint> getSortingConstraint() {
    return mapFields( Collections.singletonList( getField() ) );
  }
}
