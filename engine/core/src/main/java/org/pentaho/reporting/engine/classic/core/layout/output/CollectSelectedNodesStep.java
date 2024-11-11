/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
