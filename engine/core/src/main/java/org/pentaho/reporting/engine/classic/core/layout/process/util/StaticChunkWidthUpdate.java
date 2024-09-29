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


package org.pentaho.reporting.engine.classic.core.layout.process.util;

public abstract class StaticChunkWidthUpdate {
  private StaticChunkWidthUpdate parent;

  protected StaticChunkWidthUpdate() {
  }

  protected void reuse( final StaticChunkWidthUpdate parent ) {
    this.parent = parent;
  }

  public abstract void update( long minChunkWidth );

  public void finish() {

  }

  public boolean isInline() {
    return false;
  }

  public StaticChunkWidthUpdate pop() {
    final StaticChunkWidthUpdate retval = parent;
    parent = null;
    return retval;
  }
}
