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
