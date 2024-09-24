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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.Map;

/**
 * Computes the mapping between elements and their layouted position.
 *
 * @author Thomas Morgner
 */
public class TransferLayoutProcessStep extends IterateSimpleStructureProcessStep {
  private Map<InstanceID, Element> elementsById;
  private BreakPositionsList verticalEdgePositions;

  public TransferLayoutProcessStep() {
  }

  public void performTransfer( final Section section,
                               final LogicalPageBox logicalPageBox,
                               final Map<InstanceID, Element> elementHashMap,
                               final BreakPositionsList verticalEdgePositions ) {
    //noinspection AssignmentToCollectionOrArrayFieldFromParameter
    this.verticalEdgePositions = verticalEdgePositions;
    this.elementsById = elementHashMap;
    try {
      this.elementsById.clear();
      elementsById.put( section.getObjectID(), section );
      collectElements( section );

      if ( section instanceof RootLevelBand ) {
        final RootLevelBand rl = (RootLevelBand) section;
        final int count = rl.getSubReportCount();
        for ( int i = 0; i < count; i++ ) {
          final SubReport report = rl.getSubReport( i );
          elementsById.put( report.getObjectID(), report );
        }
      }

      startProcessing( logicalPageBox );
    } finally {
      this.elementsById = null;
      this.verticalEdgePositions = null;
    }
  }

  private void collectElements( final Section sectionReportElement ) {
    final int count = sectionReportElement.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final Element reportElement = sectionReportElement.getElement( i );
      final InstanceID id = reportElement.getObjectID();
      elementsById.put( id, reportElement );

      if ( reportElement instanceof SubReport ) {
        continue;
      }
      if ( reportElement instanceof Section ) {
        collectElements( (Section) reportElement );
      }
    }
  }

  public boolean startBox( final RenderBox box ) {
    final InstanceID id = box.getNodeLayoutProperties().getInstanceId();
    final Element element = elementsById.get( id );
    if ( element == null ) {
      return true;
    }

    final CachedLayoutData data = ModelUtility.getCachedLayoutData( element );
    verticalEdgePositions.add( data.getY(), id );
    verticalEdgePositions.add( data.getY() + data.getHeight(), id );
    return true;
  }

}
