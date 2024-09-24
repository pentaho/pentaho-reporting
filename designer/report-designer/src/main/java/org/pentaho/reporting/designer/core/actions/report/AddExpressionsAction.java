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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.expressions.ExpressionChooserDialog;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.ExpressionAddedUndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class AddExpressionsAction extends AbstractReportContextAction {
  private static int nameCounter = 0;

  public AddExpressionsAction() {
    putValue( Action.NAME, ActionMessages.getString( "AddExpressionsAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "AddExpressionsAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "AddExpressionsAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "AddExpressionsAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getFunctionIcon() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final ExpressionChooserDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new ExpressionChooserDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new ExpressionChooserDialog( (JFrame) window );
    } else {
      dialog = new ExpressionChooserDialog();
    }

    final Expression expression = dialog.performSelect();
    if ( expression == null ) {
      return;
    }
    final AbstractReportDefinition definition = activeContext.getReportDefinition();
    // try generate a unique expression name
    String possibleName = expression.getClass().getSimpleName() + nameCounter++;
    while ( definition.getExpressions().get( possibleName ) != null ) {
      possibleName = expression.getClass().getSimpleName() + nameCounter++;
    }
    expression.setName( possibleName );
    final int position = definition.getExpressions().size();
    activeContext.getUndo().addChange( ActionMessages.getString( "AddExpressionsAction.Text" ),
      new ExpressionAddedUndoEntry( position, expression ) );
    definition.addExpression( expression );
  }
}
