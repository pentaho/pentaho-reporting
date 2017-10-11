/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
