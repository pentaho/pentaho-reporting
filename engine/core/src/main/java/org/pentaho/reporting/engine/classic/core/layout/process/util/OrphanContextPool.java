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

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;

public class OrphanContextPool {
  private static class BlockContextPool extends StackedObjectPool<OrphanBlockContext> {
    private BlockContextPool() {
    }

    protected OrphanBlockContext create() {
      return new OrphanBlockContext();
    }
  }

  private static class CanvasContextPool extends StackedObjectPool<OrphanCanvasContext> {
    private CanvasContextPool() {
    }

    protected OrphanCanvasContext create() {
      return new OrphanCanvasContext();
    }
  }

  private CanvasContextPool canvasContextPool;
  private BlockContextPool blockContextPool;

  public OrphanContextPool() {
    canvasContextPool = new CanvasContextPool();
    blockContextPool = new BlockContextPool();
  }

  public OrphanContext create( final RenderBox box, final OrphanContext context ) {
    if ( ( box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
      final int orphans = properties.getOrphans();
      final OrphanBlockContext retval = blockContextPool.get();
      retval.init( blockContextPool, context, box, orphans );
      return retval;
    }

    final OrphanCanvasContext retval = canvasContextPool.get();
    retval.init( canvasContextPool, context );
    return retval;
  }

  public void free( final OrphanContext context ) {
    context.clearForPooledReuse();
  }
}
