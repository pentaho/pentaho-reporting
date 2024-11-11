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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.inspections.Inspection;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;

import java.util.Map;

public abstract class AbstractStructureInspection implements Inspection {
  protected AbstractStructureInspection() {
  }

  public void inspect( final ReportDesignerContext designerContext,
                       final ReportDocumentContext reportRenderContext,
                       final InspectionResultListener resultHandler ) throws ReportDataFactoryException {
    final AbstractReportDefinition reportDefinition = reportRenderContext.getReportDefinition();
    final ContextAwareDataSchemaModel model = reportRenderContext.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();

    if ( reportDefinition instanceof MasterReport ) {
      final MasterReport mr = (MasterReport) reportDefinition;
      final ReportParameterDefinition parameters = mr.getParameterDefinition();
      final ParameterDefinitionEntry[] entries = parameters.getParameterDefinitions();
      for ( int i = 0; i < entries.length; i++ ) {
        final ParameterDefinitionEntry entry = entries[ i ];
        inspectParameter( designerContext, reportRenderContext, resultHandler, columnNames, parameters, entry );
      }
    }

    final CompoundDataFactory dataFactory = CompoundDataFactory.normalize( reportDefinition.getDataFactory() );
    final int size = dataFactory.size();
    for ( int i = 0; i < size; i++ ) {
      inspectDataSource( designerContext, reportRenderContext, resultHandler, columnNames, dataFactory );
    }

    final ExpressionCollection expressions = reportDefinition.getExpressions();
    final Expression[] expressionsArray = expressions.getExpressions();
    for ( int i = 0; i < expressionsArray.length; i++ ) {
      final Expression expression = expressionsArray[ i ];
      inspectExpression( designerContext, reportRenderContext, resultHandler, columnNames, expression );
    }

    inspectElement( designerContext, reportRenderContext, resultHandler, columnNames, reportDefinition );
    traverseSection( designerContext, reportRenderContext, resultHandler, columnNames, reportDefinition );
  }

  public void traverseSection( final ReportDesignerContext designerContext,
                               final ReportDocumentContext reportRenderContext,
                               final InspectionResultListener resultHandler,
                               final String[] columnNames,
                               final Section section ) {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );
      inspectElement( designerContext, reportRenderContext, resultHandler, columnNames, element );
      if ( element instanceof SubReport && reportRenderContext.getReportDefinition() != element ) {
        continue;
      }

      if ( element instanceof Section ) {
        traverseSection( designerContext, reportRenderContext, resultHandler, columnNames, (Section) element );
      }
    }
  }

  protected void inspectElement( final ReportDesignerContext designerContext,
                                 final ReportDocumentContext reportRenderContext,
                                 final InspectionResultListener resultHandler,
                                 final String[] columnNames,
                                 final ReportElement element ) {

  }

  protected void traverseAttributeExpressions( final ReportDesignerContext designerContext,
                                               final ReportDocumentContext reportRenderContext,
                                               final InspectionResultListener resultHandler,
                                               final String[] columnNames,
                                               final ReportElement element ) {

    final String[] attrExprNamespaces = element.getAttributeExpressionNamespaces();
    for ( int i = 0; i < attrExprNamespaces.length; i++ ) {
      final String attrExprNamespace = attrExprNamespaces[ i ];
      final String[] names = element.getAttributeExpressionNames( attrExprNamespace );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[ j ];
        final Expression expression = element.getAttributeExpression( attrExprNamespace, name );
        if ( expression == null ) {
          continue;
        }
        final String expressionName = expression.getClass().getName();
        if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionName ) == false ) {
          inspectAttributeExpression( designerContext, reportRenderContext, resultHandler,
            columnNames, element, attrExprNamespace, name, expression, null );
        } else {
          final ExpressionMetaData metaData = ExpressionRegistry.getInstance().getExpressionMetaData( expressionName );
          inspectAttributeExpression( designerContext, reportRenderContext, resultHandler,
            columnNames, element, attrExprNamespace, name, expression, metaData );
        }
      }
    }
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
  }

  protected void traverseStyleExpressions( final ReportDesignerContext designerContext,
                                           final ReportDocumentContext reportRenderContext,
                                           final InspectionResultListener resultHandler,
                                           final String[] columnNames,
                                           final ReportElement element ) {

    final Map<StyleKey, Expression> map = element.getStyleExpressions();
    for ( final Map.Entry<StyleKey, Expression> entry : map.entrySet() ) {
      final StyleKey styleKey = entry.getKey();
      final Expression expression = entry.getValue();

      if ( expression == null ) {
        continue;
      }
      final String expressionName = expression.getClass().getName();
      if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionName ) == false ) {
        inspectStyleExpression( designerContext, reportRenderContext, resultHandler,
          columnNames, element, styleKey, expression, null );
      } else {
        final ExpressionMetaData metaData = ExpressionRegistry.getInstance().getExpressionMetaData( expressionName );
        inspectStyleExpression( designerContext, reportRenderContext, resultHandler,
          columnNames, element, styleKey, expression, metaData );
      }
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
  }

  protected void inspectExpression( final ReportDesignerContext designerContext,
                                    final ReportDocumentContext reportRenderContext,
                                    final InspectionResultListener resultHandler,
                                    final String[] columnNames,
                                    final Expression expression ) {
    final String expressionName = expression.getClass().getName();
    if ( ExpressionRegistry.getInstance().isExpressionRegistered( expressionName ) == false ) {
      inspectExpression( designerContext, reportRenderContext, resultHandler,
        columnNames, expression, null );
    } else {
      final ExpressionMetaData metaData = ExpressionRegistry.getInstance().getExpressionMetaData( expressionName );
      inspectExpression( designerContext, reportRenderContext, resultHandler,
        columnNames, expression, metaData );
    }
  }

  protected void inspectExpression( final ReportDesignerContext designerContext,
                                    final ReportDocumentContext reportRenderContext,
                                    final InspectionResultListener resultHandler,
                                    final String[] columnNames,
                                    final Expression expression,
                                    final ExpressionMetaData expressionMetaData ) {
  }

  protected void inspectParameter( final ReportDesignerContext designerContext,
                                   final ReportDocumentContext reportRenderContext,
                                   final InspectionResultListener resultHandler,
                                   final String[] columnNames,
                                   final ReportParameterDefinition definition,
                                   final ParameterDefinitionEntry parameter ) {
  }

  protected void inspectDataSource( final ReportDesignerContext designerContext,
                                    final ReportDocumentContext reportRenderContext,
                                    final InspectionResultListener resultHandler,
                                    final String[] columnNames,
                                    final DataFactory dataFactory ) {
  }
}
