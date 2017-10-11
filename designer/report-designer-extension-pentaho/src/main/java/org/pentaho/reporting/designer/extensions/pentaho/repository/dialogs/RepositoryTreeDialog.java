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

package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.model.RepositoryTreeModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.RepositoryTreeCellRenderer;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.pensol.WebSolutionFileObject;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class RepositoryTreeDialog extends CommonDialog {
  private class ShowHiddenFilesAction extends AbstractAction {
    private ShowHiddenFilesAction() {
      putValue( Action.NAME, Messages.getInstance().getString( "ShowHiddenFilesAction.Name" ) );
      setSelected( Boolean.FALSE );
    }

    private Boolean getSelected() {
      return (Boolean) this.getValue( Action.SELECTED_KEY );
    }

    private void setSelected( final Boolean selected ) {
      this.putValue( Action.SELECTED_KEY, selected );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      repositoryTreeModel.setShowHiddenFiles( Boolean.TRUE.equals( getSelected() ) );
    }
  }

  private class DoubleClickHandler extends MouseAdapter {
    private DoubleClickHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() <= 1 ) {
        return;
      }
      try {
        final TreePath selectionPath = repositoryBrowser.getSelectionPath();
        if ( selectionPath == null ) {
          return;
        }

        final Object o = repositoryBrowser.getLastSelectedPathComponent();
        if ( o instanceof FileObject ) {
          final FileObject treeNode = (FileObject) o;
          if ( repositoryBrowser.isExpanded( selectionPath ) && treeNode.getType() == FileType.FOLDER ) {
            setSelectedPath( treeNode );
            setConfirmed( true );
            setVisible( false );
          }
        }
      } catch ( FileSystemException fse ) {
        // ignore ..
      }
    }
  }

  private class NewFolderAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private NewFolderAction() {
      final URL location =
          RepositoryTreeDialog.class
              .getResource( "/org/pentaho/reporting/designer/extensions/pentaho/repository/resources/newfolder.png" );
      putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString(
          "SolutionRepositoryTreeDialog.NewFolderAction.Name" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final CreateNewRepositoryFolderDialog newFolderDialog =
          new CreateNewRepositoryFolderDialog( RepositoryTreeDialog.this );

      if ( !newFolderDialog.performEdit() ) {
        return;
      }

      final TreePath selectionPath = repositoryBrowser.getSelectionPath();
      if ( selectionPath == null ) {
        return;
      }

      final FileObject treeNode = (FileObject) selectionPath.getLastPathComponent();
      if ( !StringUtils.isEmpty( newFolderDialog.getName() ) ) {
        final Component glassPane = SwingUtilities.getRootPane( RepositoryTreeDialog.this ).getGlassPane();
        try {
          glassPane.setVisible( true );
          glassPane.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
          final FileObject child = treeNode.resolveFile( newFolderDialog.getFolderName() );
          if ( child instanceof WebSolutionFileObject ) {
            final WebSolutionFileObject webSolutionFileObject = (WebSolutionFileObject) child;
            webSolutionFileObject.setDescription( newFolderDialog.getDescription() );
          }
          child.createFolder();
          repositoryTreeModel.fireTreeDataChanged();
          repositoryBrowser.setSelectionPath( selectionPath.getParentPath().pathByAddingChild( child ) );
          setDirty( true );
        } catch ( Exception e1 ) {
          UncaughtExceptionsModel.getInstance().addException( e1 );
        } finally {
          glassPane.setVisible( false );
          glassPane.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
        }
      }
    }
  }

  private RepositoryTreeModel repositoryTreeModel;
  private JTree repositoryBrowser;
  private boolean dirty;
  private boolean addNewButtonPanel;

  public RepositoryTreeDialog( final JDialog dialog, final boolean addNewButtonPanel ) {
    super( dialog );
    init( addNewButtonPanel );
  }

  public RepositoryTreeDialog( final boolean addNewButtonPanel ) {
    init( addNewButtonPanel );
  }

  public RepositoryTreeDialog( final Frame owner, final boolean addNewButtonPanel ) {
    super( owner );
    init( addNewButtonPanel );
  }

  protected void init( final boolean addNewButtonPanel ) {

    setModal( true );
    setTitle( Messages.getInstance().getString( "SolutionRepositoryTreeDialog.Title" ) );

    this.addNewButtonPanel = addNewButtonPanel;

    this.repositoryTreeModel = new RepositoryTreeModel();

    repositoryBrowser = new JTree( repositoryTreeModel );
    repositoryBrowser.setCellRenderer( new RepositoryTreeCellRenderer() );
    repositoryBrowser.addMouseListener( new DoubleClickHandler() );
    repositoryBrowser.setRootVisible( true );
    repositoryBrowser.setShowsRootHandles( true );
    repositoryBrowser.setToggleClickCount( 1 );

    super.init();
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
  }

  protected String getDialogId() {
    return "ReportDesigner.Pentaho.RepositoryTree";
  }

  public FileObject getSelectedPath() {
    final TreePath selectionPath = repositoryBrowser.getSelectionPath();
    if ( selectionPath == null ) {
      return null;
    }
    if ( selectionPath.getLastPathComponent() instanceof FileObject ) {
      return (FileObject) selectionPath.getLastPathComponent();
    }
    return null;
  }

  public void setSelectedPath( final FileObject path ) throws FileSystemException {
    final TreePath path1 = repositoryTreeModel.getTreePathForSelection( path, null );
    if ( path1 != null ) {
      repositoryBrowser.setSelectionPath( path1 );
      repositoryBrowser.scrollPathToVisible( path1 );
    } else {
      repositoryBrowser.clearSelection();
    }
  }

  public FileObject performSelectLocation( final FileObject fileSystemRoot, final String[] filter,
      final FileObject publishLocation ) throws FileSystemException {
    repositoryTreeModel.setShowFoldersOnly( true );
    repositoryTreeModel.setFileSystemRoot( fileSystemRoot );
    repositoryTreeModel.setFilters( filter );
    // repositoryBrowser.setModel(null);
    // repositoryBrowser.setModel(repositoryTreeModel);
    repositoryTreeModel.fireTreeDataChanged();

    dirty = false;
    setSelectedPath( publishLocation );
    if ( performEdit() ) {
      return getSelectedPath();
    }
    return null;
  }

  protected void setDirty( final boolean dirty ) {
    this.dirty = dirty;
  }

  public boolean isDirty() {
    return dirty;
  }

  /**
   * @noinspection ReuseOfLocalVariable
   */
  protected Component createContentPane() {
    final JScrollPane treeView = new JScrollPane( repositoryBrowser );
    final JPanel newFolderButtonPanel = new JPanel( new BorderLayout() );

    final JPanel panel = new JPanel();
    panel.setLayout( new GridBagLayout() );
    GridBagConstraints c = new GridBagConstraints();
    if ( addNewButtonPanel ) {
      final JButton newFolder = new JButton( new NewFolderAction() );
      newFolder.setBorder( BorderFactory.createEmptyBorder() );
      final JLabel label =
          new JLabel( Messages.getInstance().getString( "SolutionRepositoryTreeDialog.SelectLocation" ) );
      label.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 80 ) );
      newFolderButtonPanel.add( label, BorderLayout.CENTER );
      newFolderButtonPanel.add( newFolder, BorderLayout.EAST );

      c.insets = new Insets( 2, 10, 0, 10 );
      c.anchor = GridBagConstraints.WEST;
      c.gridx = 0;
      c.gridy = 0;
      c.fill = GridBagConstraints.HORIZONTAL;
      panel.add( newFolderButtonPanel, c );
    }

    c = new GridBagConstraints();
    c.insets = new Insets( 0, 10, 5, 10 );
    c.gridx = 0;
    c.gridy = 1;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1.0;
    c.weighty = 1.0;
    panel.add( treeView, c );

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets( 0, 10, 5, 10 );
    c.gridx = 0;
    c.gridy = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    panel.add( new JCheckBox( new ShowHiddenFilesAction() ) );
    return panel;
  }

}
