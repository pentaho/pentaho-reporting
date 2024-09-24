/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
