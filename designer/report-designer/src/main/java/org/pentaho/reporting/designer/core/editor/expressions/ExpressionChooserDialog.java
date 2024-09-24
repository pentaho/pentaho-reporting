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

package org.pentaho.reporting.designer.core.editor.expressions;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ExpressionChooserDialog extends CommonDialog {
  private JTree expressionsTree;
  private ExpressionChooserDialog.AddAction addAction;
  private Expression expression;

  public ExpressionChooserDialog()
    throws HeadlessException {
    init();
  }

  public ExpressionChooserDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public ExpressionChooserDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( EditorExpressionsMessages.getString( "ExpressionChooserDialog.Title" ) );
    setModal( true );

    final DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
    selectionModel.setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
    selectionModel.addTreeSelectionListener( new ExpressionSelectionHandler() );

    expressionsTree = new JTree( ExpressionsTreeModel.getTreeModel() );
    expressionsTree.setRootVisible( false );
    expressionsTree.setSelectionModel( selectionModel );
    expressionsTree.addMouseListener( new DblClickHandler() );
    expressionsTree.setCellRenderer( new ExpressionTreeCellRenderer() );

    addAction = new AddAction();

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ExpressionChooser";
  }

  protected Component createContentPane() {
    return ( new JScrollPane( expressionsTree ) );
  }

  private class DblClickHandler extends MouseAdapter {
    private DblClickHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getButton() != 1 ) {
        return;
      }
      if ( e.getClickCount() < 2 ) {
        return;
      }

      if ( addAction.isEnabled() ) {
        addAction.actionPerformed( null );
      }
    }
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    final TreePath selectionPath = expressionsTree.getSelectionPath();
    final Object o = selectionPath.getLastPathComponent();
    if ( o instanceof ExpressionMetaData == false ) {
      return false;
    }
    return true;
  }

  private class AddAction extends AbstractAction {
    private AddAction() {
      putValue( Action.NAME, EditorExpressionsMessages.getString( "ExpressionChooserDialog.Add" ) );
      setEnabled( false );
    }

    public void actionPerformed( final ActionEvent e ) {
      final TreePath selectionPath = expressionsTree.getSelectionPath();
      final Object o = selectionPath.getLastPathComponent();
      if ( o instanceof ExpressionMetaData == false ) {
        return;
      }
      try {
        final ExpressionMetaData metaData = (ExpressionMetaData) o;
        expression = (Expression) metaData.getExpressionType().newInstance();
      } catch ( Exception e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }

      setConfirmed( true );
      dispose();
    }
  }

  private class ExpressionSelectionHandler implements TreeSelectionListener {
    private ExpressionSelectionHandler() {
    }

    public void valueChanged( final TreeSelectionEvent e ) {
      final TreePath path = e.getPath();
      if ( path == null ) {
        addAction.setEnabled( false );
        return;
      }
      final Object o = path.getLastPathComponent();
      if ( o instanceof ExpressionMetaData ) {
        addAction.setEnabled( true );
      } else {
        addAction.setEnabled( false );
      }
    }
  }

  public Expression performSelect() {
    expression = null;
    if ( performEdit() == false ) {
      return null;
    }
    if ( expression == null ) {
      addAction.actionPerformed( null );
    }
    return expression;
  }

}
