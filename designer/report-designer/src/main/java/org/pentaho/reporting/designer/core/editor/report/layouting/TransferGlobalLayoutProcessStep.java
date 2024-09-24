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
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Computes the mapping between elements and their layouted position.
 *
 * @author Thomas Morgner
 */
public class TransferGlobalLayoutProcessStep extends IterateSimpleStructureProcessStep {
  private Map<InstanceID, Element> elementsById;
  private long age;
  private BreakPositionsList horizontalEdgePositions;
  private Map<InstanceID, Set<InstanceID>> conflicts;

  public TransferGlobalLayoutProcessStep() {
    horizontalEdgePositions = new BreakPositionsList();
    elementsById = new HashMap<InstanceID, Element>();
  }

  public Map<InstanceID, Element> getElementsById() {
    return elementsById;
  }

  public void performTransfer( final LogicalPageBox logicalPageBox,
                               final Map<InstanceID, Set<InstanceID>> conflicts,
                               final MasterReport report ) {
    //noinspection AssignmentToCollectionOrArrayFieldFromParameter
    this.conflicts = conflicts;
    this.age += 1;
    try {
      this.elementsById.clear();
      elementsById.put( report.getObjectID(), report );
      collectDesignTimeElements( report );
      startProcessing( logicalPageBox );
    } finally {
      this.conflicts = null;
    }
  }

  private void collectDesignTimeElements( final Section sectionReportElement ) {
    final int count = sectionReportElement.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final Element reportElement = sectionReportElement.getElement( i );
      final InstanceID id = reportElement.getObjectID();
      elementsById.put( id, reportElement );

      if ( reportElement instanceof Section ) {
        collectDesignTimeElements( (Section) reportElement );
      }
    }

    if ( sectionReportElement instanceof RootLevelBand ) {
      final RootLevelBand rlb = (RootLevelBand) sectionReportElement;
      for ( int i = 0; i < rlb.getSubReportCount(); i += 1 ) {
        final SubReport reportElement = rlb.getSubReport( i );
        final InstanceID id = reportElement.getObjectID();
        elementsById.put( id, reportElement );
        collectDesignTimeElements( reportElement );
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
    if ( data.getLayoutAge() == age ) {
      return true;
    }

    data.setX( box.getX() );
    data.setY( box.getY() );
    data.setWidth( box.getWidth() );
    data.setHeight( box.getHeight() );
    final BoxDefinition boxDefinition = box.getBoxDefinition();
    data.setPaddingX( boxDefinition.getPaddingLeft() + boxDefinition.getBorder().getLeft().getWidth() );
    data.setPaddingY( boxDefinition.getPaddingTop() + boxDefinition.getBorder().getTop().getWidth() );
    data.setLayoutAge( age );
    data.setElementType( box.getNodeType() );
    data.setConflictsInTableMode( conflicts.containsKey( id ) );

    horizontalEdgePositions.add( data.getX(), id );
    horizontalEdgePositions.add( data.getX() + data.getWidth(), id );
    return true;
  }

  public BreakPositionsList getHorizontalEdgePositions() {
    return horizontalEdgePositions;
  }

  public void reset() {
    horizontalEdgePositions.clear();
    elementsById.clear();
  }
}
