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

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

/**
 * Applies the createRollbackInformation-marker to all closed boxes and applies the pending marker to all currently open
 * boxes. During a roll-back, we can use these markers to identify boxes that have been added since the last
 * createRollbackInformation to remove them from the model.
 *
 * @author Thomas Morgner
 */
public final class ApplyCommitStep extends IterateSimpleStructureProcessStep {
  public ApplyCommitStep() {
  }

  public void compute( final LogicalPageBox pageBox ) {
    startProcessing( pageBox );
  }

  protected boolean startBox( final RenderBox box ) {
    if ( box.isCommited() ) {
      return false;
    }

    box.commit();
    return true;
  }
}
