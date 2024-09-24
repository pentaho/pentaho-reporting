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
