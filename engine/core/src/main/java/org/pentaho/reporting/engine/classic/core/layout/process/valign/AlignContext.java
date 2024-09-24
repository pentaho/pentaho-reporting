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
 * To position an element inside an box, we need the following data:
 * <p/>
 * (1) Offset. The distance between the parent's top-edge and the child's top edge.
 * <p/>
 * (2) Dominant baseline. The childs alignment point is defined by that one.
 * <p/>
 * (3) Ascent. The distance from the baseline to the top edge.
 * <p/>
 * (4) descent. The distance from the baseline to the bottom edge.
 *
 * @author Thomas Morgner
 */
public abstract class AlignContext {
  private int dominantBaseline;
  private RenderNode node;
  private AlignContext next;

  protected AlignContext( final RenderNode node ) {
    this.node = node;
  }

  public boolean isSimpleNode() {
    return false;
  }

  public RenderNode getNode() {
    return node;
  }

  public AlignContext getNext() {
    return next;
  }

  public void setNext( final AlignContext next ) {
    this.next = next;
  }

  public abstract void shift( final long delta );

  public abstract long getAfterEdge();

  public abstract long getBeforeEdge();

  public void setDominantBaseline( final int dominantBaseline ) {
    this.dominantBaseline = dominantBaseline;
  }

  public int getDominantBaseline() {
    return dominantBaseline;
  }

  public abstract long getBaselineDistance( int baseline );
}
