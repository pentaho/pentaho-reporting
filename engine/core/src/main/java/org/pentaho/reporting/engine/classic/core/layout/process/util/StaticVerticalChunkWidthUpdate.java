/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.MinorAxisLayoutStepUtil;

public class StaticVerticalChunkWidthUpdate extends StaticChunkWidthUpdate {
  private RenderBox box;
  private long chunkWidth;
  private long minimumBorderBoxWidth;
  private StackedObjectPool<StaticVerticalChunkWidthUpdate> pool;

  StaticVerticalChunkWidthUpdate( final StackedObjectPool<StaticVerticalChunkWidthUpdate> pool ) {
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

  public StaticChunkWidthUpdate pop() {
    box = null;
    chunkWidth = 0;
    minimumBorderBoxWidth = 0;
    pool.free( this );
    return super.pop();
  }
}
