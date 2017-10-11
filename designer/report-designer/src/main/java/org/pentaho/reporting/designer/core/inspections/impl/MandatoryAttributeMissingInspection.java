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
import org.pentaho.reporting.designer.core.inspections.AttributeLocationInfo;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;

import java.util.Locale;

/**
 * Checks, whether all mandatory element attributes are set. This also checks whether all fieldnames are set.
 *
 * @author Thomas Morgner
 */
public class MandatoryAttributeMissingInspection extends AbstractStructureInspection {
  public MandatoryAttributeMissingInspection() {
  }

  public boolean isInlineInspection() {
    return true;
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    final ElementMetaData metaData = element.getMetaData();
    final AttributeMetaData[] attributeDescriptions = metaData.getAttributeDescriptions();
    for ( int i = 0; i < attributeDescriptions.length; i++ ) {
      final AttributeMetaData attributeMetaData = attributeDescriptions[ i ];
      final Object value = element.getAttribute( attributeMetaData.getNameSpace(), attributeMetaData.getName() );
      if ( value != null ) {
        if ( attributeMetaData.getTargetType().isInstance( value ) == false ) {
          // notify of invalid type
          resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
            Messages.getString( "MandatoryAttributeMissingInspection.AttributeValueHasInvalidType",
              element.getName(), attributeMetaData.getDisplayName( Locale.getDefault() ),
              attributeMetaData.getTargetType().getName(), value.getClass().getName() ),
            new AttributeLocationInfo( element, attributeMetaData.getNameSpace(), attributeMetaData.getName(),
              false ) ) );
        }

        if ( attributeMetaData.isDesignTimeValue() ) {
          if ( element.getAttributeExpression( attributeMetaData.getNameSpace(), attributeMetaData.getName() )
            != null ) {
            // warn that design-time values have no need for a expression
            resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
              Messages.getString( "MandatoryAttributeMissingInspection.DesignTimeAttributeWithExpression",
                element.getName(), attributeMetaData.getDisplayName( Locale.getDefault() ) ),
              new AttributeLocationInfo( element, attributeMetaData.getNameSpace(), attributeMetaData.getName(),
                false ) ) );
          }
        }
        continue;
      }

      if ( attributeMetaData.isMandatory() == false ) {
        continue;
      }
      if ( attributeMetaData.isDesignTimeValue() == false ) {
        if ( element.getAttributeExpression( attributeMetaData.getNameSpace(), attributeMetaData.getName() ) != null ) {
          continue;
        }

        // warn that either value or expression must be set
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "MandatoryAttributeMissingInspection.MandatoryAttributeWithoutValueOrExpression",
            element.getName(), attributeMetaData.getDisplayName( Locale.getDefault() ) ),
          new AttributeLocationInfo( element, attributeMetaData.getNameSpace(), attributeMetaData.getName(),
            false ) ) );

      } else {
        // warn that a value must be set
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "MandatoryAttributeMissingInspection.MandatoryAttributeWithoutValue",
            element.getName(), attributeMetaData.getDisplayName( Locale.getDefault() ) ),
          new AttributeLocationInfo( element, attributeMetaData.getNameSpace(), attributeMetaData.getName(),
            false ) ) );
      }
    }
  }
}
