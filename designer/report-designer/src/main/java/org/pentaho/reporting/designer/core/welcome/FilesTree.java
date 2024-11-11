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


package org.pentaho.reporting.designer.core.welcome;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.global.OpenReportAction;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;


public class FilesTree extends JTree {
  private class MouseHandler extends MouseAdapter {
    private MouseHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1 ) {
        performDefaultAction();
      }
    }
  }

  private class KeyboardHandler extends KeyAdapter {
    private KeyboardHandler() {
    }

    /**
     * Invoked when a key has been typed. This event occurs when a key press is followed by a key release.
     */
    public void keyTyped( final KeyEvent e ) {
      if ( e.getKeyChar() == KeyEvent.VK_ENTER ) {
        performDefaultAction();
      }
    }
  }

  private ReportDesignerContext reportDesignerContext;
  private JDialog owner;

  public FilesTree( final TreeModel treeModel,
                    final ReportDesignerContext reportDesignerContext,
                    final JDialog owner ) {
    super( treeModel );
    this.reportDesignerContext = reportDesignerContext;
    this.owner = owner;

    this.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
    this.setRootVisible( false );
    this.setShowsRootHandles( true );
    this.addMouseListener( new MouseHandler() );
    this.addKeyListener( new KeyboardHandler() );
  }

  private void performDefaultAction() {
    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
    if ( node == null ) {
      return;
    }
    if ( node.isLeaf() ) {
      if ( owner != null ) {
        owner.setVisible( false );
      }
      OpenReportAction.openReport( new File( node.getUserObject().toString() ), reportDesignerContext );
    }
  }
}
