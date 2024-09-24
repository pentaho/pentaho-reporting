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
