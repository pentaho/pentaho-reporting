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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

public final class RollbackStep extends IterateSimpleStructureProcessStep {
  private static final Log logger = LogFactory.getLog( RollbackStep.class );

  public RollbackStep() {
  }

  public void compute( final LogicalPageBox pageBox ) {
    if ( pageBox.isAppliedSeen() == false ) {
      throw new IllegalStateException( "How can I not see the root of the layout-tree?" );
    }
    startProcessing( pageBox );
    pageBox.rollbackSaveInformation();
    // todo PRD-4606
    // pageBox.resetCacheState(true);
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    processBoxChilds( box );
  }

  protected boolean startBox( final RenderBox parent ) {
    boolean needDeepDive = false;
    RenderNode child = parent.getFirstChild();
    while ( child != null ) {
      final int type = child.getNodeType();
      if ( type == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
        child = child.getNext();
        continue;
      }

      if ( ( type & LayoutNodeTypes.MASK_BOX ) != LayoutNodeTypes.MASK_BOX ) {
        // this should never be an issue, as plain render-nodes are no added to boxes other than
        // canvas boxes made of bands or inline-boxes. Therefore we are guaranteed that we already
        // removed the parent of a render node.
        throw new IllegalStateException(
            "Assertation error: A rollback-process encountered a render-node it should not have encountered." );
      }

      final RenderBox box = (RenderBox) child;
      if ( box.isAppliedSeen() == false ) {
        // must be a new box. Go away, evil new box ...
        final RenderNode next = child.getNext();
        parent.remove( box );
        // todo PRD-4606
        parent.resetCacheState( false );
        child = next;
        continue;
      }

      if ( box.isCommited() == false ) {
        needDeepDive = true;
      }
      child = child.getNext();
    }
    return needDeepDive;
  }

  protected void finishBox( final RenderBox box ) {
    if ( box.isCommited() == false ) {
      box.rollback( true );
    }
  }

}
