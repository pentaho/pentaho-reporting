/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
