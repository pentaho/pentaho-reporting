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
import org.pentaho.reporting.designer.core.inspections.Inspection;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.LocationInfo;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;

import java.util.HashSet;

/**
 * This inspection warns if the datasource contains duplicate column names.
 *
 * @author Thomas Morgner
 */
public class DuplicateFieldInspection implements Inspection {
  public DuplicateFieldInspection() {
  }

  public boolean isInlineInspection() {
    return true;
  }

  public void inspect( final ReportDesignerContext designerContext,
                       final ReportDocumentContext reportRenderContext,
                       final InspectionResultListener resultHandler ) throws ReportDataFactoryException {
    final AbstractReportDefinition reportDefinition = reportRenderContext.getReportDefinition();
    final ContextAwareDataSchemaModel model = reportRenderContext.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();

    final HashSet<String> warnedNames = new HashSet<String>();
    final HashSet<String> exprNames = new HashSet<String>();
    final Expression[] expressions = reportDefinition.getExpressions().getExpressions();
    for ( int i = 0; i < expressions.length; i++ ) {
      final Expression expression = expressions[ i ];
      final String columnName = expression.getName();
      if ( exprNames.add( columnName ) == false ) {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "DuplicateFieldInspection.ExpressionDuplicate", columnName ),
          new LocationInfo( reportDefinition ) ) );
        warnedNames.add( columnName );
      }
    }

    final HashSet<String> cols = new HashSet<String>();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String columnName = columnNames[ i ];
      if ( warnedNames.contains( columnName ) == false && cols.add( columnName ) == false ) {
        resultHandler.notifyInspectionResult( new InspectionResult( this, InspectionResult.Severity.WARNING,
          Messages.getString( "DuplicateFieldInspection.OtherDuplicate", columnName ),
          new LocationInfo( reportDefinition ) ) );

      }
    }

  }
}
