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
public final class CacheBoxShifter {
  private CacheBoxShifter() {
  }

  public static void shiftBox( final RenderNode box, final long amount ) {
    if ( amount == 0 ) {
      return;
    }
    if ( amount < 0 ) {
      throw new IllegalArgumentException( "Cannot shift upwards: " + amount );
    }

    box.shiftCached( amount );
    if ( ( box.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      CacheBoxShifter.shiftBoxInternal( (RenderBox) box, amount );
    }
  }

  public static void shiftBoxUnchecked( final RenderNode box, final long amount ) {
    if ( amount == 0 ) {
      return;
    }

    box.shiftCached( amount );
    if ( ( box.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      CacheBoxShifter.shiftBoxInternal( (RenderBox) box, amount );
    }
  }

  public static void shiftBoxChilds( final RenderBox box, final long amount ) {
    if ( amount == 0 ) {
      return;
    }
    CacheBoxShifter.shiftBoxInternal( box, amount );
  }

  private static void shiftBoxInternal( final RenderBox box, final long amount ) {
    RenderNode node = box.getFirstChild();
    while ( node != null ) {
      node.shiftCached( amount );
      if ( ( node.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
        CacheBoxShifter.shiftBoxInternal( (RenderBox) node, amount );
      }
      node = node.getNext();
    }
  }

  public static void extendHeight( final RenderNode node, final long amount ) {
    if ( amount < 0 ) {
      throw new IllegalArgumentException( "Cannot shrink elements." );
    }
    if ( node == null || amount == 0 ) {
      return;
    }

    node.setCachedHeight( node.getCachedHeight() + amount );

    RenderBox parent = node.getParent();
    while ( parent != null ) {
      parent.setCachedHeight( parent.getCachedHeight() + amount );
      parent = parent.getParent();
    }
  }
}
