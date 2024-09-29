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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

public class FindOldestProcessKeyStep extends IterateSimpleStructureProcessStep {
  private ReportStateKey key;
  private boolean finishedPaginate;

  public FindOldestProcessKeyStep() {
  }

  public ReportStateKey find( final RenderBox box ) {
    if ( box.isProcessKeyCacheValid() ) {
      return box.getProcessKeyCached();
    }

    key = null;
    finishedPaginate = box.isFinishedPaginate();
    startProcessing( box );
    box.setProcessKeyCached( key );
    return key;
  }

  protected void processOtherNode( final RenderNode node ) {
    final ReportStateKey stateKey = node.getStateKey();
    if ( stateKey == null || stateKey.isInlineSubReportState() ) {
      return;
    }

    if ( key == null ) {
      key = stateKey;
      return;
    }

    if ( stateKey.getSequenceCounter() > key.getSequenceCounter() ) {
      key = stateKey;
    }
  }

  protected boolean startBox( final RenderBox box ) {
    if ( box.isProcessKeyCacheValid() ) {
      key = box.getProcessKeyCached();
      return false;
    }

    processOtherNode( box );
    if ( finishedPaginate == true ) {
      box.setFinishedPaginate( true );
    }
    return true;
  }

  protected void finishBox( final RenderBox box ) {
    box.setProcessKeyCached( key );
  }
}
