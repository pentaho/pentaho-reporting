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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;

public class GroupDataBody extends GroupBody {
  private DetailsHeader detailsHeader;
  private NoDataBand noDataBand;
  private ItemBand itemBand;
  private DetailsFooter detailsFooter;

  public GroupDataBody() {
    setElementType( new GroupDataBodyType() );
    this.noDataBand = new NoDataBand();
    this.itemBand = new ItemBand();
    this.detailsHeader = new DetailsHeader();
    this.detailsFooter = new DetailsFooter();
    registerAsChild( noDataBand );
    registerAsChild( itemBand );
    registerAsChild( detailsHeader );
    registerAsChild( detailsFooter );
  }

  public Group getGroup() {
    return null;
  }

  public NoDataBand getNoDataBand() {
    return noDataBand;
  }

  public void setNoDataBand( final NoDataBand noDataBand ) {
    if ( noDataBand == null ) {
      throw new NullPointerException( "The noDataBand must not be null" );
    }
    validateLooping( noDataBand );
    if ( unregisterParent( noDataBand ) ) {
      return;
    }
    final NoDataBand oldElement = this.noDataBand;
    this.noDataBand.setParent( null );
    this.noDataBand = noDataBand;
    this.noDataBand.setParent( this );

    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( this.noDataBand );
  }

  public ItemBand getItemBand() {
    return itemBand;
  }

  public void setItemBand( final ItemBand itemBand ) {
    if ( itemBand == null ) {
      throw new NullPointerException( "The itemBand must not be null" );
    }
    validateLooping( itemBand );
    if ( unregisterParent( itemBand ) ) {
      return;
    }
    final ItemBand oldElement = this.itemBand;
    this.itemBand.setParent( null );
    this.itemBand = itemBand;
    this.itemBand.setParent( this );

    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( this.itemBand );
  }

  public DetailsHeader getDetailsHeader() {
    return detailsHeader;
  }

  public void setDetailsHeader( final DetailsHeader detailsHeader ) {
    if ( detailsHeader == null ) {
      throw new NullPointerException( "The detailsHeader must not be null" );
    }
    validateLooping( detailsHeader );
    if ( unregisterParent( detailsHeader ) ) {
      return;
    }
    final DetailsHeader oldElement = this.detailsHeader;
    this.detailsHeader.setParent( null );
    this.detailsHeader = detailsHeader;
    this.detailsHeader.setParent( this );

    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( this.detailsHeader );
  }

  public DetailsFooter getDetailsFooter() {
    return detailsFooter;
  }

  public void setDetailsFooter( final DetailsFooter detailsFooter ) {
    if ( detailsFooter == null ) {
      throw new NullPointerException( "The detailsFooter must not be null" );
    }
    validateLooping( detailsFooter );
    if ( unregisterParent( detailsFooter ) ) {
      return;
    }

    final DetailsFooter oldElement = this.detailsFooter;
    this.detailsFooter.setParent( null );
    this.detailsFooter = detailsFooter;
    this.detailsFooter.setParent( this );

    notifyNodeChildRemoved( oldElement );
    notifyNodeChildAdded( this.detailsFooter );
  }

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( element == itemBand ) {
      this.itemBand.setParent( null );
      this.itemBand = new ItemBand();
      this.itemBand.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.itemBand );
    } else if ( element == noDataBand ) {
      this.noDataBand.setParent( null );
      this.noDataBand = new NoDataBand();
      this.noDataBand.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.noDataBand );
    } else if ( element == detailsHeader ) {
      this.detailsHeader.setParent( null );
      this.detailsHeader = new DetailsHeader();
      this.detailsHeader.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.detailsHeader );
    } else if ( element == detailsFooter ) {
      this.detailsFooter.setParent( null );
      this.detailsFooter = new DetailsFooter();
      this.detailsFooter.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.detailsFooter );
    }
  }

  public int getElementCount() {
    return 4;
  }

  public Element getElement( final int index ) {
    switch ( index ) {
      case 0:
        return detailsHeader;
      case 1:
        return itemBand;
      case 2:
        return noDataBand;
      case 3:
        return detailsFooter;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public void setElementAt( final int position, final Element element ) {
    switch ( position ) {
      case 0:
        setDetailsHeader( (DetailsHeader) element );
        break;
      case 1:
        setItemBand( (ItemBand) element );
        break;
      case 2:
        setNoDataBand( (NoDataBand) element );
        break;
      case 3:
        setDetailsFooter( (DetailsFooter) element );
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  /**
   * Clones this Element, the datasource and the private stylesheet of this Element. The clone does no longer have a
   * parent, as the old parent would not recognize that new object anymore.
   *
   * @return a clone of this Element.
   */
  public GroupDataBody clone() {
    final GroupDataBody dataBody = (GroupDataBody) super.clone();
    dataBody.itemBand = (ItemBand) itemBand.clone();
    dataBody.noDataBand = (NoDataBand) noDataBand.clone();
    dataBody.detailsHeader = (DetailsHeader) detailsHeader.clone();
    dataBody.detailsFooter = (DetailsFooter) detailsFooter.clone();

    dataBody.registerAsChild( dataBody.itemBand );
    dataBody.registerAsChild( dataBody.noDataBand );
    dataBody.registerAsChild( dataBody.detailsHeader );
    dataBody.registerAsChild( dataBody.detailsFooter );
    return dataBody;
  }

  public GroupDataBody derive( final boolean preserveElementInstanceIds ) {
    final GroupDataBody dataBody = (GroupDataBody) super.derive( preserveElementInstanceIds );
    dataBody.itemBand = (ItemBand) itemBand.derive( preserveElementInstanceIds );
    dataBody.noDataBand = (NoDataBand) noDataBand.derive( preserveElementInstanceIds );
    dataBody.detailsHeader = (DetailsHeader) detailsHeader.derive( preserveElementInstanceIds );
    dataBody.detailsFooter = (DetailsFooter) detailsFooter.derive( preserveElementInstanceIds );

    dataBody.registerAsChild( dataBody.itemBand );
    dataBody.registerAsChild( dataBody.noDataBand );
    dataBody.registerAsChild( dataBody.detailsHeader );
    dataBody.registerAsChild( dataBody.detailsFooter );
    return dataBody;
  }
}
