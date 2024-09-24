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

/**
 * A spacer reserves space for whitespaces found in the text. When encountered at the beginning or end of lines, it gets
 * removed.
 * <p/>
 * Spacers are always considered discardable, so when encountered alone, they will get pruned.
 *
 * @author Thomas Morgner
 */
public final class SpacerRenderNode extends RenderNode {
  private boolean empty;
  private boolean preserve;
  private int spaceCount;

  public SpacerRenderNode() {
    this( 0, 0, false, 0 );
  }

  public SpacerRenderNode( final long width, final long height, final boolean preserve, final int spaceCount ) {
    super( NodeLayoutProperties.GENERIC_PROPERTIES );
    this.preserve = preserve;
    this.spaceCount = spaceCount;
    setMaximumBoxWidth( width );
    setMinimumChunkWidth( 0 );
    empty = width == 0 && height == 0;
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_NODE_SPACER;
  }

  /**
   * Returns the number of space-characters that resulted in this spacer-node. This is a content-creator hint to make
   * sure that the table-exports can create the represented space more easily. A space-count of zero means, that the
   * value is not known. In that case a renderer should apply some font-metrics magic to compute a suitable space count
   * from the known style information.
   *
   * @return the space count.
   */
  public int getSpaceCount() {
    return spaceCount;
  }

  public boolean isEmpty() {
    return empty;
  }

  public boolean isDiscardable() {
    return preserve == false;
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
    return true;
  }

}
