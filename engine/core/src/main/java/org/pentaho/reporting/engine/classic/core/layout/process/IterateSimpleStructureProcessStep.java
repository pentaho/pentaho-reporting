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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

import java.io.Serializable;

public abstract class IterateSimpleStructureProcessStep implements Serializable {
  protected IterateSimpleStructureProcessStep() {
  }

  protected final void startProcessing( final RenderNode node ) {
    final int nodeType = node.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
      final LogicalPageBox box = (LogicalPageBox) node;
      if ( startBox( box ) ) {
        startProcessing( box.getWatermarkArea() );
        startProcessing( box.getHeaderArea() );
        processBoxChilds( box );
        startProcessing( box.getFooterArea() );
        startProcessing( box.getRepeatFooterArea() );
      }
      finishBox( box );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startBox( box ) ) {
        processBoxChilds( box );
      }
      finishBox( box );
    } else {
      processOtherNode( node );
    }
  }

  protected void processBoxChilds( final RenderBox box ) {
    RenderNode node = box.getFirstChild();
    while ( node != null ) {
      startProcessing( node );
      node = node.getNext();
    }
  }

  protected void processOtherNode( final RenderNode node ) {
  }

  protected void finishBox( final RenderBox box ) {
  }

  protected boolean startBox( final RenderBox box ) {
    return true;
  }

}
