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

import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.MinorAxisLayoutStepUtil;

public class StaticParagraphChunkWidthUpdate extends StaticChunkWidthUpdate {
  private ParagraphRenderBox box;
  private long chunkWidth;
  private long minimumBorderBoxWidth;
  private StackedObjectPool<StaticParagraphChunkWidthUpdate> pool;

  StaticParagraphChunkWidthUpdate( final StackedObjectPool<StaticParagraphChunkWidthUpdate> pool ) {
    if ( pool == null ) {
      throw new NullPointerException();
    }
    this.pool = pool;
  }

  void reuse( final StaticChunkWidthUpdate parent, final ParagraphRenderBox box ) {
    reuse( parent );
    this.box = box;
    this.chunkWidth = 0;
    this.minimumBorderBoxWidth = MinorAxisLayoutStepUtil.resolveNodeWidthForMinChunkCalculation( box );
  }

  public void update( final long minChunkWidth ) {
    if ( chunkWidth < minChunkWidth ) {
      chunkWidth = minChunkWidth;
    }
  }

  public void finish() {
    final long chunkWidthAndInsets = Math.max( minimumBorderBoxWidth, chunkWidth + box.getInsets() );
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
    minimumBorderBoxWidth = 0;
    pool.free( this );
    return super.pop();
  }
}
