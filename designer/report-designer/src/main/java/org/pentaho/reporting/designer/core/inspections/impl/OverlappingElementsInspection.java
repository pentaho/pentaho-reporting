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
