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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;

public class CollectSelectedNodesStep extends IterateSimpleStructureProcessStep {
  private static final RenderNode[] EMPTY = new RenderNode[0];
  private ArrayList<RenderNode> resultList;
  private StrictBounds bounds;
  private StrictBounds nodebounds;
  private String namespace;
  private String name;
  private boolean strictSelection;

  public CollectSelectedNodesStep() {
    resultList = new ArrayList<RenderNode>();
    strictSelection = true;
    nodebounds = new StrictBounds();
  }

  public boolean isStrictSelection() {
    return strictSelection;
  }

  public void setStrictSelection( final boolean strictSelection ) {
    this.strictSelection = strictSelection;
  }

  public RenderNode[] getNodesAt( final LogicalPageBox logicalPageBox, final StrictBounds bounds,
      final String namespace, final String name ) {
    if ( logicalPageBox == null ) {
      throw new NullPointerException();
    }
    if ( bounds == null ) {
      throw new NullPointerException();
    }
    if ( ObjectUtilities.equal( bounds, this.bounds ) && ObjectUtilities.equal( namespace, this.namespace )
        && ObjectUtilities.equal( name, this.name ) ) {
      if ( resultList.isEmpty() ) {
        return CollectSelectedNodesStep.EMPTY;
      }
      return resultList.toArray( new RenderNode[resultList.size()] );
    }

    this.namespace = namespace;
    this.name = name;
    this.bounds = bounds;
    this.resultList.clear();
    startProcessing( logicalPageBox );
    if ( resultList.isEmpty() ) {
      return CollectSelectedNodesStep.EMPTY;
    }
    return resultList.toArray( new RenderNode[resultList.size()] );
  }

  protected boolean startBox( final RenderBox box ) {
    return handleNode( box );
  }

  protected void processOtherNode( final RenderNode node ) {
    handleNode( node );
  }

  protected boolean handleNode( final RenderNode box ) {
    if ( box.getLayoutNodeType() != LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
      if ( strictSelection ) {
        if ( box.isBoxVisible( bounds ) == false ) {
          return false;
        }
      } else {
        nodebounds.setRect( box.getX(), box.getY(), box.getWidth(), box.getHeight() );
        if ( StrictBounds.intersects( nodebounds, bounds ) == false ) {
          return false;
        }
      }
    }

    if ( name != null && namespace != null ) {
      final Object attribute = box.getAttributes().getAttribute( namespace, name );
      if ( attribute != null ) {

        this.resultList.add( box );
      }
    } else {
      this.resultList.add( box );
    }
    return true;
  }
}
