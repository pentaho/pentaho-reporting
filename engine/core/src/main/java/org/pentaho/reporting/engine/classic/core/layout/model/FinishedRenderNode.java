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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.layout.model.context.NodeLayoutProperties;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

/**
 * A box replacement. It has a predefined width and height and does not change those. It is a placeholder for all
 * already printed content.
 * <p/>
 * If you see this node inside an inline box, you can be sure you've shot yourself in the foot.
 *
 * @author Thomas Morgner
 */
public final class FinishedRenderNode extends RenderNode {
  private long layoutedX;
  private long layoutedY;
  private long layoutedWidth;
  private long layoutedHeight;
  private long marginsTop;
  private long marginsBottom;
  private boolean breakAfter;
  private ReportStateKey stateKey;
  private final int orphanLeafCount;
  private final int widowLeafCount;

  public FinishedRenderNode( final long layoutedX, final long layoutedY, final long layoutedWidth,
      final long layoutedHeight, final long marginsTop, final long marginsBottom, final boolean breakAfter,
      final int orphanLeafCount, final int widowLeafCount ) {
    this( layoutedX, layoutedY, layoutedWidth, layoutedHeight, marginsTop, marginsBottom, breakAfter, orphanLeafCount,
        widowLeafCount, null );
  }

  public FinishedRenderNode( final long layoutedX, final long layoutedY, final long layoutedWidth,
      final long layoutedHeight, final long marginsTop, final long marginsBottom, final boolean breakAfter,
      final int orphanLeafCount, final int widowLeafCount, final ReportStateKey stateKey ) {
    super( NodeLayoutProperties.GENERIC_PROPERTIES );
    if ( layoutedWidth < 0 ) {
      throw new IllegalStateException( "Layouted Width is less than zero: " + layoutedWidth );
    }
    if ( layoutedHeight < 0 ) {
      throw new IllegalStateException( "Layouted Height is less than zero: " + layoutedHeight );
    }

    this.stateKey = stateKey;
    this.breakAfter = breakAfter;
    this.layoutedX = layoutedX;
    this.layoutedY = layoutedY;
    this.layoutedWidth = layoutedWidth;
    this.layoutedWidth = layoutedWidth;
    this.layoutedHeight = layoutedHeight;
    this.marginsBottom = marginsBottom;
    this.marginsTop = marginsTop;
    this.orphanLeafCount = orphanLeafCount;
    this.widowLeafCount = widowLeafCount;

    setFinishedPaginate( true );
    setFinishedTable( true );
    setMinimumChunkWidth( layoutedWidth );
    setMaximumBoxWidth( layoutedWidth );
    setX( layoutedX );
    setY( layoutedY );
    setWidth( layoutedWidth );
    setHeight( layoutedHeight );
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_NODE_FINISHEDNODE;
  }

  public boolean isBreakAfter() {
    return breakAfter;
  }

  public long getLayoutedWidth() {
    return layoutedWidth;
  }

  public long getLayoutedHeight() {
    return layoutedHeight;
  }

  public long getMarginsTop() {
    return marginsTop;
  }

  public long getMarginsBottom() {
    return marginsBottom;
  }

  /**
   * If that method returns true, the element will not be used for rendering. For the purpose of computing sizes or
   * performing the layouting (in the validate() step), this element will treated as if it is not there.
   * <p/>
   * If the element reports itself as non-empty, however, it will affect the margin computation.
   *
   * @return
   */
  public boolean isIgnorableForRendering() {
    // Finished rows affect the margins ..
    return false;
  }

  public ReportStateKey getStateKey() {
    return stateKey;
  }

  public int getOrphanLeafCount() {
    return orphanLeafCount;
  }

  public int getWidowLeafCount() {
    return widowLeafCount;
  }

  public boolean isOrphanLeaf() {
    return widowLeafCount > 0 || orphanLeafCount > 0;
  }

  public RenderBox.RestrictFinishClearOut getRestrictFinishedClearOut() {
    if ( isOrphanLeaf() ) {
      return RenderBox.RestrictFinishClearOut.LEAF;
    }
    return RenderBox.RestrictFinishClearOut.UNRESTRICTED;
  }

  public long getLayoutedY() {
    return layoutedY;
  }

}
