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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabRowGroupType;
import org.pentaho.reporting.engine.classic.core.sorting.SortConstraint;

import java.util.Collections;
import java.util.List;

/**
 * Can have either a row- or a column body.
 *
 * @author Thomas Morgner
 */
public class CrosstabRowGroup extends Group {
  private CrosstabTitleHeader titleHeader;
  private CrosstabHeader header;
  private CrosstabSummaryHeader summaryHeader;

  public CrosstabRowGroup() {
    init();
  }

  private void init() {
    setElementType( new CrosstabRowGroupType() );
    titleHeader = new CrosstabTitleHeader();
    header = new CrosstabHeader();
    summaryHeader = new CrosstabSummaryHeader();

    registerAsChild( titleHeader );
    registerAsChild( header );
    registerAsChild( summaryHeader );
  }

  public CrosstabRowGroup( final GroupBody body ) {
    super( body );
    validateBody( body );
    init();
  }

  public CrosstabRowGroup( final CrosstabCellBody body ) {
    super( body );

    init();
  }

  public CrosstabRowGroup( final CrosstabColumnGroupBody body ) {
    super( body );

    init();
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

  public CrosstabHeader getHeader() {
    return header;
  }

  public void setHeader( final CrosstabHeader titleFooter ) {
    if ( titleFooter == null ) {
      throw new NullPointerException( "titleFooter must not be null" );
    }
    validateLooping( titleFooter );
    if ( unregisterParent( titleFooter ) ) {
      return;
    }

    final Element element = this.header;
    this.header.setParent( null );
    this.header = titleFooter;
    this.header.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.header );
  }

  public CrosstabSummaryHeader getSummaryHeader() {
    return summaryHeader;
  }

  public void setSummaryHeader( final CrosstabSummaryHeader summaryHeader ) {
    if ( summaryHeader == null ) {
      throw new NullPointerException( "summaryHeader must not be null" );
    }
    validateLooping( summaryHeader );
    if ( unregisterParent( summaryHeader ) ) {
      return;
    }

    final Element element = this.summaryHeader;
    this.summaryHeader.setParent( null );
    this.summaryHeader = summaryHeader;
    this.summaryHeader.setParent( this );

    notifyNodeChildRemoved( element );
    notifyNodeChildAdded( this.summaryHeader );
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
    return new CrosstabColumnGroupBody();
  }

  public void setBody( final GroupBody body ) {
    validateBody( body );
    super.setBody( body );
  }

  private void validateBody( final GroupBody body ) {
    if ( body instanceof CrosstabRowGroupBody == false && body instanceof CrosstabColumnGroupBody == false ) {
      throw new IllegalArgumentException( body + " must be either row-group- or column-group-body" );
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

  public CrosstabRowGroup clone() {
    final CrosstabRowGroup element = (CrosstabRowGroup) super.clone();
    element.summaryHeader = (CrosstabSummaryHeader) summaryHeader.clone();
    element.titleHeader = (CrosstabTitleHeader) titleHeader.clone();
    element.header = (CrosstabHeader) header.clone();
    element.registerAsChild( element.titleHeader );
    element.registerAsChild( element.header );
    element.registerAsChild( element.summaryHeader );
    return element;
  }

  public CrosstabRowGroup derive( final boolean preserveElementInstanceIds ) {
    final CrosstabRowGroup element = (CrosstabRowGroup) super.derive( preserveElementInstanceIds );
    element.summaryHeader = (CrosstabSummaryHeader) summaryHeader.derive( preserveElementInstanceIds );
    element.titleHeader = (CrosstabTitleHeader) titleHeader.derive( preserveElementInstanceIds );
    element.header = (CrosstabHeader) header.derive( preserveElementInstanceIds );
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
    } else if ( summaryHeader == element ) {
      this.summaryHeader.setParent( null );
      this.summaryHeader = new CrosstabSummaryHeader();
      this.summaryHeader.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.summaryHeader );
    } else if ( header == element ) {
      this.header.setParent( null );
      this.header = new CrosstabHeader();
      this.header.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.header );
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
