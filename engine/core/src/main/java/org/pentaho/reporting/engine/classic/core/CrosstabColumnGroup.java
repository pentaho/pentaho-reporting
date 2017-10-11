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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabColumnGroupType;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;

import java.util.Collections;
import java.util.List;

/**
 * Can have either a column body or a details body.
 *
 * @author Thomas Morgner
 */
public class CrosstabColumnGroup extends Group {
  private CrosstabTitleHeader titleHeader;
  private CrosstabHeader header;
  private CrosstabSummaryHeader summaryHeader;

  public CrosstabColumnGroup() {
    init();
  }

  public CrosstabColumnGroup( final GroupBody body ) {
    super( body );
    validateBody( body );
    init();
  }

  public CrosstabColumnGroup( final CrosstabCellBody body ) {
    super( body );

    init();
  }

  public CrosstabColumnGroup( final CrosstabColumnGroupBody body ) {
    super( body );

    init();
  }

  private void init() {
    setElementType( new CrosstabColumnGroupType() );
    titleHeader = new CrosstabTitleHeader();
    summaryHeader = new CrosstabSummaryHeader();
    header = new CrosstabHeader();

    registerAsChild( titleHeader );
    registerAsChild( summaryHeader );
    registerAsChild( header );
  }

  public CrosstabTitleHeader getTitleHeader() {
    return titleHeader;
  }

  public void setTitleHeader( final CrosstabTitleHeader titleHeader ) {
    if ( titleHeader == null ) {
      throw new NullPointerException( "titleHeader must not be null" );
    }
    validateLooping( titleHeader );
    if ( unregisterParent( titleHeader ) ) {
      return;
    }

    final Element element = this.titleHeader;
    this.titleHeader.setParent( null );
    this.titleHeader = titleHeader;
    this.titleHeader.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.titleHeader );
  }

  public CrosstabSummaryHeader getSummaryHeader() {
    return summaryHeader;
  }

  public void setSummaryHeader( final CrosstabSummaryHeader header ) {
    if ( header == null ) {
      throw new NullPointerException( "titleFooter must not be null" );
    }
    validateLooping( header );
    if ( unregisterParent( header ) ) {
      return;
    }

    final Element element = this.summaryHeader;
    this.summaryHeader.setParent( null );
    this.summaryHeader = header;
    this.summaryHeader.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.summaryHeader );
  }

  public CrosstabHeader getHeader() {
    return header;
  }

  public void setHeader( final CrosstabHeader header ) {
    if ( header == null ) {
      throw new NullPointerException( "titleFooter must not be null" );
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
    return new CrosstabCellBody();
  }

  public void setBody( final GroupBody body ) {
    validateBody( body );
    super.setBody( body );
  }

  private void validateBody( final GroupBody body ) {
    if ( body instanceof CrosstabCellBody == false && body instanceof CrosstabColumnGroupBody == false ) {
      throw new IllegalArgumentException();
    }
  }

  public boolean isGroupChange( final DataRow dataRow ) {
    final String field = getField();
    if ( field == null ) {
      return false;
    }
    if ( dataRow.isChanged( field ) ) {
      return true;
    }
    return false;
  }

  public CrosstabColumnGroup clone() {
    final CrosstabColumnGroup element = (CrosstabColumnGroup) super.clone();
    element.titleHeader = (CrosstabTitleHeader) titleHeader.clone();
    element.header = (CrosstabHeader) header.clone();
    element.summaryHeader = (CrosstabSummaryHeader) summaryHeader.clone();
    element.registerAsChild( element.titleHeader );
    element.registerAsChild( element.header );
    element.registerAsChild( element.summaryHeader );
    return element;
  }

  public CrosstabColumnGroup derive( final boolean preserveElementInstanceIds ) {
    final CrosstabColumnGroup element = (CrosstabColumnGroup) super.derive( preserveElementInstanceIds );
    element.titleHeader = (CrosstabTitleHeader) titleHeader.derive( preserveElementInstanceIds );
    element.header = (CrosstabHeader) header.derive( preserveElementInstanceIds );
    element.summaryHeader = (CrosstabSummaryHeader) summaryHeader.derive( preserveElementInstanceIds );
    element.registerAsChild( element.titleHeader );
    element.registerAsChild( element.header );
    element.registerAsChild( element.summaryHeader );
    return element;
  }

  public int getElementCount() {
    return 4;
  }

  public Element getElement( final int index ) {
    switch ( index ) {
      case 0:
        return getTitleHeader();
      case 1:
        return getHeader();
      case 2:
        return getSummaryHeader();
      case 3:
        return getBody();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public void setElementAt( final int index, final Element element ) {
    switch ( index ) {
      case 0:
        setTitleHeader( (CrosstabTitleHeader) element );
        break;
      case 1:
        setHeader( (CrosstabHeader) element );
        break;
      case 2:
        setSummaryHeader( (CrosstabSummaryHeader) element );
        break;
      case 3:
        setBody( (GroupBody) element );
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( titleHeader == element ) {
      this.titleHeader.setParent( null );
      this.titleHeader = new CrosstabTitleHeader();
      this.titleHeader.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.titleHeader );
    } else if ( header == element ) {
      this.header.setParent( null );
      this.header = new CrosstabHeader();
      this.header.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.header );
    } else if ( summaryHeader == element ) {
      this.summaryHeader.setParent( null );
      this.summaryHeader = new CrosstabSummaryHeader();
      this.summaryHeader.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.summaryHeader );
    } else {
      super.removeElement( element );
    }
  }

  public boolean isPrintSummary() {
    final Object attribute = getAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PRINT_SUMMARY );
    if ( attribute == null ) {
      return true;
    }

    return Boolean.TRUE.equals( attribute );
  }

  public void setPrintSummary( final boolean printSummary ) {
    setAttribute( AttributeNames.Crosstab.NAMESPACE, AttributeNames.Crosstab.PRINT_SUMMARY, printSummary );
  }

  public List<SortConstraint> getSortingConstraint() {
    return mapFields( Collections.singletonList( getField() ) );
  }
}
