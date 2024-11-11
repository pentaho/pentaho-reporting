/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.MinorAxisLayoutStepUtil;

public class StaticHorizontalChunkWidthUpdate extends StaticChunkWidthUpdate {
  private RenderBox box;
  private long chunkWidth;
  private long minimumBorderBoxWidth;
  private StackedObjectPool<StaticHorizontalChunkWidthUpdate> pool;

  StaticHorizontalChunkWidthUpdate( final StackedObjectPool<StaticHorizontalChunkWidthUpdate> pool ) {
    if ( pool == null ) {
      throw new NullPointerException();
    }
    this.pool = pool;
  }

  void reuse( final StaticChunkWidthUpdate parent, final RenderBox box ) {
    reuse( parent );
    this.box = box;
    this.chunkWidth = 0;
    this.minimumBorderBoxWidth = MinorAxisLayoutStepUtil.resolveNodeWidthForMinChunkCalculation( box );
  }

  public void update( final long minChunkWidth ) {
    chunkWidth += minChunkWidth;
  }

  public void finish() {
    final long chunkWidthAndInsets = Math.max( minimumBorderBoxWidth, chunkWidth + box.getInsets() );
    if ( box.getMinimumChunkWidth() < chunkWidthAndInsets ) {
      box.setMinimumChunkWidth( chunkWidthAndInsets );
    }
  }

  public StaticChunkWidthUpdate pop() {
    this.box = null;
    this.chunkWidth = 0;
    this.minimumBorderBoxWidth = 0;
    this.pool.free( this );
    return super.pop();
  }
}
