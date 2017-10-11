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
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.inspections.AttributeLocationInfo;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.LocationInfo;
import org.pentaho.reporting.designer.core.inspections.StyleLocationInfo;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.util.Locale;

public class ReportMigrationInspection extends AbstractStructureInspection {
  private int compatibilityLevel;
  private String compatibilityText;

  public ReportMigrationInspection() {
  }

  public void inspect( final ReportDesignerContext designerContext,
                       final ReportDocumentContext reportRenderContext,
                       final InspectionResultListener resultHandler ) throws ReportDataFactoryException {
    final MasterReport masterReportElement = reportRenderContext.getContextRoot();
    final Integer compatibilityLevel = masterReportElement.getCompatibilityLevel();
    if ( compatibilityLevel == null ||
      compatibilityLevel == ClassicEngineBoot.VERSION_TRUNK ) {
      // no need to test any compatibility, as this report is a TRUNK report.
      return;
    }

    if ( compatibilityLevel == ClassicEngineBoot.computeCurrentVersionId() ) {
      // this report is the same level as the current version of the engine. No need to check anything.
      return;
    }

    this.compatibilityLevel = compatibilityLevel;
    this.compatibilityText = ClassicEngineBoot.printVersion( compatibilityLevel );
    super.inspect( designerContext, reportRenderContext, resultHandler );
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportRenderContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {
    final ElementMetaData elementMetaData = element.getMetaData();
    if ( elementMetaData.getCompatibilityLevel() > compatibilityLevel ) {
      // warn: ReportMigrationInspection.ElementInvalid
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "ReportMigrationInspection.ElementInvalid",
          element.getName(), compatibilityText ), new LocationInfo( element ) ) );
    }

    for ( final AttributeMetaData attributeMetaData : elementMetaData.getAttributeDescriptions() ) {
      if ( attributeMetaData.getCompatibilityLevel() > compatibilityLevel ) {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "ReportMigrationInspection.AttributeInvalid",
            attributeMetaData.getDisplayName( Locale.getDefault() ), element.getName(), compatibilityText ),
          new AttributeLocationInfo( element, attributeMetaData.getNameSpace(), attributeMetaData.getName(),
            false ) ) );
      }
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
    if ( expressionMetaData == null ) {
      return;
    }

    final AttributeMetaData attributeMetaData =
      element.getMetaData().getAttributeDescription( attributeNamespace, attributeName );
    if ( attributeMetaData == null ) {
      return;
    }

    if ( attributeMetaData.getCompatibilityLevel() > compatibilityLevel ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "ReportMigrationInspection.AttributeExpressionDefined",
          attributeMetaData.getDisplayName( Locale.getDefault() ), element.getName(), compatibilityText ),
        new AttributeLocationInfo( element, attributeMetaData.getNameSpace(), attributeMetaData.getName(), false ) ) );
    }

    if ( expressionMetaData.getCompatibilityLevel() > compatibilityLevel ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "ReportMigrationInspection.AttributeExpressionInvalid",
          attributeMetaData.getDisplayName( Locale.getDefault() ), element.getName(), compatibilityText ),
        new AttributeLocationInfo( element, attributeMetaData.getNameSpace(), attributeMetaData.getName(), false ) ) );
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
    if ( expressionMetaData == null ) {
      return;
    }
    final StyleMetaData styleMetaData = element.getMetaData().getStyleDescription( styleKey );
    if ( styleMetaData == null ) {
      return;
    }

    if ( styleMetaData.getCompatibilityLevel() > compatibilityLevel ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "ReportMigrationInspection.StyleExpressionInvalid",
          styleMetaData.getDisplayName( Locale.getDefault() ), element.getName(), compatibilityText ),
        new StyleLocationInfo( element, styleKey, false ) ) );
    }

    if ( expressionMetaData.getCompatibilityLevel() > compatibilityLevel ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "ReportMigrationInspection.StyleExpressionInvalid",
          styleMetaData.getDisplayName( Locale.getDefault() ), element.getName(), compatibilityText ),
        new StyleLocationInfo( element, styleKey, false ) ) );
    }
  }

  protected void inspectExpression( final ReportDesignerContext designerContext,
                                    final ReportDocumentContext reportRenderContext,
                                    final InspectionResultListener resultHandler,
                                    final String[] columnNames,
                                    final Expression expression,
                                    final ExpressionMetaData expressionMetaData ) {
    if ( expressionMetaData == null ) {
      return;
    }
    if ( expressionMetaData.getCompatibilityLevel() > compatibilityLevel ) {
      resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
        Messages.getString( "ReportMigrationInspection.ExpressionInvalid",
          expressionMetaData.getDisplayName( Locale.getDefault() ), compatibilityText ),
        new LocationInfo( expression ) ) );
    }
  }

  public boolean isInlineInspection() {
    return true;
  }
}
