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

public class MinorAxisNodeContext {
  private MinorAxisNodeContext parent;
  private MinorAxisNodeContext blockContext;
  private long x;
  private long width;
  private boolean blockNode;
  private boolean horizontal;
  private boolean overflowX;
  private long x1;
  private long x2;

  private boolean blockLevelNode;

  private long maxChildX2;
  private MinorAxisNodeContextPool pool;

  protected MinorAxisNodeContext( final MinorAxisNodeContextPool pool ) {
    this.pool = pool;
  }

  protected void reuseParent( final MinorAxisNodeContext context ) {
    this.parent = context;
    this.maxChildX2 = 0;
    this.width = 0;
    this.x = 0;
    this.x1 = 0;
    this.x2 = 0;

    if ( context != null ) {
      if ( context.blockNode ) {
        this.blockContext = context;
      } else {
        this.blockContext = context.blockContext;
      }
    }
  }

  protected void reuse( final boolean horizontal, final boolean blockLevelNode, final boolean overflowX,
      final boolean blockNode ) {
    this.horizontal = horizontal;
    this.blockLevelNode = blockLevelNode;
    this.overflowX = overflowX;
    this.blockNode = blockNode;
  }

  /**
   * Defines the active area for the element. Note that it is absolutely legal to define elements that have a
   * content-area outside of the visible area (ie: sum of left and right insets is larger than the width).
   * <p/>
   * In that case, the element has a effective content-area width of zero. It still may generate content if the parent
   * element has been set to 'overflow-x: true'.
   *
   * @param x
   * @param left
   * @param right
   * @param width
   */
  public void setArea( final long x, final long left, final long right, final long width ) {
    this.x = x;
    this.width = width;
    this.x1 = x + left;
    this.x2 = Math.max( x1, x + width - right );
    if ( !horizontal ) {
      this.maxChildX2 = x2;
    }
  }

  public long getX1() {
    return x1;
  }

  public long getParentX1() {
    if ( parent == null ) {
      return 0;
    }
    return parent.getX1();
  }

  public long getX2() {
    return x2;
  }

  public long getMaxChildX2() {
    if ( horizontal ) {
      return Math.max( maxChildX2, x2 );
    }
    return maxChildX2;
  }

  public void updateX2( final long position ) {
    if ( maxChildX2 < position ) {
      maxChildX2 = position;
    }
  }

  public void updateParentX2( final long position ) {
    if ( parent == null ) {
      return;
    }

    if ( overflowX ) {
      // overflow means that child nodes will not expand the parent's content area. The nodes float elsewhere.
      return;
    }

    parent.updateX2( position );
  }

  public MinorAxisNodeContext pop() {
    final MinorAxisNodeContext retval = parent;
    parent = null;
    if ( pool != null ) {
      pool.free( this );
    }
    return retval;
  }

  public long getX() {
    return x;
  }

  public long getWidth() {
    return width;
  }

  public long getContentAreaWidth() {
    return x2 - x1;
  }

  public boolean isOverflowX() {
    return overflowX;
  }

  public long getResolvedPreferredSize() {
    if ( parent == null ) {
      return 0;
    }
    if ( blockLevelNode ) {
      return parent.getContentAreaWidth();
    }
    return getContentAreaWidth();
  }

  public long getBlockContextWidth() {
    if ( blockContext == null ) {
      return 0;
    }
    return blockContext.getContentAreaWidth();
  }

  public long getParentX2() {
    if ( parent == null ) {
      return 0;
    }

    return parent.getX() + parent.getWidth();
  }

}
