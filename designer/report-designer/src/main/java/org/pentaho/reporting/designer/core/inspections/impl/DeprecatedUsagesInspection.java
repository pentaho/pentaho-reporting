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
import org.pentaho.reporting.designer.core.inspections.LocationInfo;
import org.pentaho.reporting.designer.core.inspections.StyleLocationInfo;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.util.Locale;

/**
 * Checks, whether all mandatory element attributes are set. This also checks whether all fieldnames are set.
 *
 * @author Thomas Morgner
 */
public class DeprecatedUsagesInspection extends AbstractStructureInspection {
  public DeprecatedUsagesInspection() {
  }

  public boolean isInlineInspection() {
    return true;
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    if ( element.getMetaData().isDeprecated() ) {
      final String message = element.getMetaData().getDeprecationMessage( Locale.getDefault() );
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.ElementTypeDeprecated", element.getName(), message ),
        new LocationInfo( element ) ) );
    }

    traverseAttributeExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
    traverseStyleExpressions( designerContext, reportRenderContext, resultHandler, columnNames, element );
  }

  protected void inspectAttributeExpression( final ReportDesignerContext designerContext,
                                             final ReportDocumentContext reportRenderContext,
                                             final InspectionResultListener resultHandler,
                                             final String[] columnNames,
                                             final ReportElement element,
                                             final String attributeNamespace,
                                             final String attributeName,
                                             final Expression expression,
                                             final ExpressionMetaData expressionMetaData ) {
    final AttributeMetaData attrDescr =
      element.getMetaData().getAttributeDescription( attributeNamespace, attributeName );
    if ( attrDescr != null && attrDescr.isDeprecated() ) {
      final String message = attrDescr.getDeprecationMessage( Locale.getDefault() );
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.StyleExpressionTargetDeprecated",
          element.getName(), attrDescr.getDisplayName( Locale.getDefault() ), message ),
        new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
    }

    if ( expressionMetaData == null ) {
      return;
    }

    if ( expressionMetaData.isDeprecated() == false ) {
      return;
    }
    final String message = expressionMetaData.getDeprecationMessage( Locale.getDefault() );
    if ( attrDescr != null ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.StyleExpressionDeprecated",
          element.getName(), attrDescr.getDisplayName( Locale.getDefault() ), message ),
        new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
    } else {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.StyleExpressionDeprecatedNoMetaData",
          element.getName(), attributeNamespace, attributeName, message ),
        new AttributeLocationInfo( element, attributeNamespace, attributeName, true ) ) );
    }

  }

  protected void inspectStyleExpression( final ReportDesignerContext designerContext,
                                         final ReportDocumentContext reportRenderContext,
                                         final InspectionResultListener resultHandler,
                                         final String[] columnNames,
                                         final ReportElement element,
                                         final StyleKey styleKey,
                                         final Expression expression,
                                         final ExpressionMetaData expressionMetaData ) {
    final StyleMetaData styleDescription = element.getMetaData().getStyleDescription( styleKey );
    if ( styleDescription != null && styleDescription.isDeprecated() ) {
      final String message = styleDescription.getDeprecationMessage( Locale.getDefault() );
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.StyleExpressionTargetDeprecated",
          element.getName(), styleDescription.getDisplayName( Locale.getDefault() ), message ),
        new StyleLocationInfo( element, styleKey, true ) ) );
    }

    if ( expressionMetaData == null ) {
      return;
    }

    if ( expressionMetaData.isDeprecated() == false ) {
      return;
    }
    final String message = expressionMetaData.getDeprecationMessage( Locale.getDefault() );
    if ( styleDescription != null ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.StyleExpressionDeprecated",
          element.getName(), styleDescription.getDisplayName( Locale.getDefault() ), message ),
        new StyleLocationInfo( element, styleKey, true ) ) );
    } else {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.StyleExpressionDeprecatedNoMetaData",
          element.getName(), styleKey.getName(), message ),
        new StyleLocationInfo( element, styleKey, true ) ) );
    }
  }

  protected void inspectExpression( final ReportDesignerContext designerContext,
                                    final ReportDocumentContext reportRenderContext,
                                    final InspectionResultListener resultHandler,
                                    final String[] columnNames,
                                    final Expression expression,
                                    final ExpressionMetaData expressionMetaData ) {
    if ( expressionMetaData != null && expressionMetaData.isDeprecated() ) {
      final String message = expressionMetaData.getDeprecationMessage( Locale.getDefault() );
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.ExpressionDeprecated", expression.getName(), message ),
        new LocationInfo( expression ) ) );
    }
  }

  protected void inspectDataSource( final ReportDesignerContext designerContext,
                                    final ReportDocumentContext reportRenderContext,
                                    final InspectionResultListener resultHandler,
                                    final String[] columnNames,
                                    final DataFactory dataFactory ) {
    final DataFactoryMetaData metaData = dataFactory.getMetaData();
    if ( metaData.isDeprecated() ) {
      final String message = metaData.getDeprecationMessage( Locale.getDefault() );
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "DeprecatedUsagesInspection.DataFactoryDeprecated",
          metaData.getDisplayName( Locale.getDefault() ), message ),
        new LocationInfo( dataFactory ) ) );
    }
  }
}
