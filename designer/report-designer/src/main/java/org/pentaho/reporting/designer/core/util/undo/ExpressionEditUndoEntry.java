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


package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.Expression;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ExpressionEditUndoEntry implements UndoEntry {
  private int position;
  private Expression oldExpression;
  private Expression expression;

  public ExpressionEditUndoEntry( final int position, final Expression oldExpression, final Expression expression ) {
    this.position = position;
    this.oldExpression = oldExpression;
    this.expression = expression;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition definition = renderContext.getReportDefinition();
    definition.getExpressions().set( position, oldExpression );
    definition.notifyNodeChildRemoved( expression );
    definition.notifyNodeChildAdded( oldExpression );
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition definition = renderContext.getReportDefinition();
    definition.getExpressions().set( position, expression );
    definition.notifyNodeChildRemoved( oldExpression );
    definition.notifyNodeChildAdded( expression );
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
