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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupBodyType;

public class CrosstabOtherGroupBody extends GroupBody {
  private CrosstabOtherGroup group;

  public CrosstabOtherGroupBody() {
    setElementType( new CrosstabOtherGroupBodyType() );
    group = new CrosstabOtherGroup();
    registerAsChild( group );
  }

  public CrosstabOtherGroupBody( final CrosstabOtherGroup group ) {
    this();
    setGroup( group );
  }

  public CrosstabOtherGroup getGroup() {
    return group;
  }

  public void setGroup( final CrosstabOtherGroup group ) {
    if ( group == null ) {
      throw new NullPointerException( "The group must not be null" );
    }
    validateLooping( group );
    if ( unregisterParent( group ) ) {
      return;
    }
    final Group oldGroup = this.group;
    this.group.setParent( null );
    this.group = group;
    this.group.setParent( this );

    notifyNodeChildRemoved( oldGroup );
    notifyNodeChildAdded( this.group );
  }

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( element == group ) {
      this.group.setParent( null );
      this.group = new CrosstabOtherGroup();
      this.group.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.group );
    }
  }

  public void setElementAt( final int position, final Element element ) {
    if ( position != 0 ) {
      throw new IndexOutOfBoundsException();
    }
    setGroup( (CrosstabOtherGroup) element );
  }

  public int getElementCount() {
    return 1;
  }

  public Element getElement( final int index ) {
    if ( index == 0 ) {
      return group;
    }
    throw new IndexOutOfBoundsException();
  }

  /**
   * Clones this Element, the datasource and the private stylesheet of this Element. The clone does no longer have a
   * parent, as the old parent would not recognize that new object anymore.
   *
   * @return a clone of this Element.
   */
  public CrosstabOtherGroupBody clone() {
    final CrosstabOtherGroupBody o = (CrosstabOtherGroupBody) super.clone();
    o.group = (CrosstabOtherGroup) group.clone();
    o.registerAsChild( o.group );
    return o;
  }

  /**
   * Clones the report.
   *
   * @return the clone.
   */
  public CrosstabOtherGroupBody derive( final boolean preserveElementInstanceIds ) {
    final CrosstabOtherGroupBody o = (CrosstabOtherGroupBody) super.derive( preserveElementInstanceIds );
    o.group = (CrosstabOtherGroup) group.derive( preserveElementInstanceIds );
    o.registerAsChild( o.group );
    return o;
  }

}
