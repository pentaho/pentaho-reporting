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

import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class StaticChunkWidthUpdatePool {
  private static class HorizontalPool extends StackedObjectPool<StaticHorizontalChunkWidthUpdate> {
    private HorizontalPool() {
    }

    public StaticHorizontalChunkWidthUpdate create() {
      return new StaticHorizontalChunkWidthUpdate( this );
    }

    public StaticHorizontalChunkWidthUpdate get( final StaticChunkWidthUpdate parent, final RenderBox box ) {
      final StaticHorizontalChunkWidthUpdate chunkWidthUpdate = super.get();
      chunkWidthUpdate.reuse( parent, box );
      return chunkWidthUpdate;
    }
  }

  private static class VerticalPool extends StackedObjectPool<StaticVerticalChunkWidthUpdate> {
    private VerticalPool() {
    }

    public StaticVerticalChunkWidthUpdate create() {
      return new StaticVerticalChunkWidthUpdate( this );
    }

    public StaticVerticalChunkWidthUpdate get( final StaticChunkWidthUpdate parent, final RenderBox box ) {
      final StaticVerticalChunkWidthUpdate chunkWidthUpdate = super.get();
      chunkWidthUpdate.reuse( parent, box );
      return chunkWidthUpdate;
    }
  }

  private static class InlinePool extends StackedObjectPool<StaticInlineBoxChunkWidthUpdate> {
    private InlinePool() {
    }

    public StaticInlineBoxChunkWidthUpdate create() {
      return new StaticInlineBoxChunkWidthUpdate( this );
    }

    public StaticInlineBoxChunkWidthUpdate get( final StaticChunkWidthUpdate parent, final RenderBox box ) {
      final StaticInlineBoxChunkWidthUpdate chunkWidthUpdate = super.get();
      chunkWidthUpdate.reuse( parent, box );
      return chunkWidthUpdate;
    }
  }

  private static class ParagraphPool extends StackedObjectPool<StaticParagraphChunkWidthUpdate> {
    private ParagraphPool() {
    }

    public StaticParagraphChunkWidthUpdate create() {
      return new StaticParagraphChunkWidthUpdate( this );
    }

    public StaticParagraphChunkWidthUpdate get( final StaticChunkWidthUpdate parent, final ParagraphRenderBox box ) {
      final StaticParagraphChunkWidthUpdate chunkWidthUpdate = super.get();
      chunkWidthUpdate.reuse( parent, box );
      return chunkWidthUpdate;
    }
  }

  private HorizontalPool horizontalPool;
  private VerticalPool verticalPool;
  private InlinePool inlinePool;
  private ParagraphPool paragraphPool;

  public StaticChunkWidthUpdatePool() {
    horizontalPool = new HorizontalPool();
    verticalPool = new VerticalPool();
    inlinePool = new InlinePool();
    paragraphPool = new ParagraphPool();
  }

  public StaticChunkWidthUpdate createHorizontal( final StaticChunkWidthUpdate parent, final RenderBox box ) {
    return horizontalPool.get( parent, box );
  }

  public StaticChunkWidthUpdate createVertical( final StaticChunkWidthUpdate parent, final RenderBox box ) {
    return verticalPool.get( parent, box );
  }

  public StaticChunkWidthUpdate createInline( final StaticChunkWidthUpdate parent, final RenderBox box ) {
    return inlinePool.get( parent, box );
  }

  public StaticChunkWidthUpdate createParagraph( final StaticChunkWidthUpdate parent, final ParagraphRenderBox box ) {
    return paragraphPool.get( parent, box );
  }
}
