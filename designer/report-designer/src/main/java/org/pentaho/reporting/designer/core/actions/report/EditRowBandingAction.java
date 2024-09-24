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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.format.RowBandingDialog;
import org.pentaho.reporting.designer.core.util.undo.ExpressionAddedUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ExpressionEditUndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditRowBandingAction extends AbstractReportContextAction {
  public EditRowBandingAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditRowBandingAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "EditRowBandingAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditRowBandingAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditRowBandingAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final ReportDesignerContext context = getReportDesignerContext();
    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final RowBandingDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new RowBandingDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new RowBandingDialog( (JFrame) window );
    } else {
      dialog = new RowBandingDialog();
    }

    final RowBandingFunction function = findRowbandingFunction( activeContext );
    final AbstractReportDefinition report = activeContext.getReportDefinition();
    if ( function == null ) {
      final RowBandingFunction newFunction = new RowBandingFunction();
      if ( dialog.performEdit( newFunction ) ) {
        report.getExpressions().add( newFunction );
        activeContext.getUndo().addChange( ActionMessages.getString( "EditRowBandingAction.Text" ),
          new ExpressionAddedUndoEntry( report.getExpressions().size() - 1, newFunction ) );
        report.notifyNodeChildAdded( function );
      }
    } else {
      final RowBandingFunction instance = (RowBandingFunction) function.getInstance();
      if ( dialog.performEdit( instance ) ) {
        final ExpressionCollection expressionCollection = report.getExpressions();
        final int idx = expressionCollection.indexOf( function );
        expressionCollection.set( idx, instance );
        activeContext.getUndo().addChange( ActionMessages.getString( "EditRowBandingAction.Text" ),
          new ExpressionEditUndoEntry( idx, function, instance ) );
        report.fireModelLayoutChanged( report, ReportModelEvent.NODE_PROPERTIES_CHANGED, instance );
      }
    }
  }

  private RowBandingFunction findRowbandingFunction( final ReportDocumentContext activeContext ) {
    final Expression[] expressions = activeContext.getReportDefinition().getExpressions().getExpressions();
    for ( int i = 0; i < expressions.length; i++ ) {
      final Expression expression = expressions[ i ];
      if ( expression instanceof RowBandingFunction ) {
        return (RowBandingFunction) expression;
      }
    }
    return null;
  }
}
