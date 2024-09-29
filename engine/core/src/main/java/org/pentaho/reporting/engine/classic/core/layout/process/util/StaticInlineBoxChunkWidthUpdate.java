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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class StaticInlineBoxChunkWidthUpdate extends StaticChunkWidthUpdate {
  private RenderBox box;
  private long chunkWidth;
  private StackedObjectPool<StaticInlineBoxChunkWidthUpdate> pool;

  StaticInlineBoxChunkWidthUpdate( final StackedObjectPool<StaticInlineBoxChunkWidthUpdate> pool ) {
    if ( pool == null ) {
      throw new NullPointerException();
    }
    this.pool = pool;
  }

  public StaticInlineBoxChunkWidthUpdate() {
  }

  void reuse( final StaticChunkWidthUpdate parent, final RenderBox box ) {
    reuse( parent );
    this.box = box;
    this.chunkWidth = 0;
  }

  public void update( final long minChunkWidth ) {
    if ( chunkWidth < minChunkWidth ) {
      chunkWidth = minChunkWidth;
    }
  }

  public void finish() {
    final long chunkWidthAndInsets = chunkWidth + box.getInsets();
    if ( box.getMinimumChunkWidth() < chunkWidthAndInsets ) {
      box.setMinimumChunkWidth( chunkWidthAndInsets );
    }
  }

  public boolean isInline() {
    return true;
  }

  public StaticChunkWidthUpdate pop() {
    box = null;
    chunkWidth = 0;
    pool.free( this );
    return super.pop();
  }
}
