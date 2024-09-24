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
