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


package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.CollectSelectedNodesStep;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.HashSet;

public class DesignerCollectSelectedNodesStep extends CollectSelectedNodesStep {
  private boolean insideElement;
  private HashSet<InstanceID> rootNodes;

  public DesignerCollectSelectedNodesStep() {
    setStrictSelection( false );
  }

  public HashSet<InstanceID> getRootNodes() {
    return rootNodes;
  }

  public void setRootNodes( final HashSet<InstanceID> rootNodes ) {
    this.rootNodes = rootNodes;
  }

  protected boolean handleNode( final RenderNode box ) {
    if ( insideElement && ModelUtility.isHideInLayoutGui( box ) == true ) {
      return false;
    }

    return super.handleNode( box );
  }

  protected boolean startBox( final RenderBox box ) {
    if ( insideElement && ( box.getElementType() instanceof SubReportType ) ) {
      handleNode( box );
      return false;
    }

    if ( rootNodes.contains( box.getInstanceId() ) ) {
      insideElement = true;
    }
    return handleNode( box );
  }

  protected void finishBox( final RenderBox box ) {
    if ( rootNodes.contains( box.getInstanceId() ) ) {
      insideElement = false;
    }
  }


}
