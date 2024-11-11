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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabRowGroupBodyType;

public class CrosstabRowGroupBody extends GroupBody {
  private CrosstabRowGroup group;

  public CrosstabRowGroupBody() {
    setElementType( new CrosstabRowGroupBodyType() );
    group = new CrosstabRowGroup();
    registerAsChild( group );
  }

  public CrosstabRowGroupBody( final CrosstabRowGroup group ) {
    this();
    setGroup( group );
  }

  public CrosstabRowGroup getGroup() {
    return group;
  }

  public void setGroup( final CrosstabRowGroup group ) {
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
      this.group = new CrosstabRowGroup();
      this.group.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.group );
    }
  }

  public void setElementAt( final int position, final Element element ) {
    if ( position != 0 ) {
      throw new IndexOutOfBoundsException();
    }
    setGroup( (CrosstabRowGroup) element );
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
  public CrosstabRowGroupBody clone() {
    final CrosstabRowGroupBody o = (CrosstabRowGroupBody) super.clone();
    o.group = group.clone();
    o.registerAsChild( o.group );
    return o;
  }

  /**
   * Clones the report.
   *
   * @return the clone.
   */
  public CrosstabRowGroupBody derive( final boolean preserveElementInstanceIds ) {
    final CrosstabRowGroupBody o = (CrosstabRowGroupBody) super.derive( preserveElementInstanceIds );
    o.group = group.derive( preserveElementInstanceIds );
    o.registerAsChild( o.group );
    return o;
  }

}
