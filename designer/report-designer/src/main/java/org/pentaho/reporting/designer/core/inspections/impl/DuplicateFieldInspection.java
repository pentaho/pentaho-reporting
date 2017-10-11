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
