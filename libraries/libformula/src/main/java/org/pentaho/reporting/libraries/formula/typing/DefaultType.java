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


package org.pentaho.reporting.libraries.formula.typing;

public abstract class DefaultType implements Type {
  private int flags;
  private boolean locked;
  private static final long serialVersionUID = -8206983276033867416L;

  protected DefaultType() {
  }

  public boolean isLocked() {
    return locked;
  }

  public void lock() {
    this.locked = true;
  }

  public void addFlag( final int name ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    flags |= name;
  }

  public boolean isFlagSet( final int name ) {
    return ( flags & name ) == name;
  }
}
