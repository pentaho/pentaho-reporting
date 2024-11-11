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


package org.pentaho.reporting.designer.core.inspections.impl;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.LocationInfo;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.Map;
import java.util.Set;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class OverlappingElementsInspection extends AbstractStructureInspection {
  public OverlappingElementsInspection() {
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    if ( element instanceof Element == false ) {
      return;
    }

    final CachedLayoutData data = ModelUtility.getCachedLayoutData( (Element) element );
    if ( data.isConflictsInTableMode() ) {
      final Map<InstanceID, Set<InstanceID>> conflicts = reportRenderContext.getSharedRenderer().getConflicts();
      final Set<InstanceID> instanceIDs = conflicts.get( element.getObjectID() );
      final String message;
      if ( instanceIDs == null || instanceIDs.isEmpty() ) {
        message = Messages.getString( "OverlappingElementsInspection.ElementConflictsInTableMode", element.getName() );
      } else {
        final String elementName = computeConflictingElementName( reportRenderContext, instanceIDs );
        if ( instanceIDs.size() == 1 ) {
          message = Messages
            .getString( "OverlappingElementsInspection.ElementConflictsInTableModeSingle", element.getName(),
              elementName );
        } else {
          message = Messages.getString( "OverlappingElementsInspection.ElementConflictsInTableModeMultiples",
            element.getName(), elementName, instanceIDs.size() );
        }
      }

      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        message, new LocationInfo( element ) ) );
    }

  }

  private String computeConflictingElementName( final ReportDocumentContext reportRenderContext,
                                                final Set<InstanceID> instanceIDs ) {
    final Map<InstanceID, Element> elementsById = reportRenderContext.getSharedRenderer().getElementsById();
    final InstanceID firstElement = instanceIDs.iterator().next();
    final Element conflictingElement = elementsById.get( firstElement );
    if ( conflictingElement == null ) {
      return Messages.getString( "OverlappingElementsInspection.UnidentifiedElement" );
    }
    return conflictingElement.getName();
  }

  public boolean isInlineInspection() {
    return true;
  }
}
