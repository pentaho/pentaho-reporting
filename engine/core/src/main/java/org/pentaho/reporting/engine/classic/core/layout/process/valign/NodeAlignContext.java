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


package org.pentaho.reporting.engine.classic.core.layout.process.valign;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * A generic align context for images and other nodes. (Renderable-Content should have been aligned by the parent.
 *
 * @author Thomas Morgner
 */
public final class NodeAlignContext extends AlignContext {
  private long shift;

  public NodeAlignContext( final RenderNode node ) {
    super( node );
  }

  public boolean isSimpleNode() {
    return true;
  }

  public long getBaselineDistance( final int baseline ) {
    return 0;
  }

  public void shift( final long delta ) {
    this.shift += delta;
  }

  public long getAfterEdge() {
    return shift;
  }

  public long getBeforeEdge() {
    return shift;
  }
}
