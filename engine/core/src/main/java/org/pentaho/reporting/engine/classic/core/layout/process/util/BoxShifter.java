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

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * By keeping the shifting in a separate class, we can optimize it later without having to touch the other code.
 * Remember: Recursive calls can be evil in complex documents..
 *
 * @author Thomas Morgner
 */
public final class BoxShifter {
  private BoxShifter() {
  }

  public static void shiftBox( final RenderNode box, final long amount ) {
    if ( amount == 0 ) {
      return;
    }
    if ( amount < 0 ) {
      throw new IllegalArgumentException( "Cannot shift upwards: " + amount );
    }

    box.shift( amount );
    if ( ( box.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      shiftBoxInternal( (RenderBox) box, amount );
    }
  }

  public static void shiftBoxUnchecked( final RenderNode box, final long amount ) {
    if ( amount == 0 ) {
      return;
    }

    box.shift( amount );
    if ( ( box.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      shiftBoxInternal( (RenderBox) box, amount );
    }
  }

  private static void shiftBoxInternal( final RenderBox box, final long amount ) {
    RenderNode node = box.getFirstChild();
    while ( node != null ) {
      node.shift( amount );
      if ( ( node.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
        shiftBoxInternal( (RenderBox) node, amount );
      }
      node = node.getNext();
    }
  }

  @Deprecated( )
  public static boolean extendHeight( final RenderBox parent, final RenderNode child, final long amountDelta ) {
    if ( amountDelta < 0 ) {
      throw new IllegalArgumentException( "Cannot shrink elements: " + parent + " + " + amountDelta );
    }
    if ( parent == null || amountDelta == 0 ) {
      return false;
    }
    parent.extendHeight( child, amountDelta );
    // todo: PRD-4606
    parent.markApplyStateDirty();
    // parent.resetCacheState(true);
    return true;
  }
}
