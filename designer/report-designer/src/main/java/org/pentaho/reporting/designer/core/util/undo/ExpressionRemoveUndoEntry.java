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


package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.Expression;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ExpressionRemoveUndoEntry implements UndoEntry {
  private int position;
  private Expression expression;

  public ExpressionRemoveUndoEntry( final int position, final Expression expression ) {
    this.position = position;
    this.expression = expression;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition definition = renderContext.getReportDefinition();
    if ( definition.getExpressions().contains( expression ) == false ) {
      definition.getExpressions().add( position, expression );
      definition.notifyNodeChildAdded( expression );
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition definition = renderContext.getReportDefinition();
    definition.getExpressions().removeExpression( position );
    definition.notifyNodeChildRemoved( expression );
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
