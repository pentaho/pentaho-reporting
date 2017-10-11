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
