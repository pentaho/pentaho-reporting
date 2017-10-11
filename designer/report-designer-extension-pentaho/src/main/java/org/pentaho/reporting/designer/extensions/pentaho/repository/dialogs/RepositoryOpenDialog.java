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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.pensol.WebSolutionFileObject;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

public class RepositoryOpenDialog extends CommonDialog {

  protected class LevelUpAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public LevelUpAction() {
      putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString(
          "RepositoryPublishDialog.LevelUpAction.Description" ) );
      final URL location =
          RepositoryOpenDialog.class
              .getResource( "/org/pentaho/reporting/designer/extensions/pentaho/repository/resources/upOneFolder.png" ); // NON-NLS
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getInstance().getString( "RepositoryPublishDialog.LevelUpAction.Name" ) );
      }
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( fileSystemRoot == null ) {
        return;
      }
      if ( fileSystemRoot.equals( selectedView ) ) {
        return;
      }
      try {
        setSelectedView( selectedView.getParent() );
      } catch ( FileSystemException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
    }
  }

  protected class BrowseRepositoryAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public BrowseRepositoryAction() {
      putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString(
          "RepositoryPublishDialog.BrowseRepositoryAction.Description" ) );
      final URL location =
          RepositoryOpenDialog.class
              .getResource( "/org/pentaho/reporting/designer/extensions/pentaho/repository/resources/exploreSolution.png" ); // NON-NLS
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getInstance().getString( "RepositoryPublishDialog.BrowseRepositoryAction.Name" ) );
      }
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( fileSystemRoot == null ) {
        return;
      }

      final RepositoryTreeDialog treeDialog =
          new RepositoryTreeDialog( RepositoryOpenDialog.this, isCreateFolderAllowed() );
      try {
        final FileObject newLocation = treeDialog.performSelectLocation( fileSystemRoot, getFilters(), selectedView );
        if ( newLocation != null ) {
          setSelectedView( newLocation );
        }

        if ( treeDialog.isDirty() ) {
          table.refresh();
        }
      } catch ( FileSystemException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
    }
  }

  private class TableInputHandler extends MouseAdapter implements KeyListener, ListSelectionListener {
    private TableInputHandler() {
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link java.awt.event.KeyEvent} for a definition
     * of a key typed event.
     */
    public void keyTyped( final KeyEvent e ) {

    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link java.awt.event.KeyEvent} for a
     * definition of a key pressed event.
     */
    public void keyPressed( final KeyEvent e ) {
      if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
        final int selectedRowRaw = table.getSelectedRow();
        if ( selectedRowRaw == -1 ) {
          return;
        }
        final int selectedRow = table.convertRowIndexToModel( selectedRowRaw );
        final FileObject selectedFileObject = table.getSelectedFileObject( selectedRow );
        if ( selectedFileObject == null ) {
          return;
        }

        try {
          if ( FileType.FOLDER.equals( selectedFileObject.getType() ) ) {
            setSelectedView( selectedFileObject );
          }
        } catch ( FileSystemException e1 ) {
          // ignore ..
        }
      }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link java.awt.event.KeyEvent} for a
     * definition of a key released event.
     */
    public void keyReleased( final KeyEvent e ) {

    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked( final MouseEvent e ) {
      if ( e.getButton() != 1 ) {
        return;
      }
      if ( e.getClickCount() < 2 ) {
        return;
      }

      final int selectedRowRaw = table.getSelectedRow();
      if ( selectedRowRaw == -1 ) {
        return;
      }
      final int selectedRow = table.convertRowIndexToModel( selectedRowRaw );

      final FileObject selectedFileObject = table.getSelectedFileObject( selectedRow );
      if ( selectedFileObject == null ) {
        return;
      }

      try {
        if ( FileType.FOLDER.equals( selectedFileObject.getType() ) ) {
          setSelectedView( selectedFileObject );
        } else if ( FileType.FILE.equals( selectedFileObject.getType() ) ) {
          if ( isDoubleClickConfirmsDialog() == false ) {
            return;
          }

          setConfirmed( true );
          setVisible( false );
        }
      } catch ( FileSystemException e1 ) {
        // ignore ..
      }
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e
     *          the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      final int selectedRowRaw = table.getSelectedRow();
      if ( selectedRowRaw == -1 ) {
        return;
      }
      final int selectedRow = table.convertRowIndexToModel( selectedRowRaw );
      final FileObject selectedFileObject = table.getSelectedFileObject( selectedRow );
      if ( selectedFileObject == null ) {
        return;
      }

      try {
        if ( selectedFileObject.getType() == FileType.FILE ) {
          fileNameTextField.setText( URLDecoder.decode( selectedFileObject.getName().getBaseName().replaceAll( "\\+",
              "%2B" ), "UTF-8" ) );
        }
      } catch ( FileSystemException e1 ) {
        // ignore ..
      } catch ( UnsupportedEncodingException e1 ) {
        // ignore ..
      }
    }
  }

  private static class FileObjectRenderer extends DefaultListCellRenderer {
    private FileObjectRenderer() {
    }

    public Component getListCellRendererComponent( final JList list, final Object value, final int index,
        final boolean isSelected, final boolean cellHasFocus ) {
      if ( value instanceof WebSolutionFileObject ) {
        final WebSolutionFileObject fo = (WebSolutionFileObject) value;
        final FileName fileName = fo.getName();
        try {
          return super.getListCellRendererComponent( list, fileName.getPathDecoded(), index, isSelected, cellHasFocus );
        } catch ( FileSystemException e ) {
          // ignored ..
        }
      }
      return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    }
  }

  private class FileNameValidator extends DocumentChangeHandler {
    private FileNameValidator() {
    }

    protected void handleChange( final DocumentEvent e ) {
      getConfirmAction().setEnabled( validateInputs( false ) );
    }
  }

  private class SelectLocationAction implements ActionListener {
    private SelectLocationAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( locationCombo.getSelectedItem() instanceof FileObject ) {
        final FileObject selectedItem = (FileObject) locationCombo.getSelectedItem();
        if ( selectedItem.equals( getSelectedView() ) == false ) {
          setSelectedView( selectedItem );
        }
      }
    }
  }

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
      table.setShowHiddenFiles( Boolean.TRUE.equals( getSelected() ) );
    }
  }

  private static final String[] REPORT_FILTER = new String[] { ".prpt", ".report", ".prpti" };
  private static final Log logger = LogFactory.getLog( RepositoryOpenDialog.class );

  private RepositoryTable table;
  private JTextField fileNameTextField;
  private JComboBox locationCombo;
  private FileObject fileSystemRoot;
  private FileObject selectedView;

  public RepositoryOpenDialog() {
    init();
  }

  public RepositoryOpenDialog( final Frame owner ) {
    super( owner );
    init();
  }

  public RepositoryOpenDialog( final Dialog owner ) {
    super( owner );
    init();
  }

  public void init() {
    locationCombo = new JComboBox();
    locationCombo.setRenderer( new FileObjectRenderer() );
    locationCombo.addActionListener( new SelectLocationAction() );

    fileNameTextField = new JTextField();
    fileNameTextField.getDocument().addDocumentListener( new FileNameValidator() );

    table = new RepositoryTable();
    table.setFilters( REPORT_FILTER );
    table.addKeyListener( new TableInputHandler() );
    table.addMouseListener( new TableInputHandler() );
    table.getSelectionModel().addListSelectionListener( new TableInputHandler() );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Pentaho.RepositoryOpen";
  }

  public String[] getFilters() {
    return table.getFilters();
  }

  public void setFilters( final String[] filters ) {
    table.setFilters( filters );
  }

  protected FileObject getSelectedView() {
    return selectedView;
  }

  public void setSelectedView( final FileObject selectedView ) {
    this.selectedView = selectedView;
    if ( selectedView != null ) {
      logger.debug( "Setting selected view to " + selectedView );
      try {
        if ( selectedView.getType() == FileType.FILE ) {
          logger.debug( "Setting filename in selected view to " + selectedView.getName().getBaseName() );
          this.fileNameTextField.setText( URLDecoder.decode( selectedView.getName().getBaseName(), "UTF-8" ) );
        }
      } catch ( Exception e ) {
        // can be ignored ..
        logger.debug( "Unable to determine file type. This is not fatal.", e );
      }
      final ComboBoxModel comboBoxModel = createLocationModel( selectedView );
      this.locationCombo.setModel( comboBoxModel );
      this.table.setSelectedPath( (FileObject) comboBoxModel.getSelectedItem() );
    } else {
      this.fileNameTextField.setText( null );
      this.table.setSelectedPath( null );
      this.locationCombo.setModel( new DefaultComboBoxModel() );
    }
  }

  private ComboBoxModel createLocationModel( final FileObject selectedFolder ) {
    if ( fileSystemRoot == null ) {
      return new DefaultComboBoxModel();
    }

    try {
      final ArrayList<FileObject> list = new ArrayList<FileObject>();
      FileObject folder = selectedFolder;
      while ( folder != null ) {
        if ( fileSystemRoot.equals( folder ) ) {
          break;
        }

        if ( folder.getType() != FileType.FILE ) {
          list.add( folder );
        }

        final FileObject parent = folder.getParent();
        if ( folder.equals( parent ) ) {
          // protect yourself against infinite loops ..
          break;
        }
        folder = parent;
      }
      list.add( fileSystemRoot );
      final DefaultComboBoxModel model = new DefaultComboBoxModel( list.toArray() );
      model.setSelectedItem( list.get( 0 ) );
      return model;
    } catch ( FileSystemException e ) {
      return new DefaultComboBoxModel();
    }
  }

  public String performOpen( final AuthenticationData loginData, final String previousSelection )
    throws FileSystemException, UnsupportedEncodingException {
    fileSystemRoot = PublishUtil.createVFSConnection( VFS.getManager(), loginData );
    if ( previousSelection == null ) {
      setSelectedView( fileSystemRoot );
    } else {
      final FileObject view = fileSystemRoot.resolveFile( previousSelection );
      if ( view == null ) {
        setSelectedView( fileSystemRoot );
      } else {
        if ( view.exists() == false ) {
          setSelectedView( fileSystemRoot );
        } else if ( view.getType() == FileType.FOLDER ) {
          setSelectedView( view );
        } else {
          setSelectedView( view.getParent() );
        }
      }
    }

    if ( StringUtils.isEmpty( fileNameTextField.getText(), true ) && previousSelection != null ) {
      final String fileName = IOUtils.getInstance().getFileName( previousSelection );
      DebugLog.log( "Setting filename to " + fileName );
      fileNameTextField.setText( fileName );
    }

    getConfirmAction().setEnabled( validateInputs( false ) );
    if ( super.performEdit() == false || selectedView == null ) {
      return null;
    }

    return getSelectedFile();
  }

  protected String getSelectedFile() throws FileSystemException, UnsupportedEncodingException {
    if ( StringUtils.isEmpty( fileNameTextField.getText() ) ) {
      return null;
    }

    if ( selectedView.getType() == FileType.FILE ) {
      selectedView = selectedView.getParent();
    }

    final FileObject targetFile =
        selectedView.resolveFile( fileNameTextField.getText().replaceAll( "\\%", "%25" ).replaceAll( "\\!", "%21" )
            .replaceAll( ":", "%3A" ) );
    return targetFile.getName().getPathDecoded();
  }

  protected RepositoryTable getTable() {
    return table;
  }

  protected JTextField getFileNameTextField() {
    return fileNameTextField;
  }

  protected JComboBox getLocationCombo() {
    return locationCombo;
  }

  protected Component createContentPane() {
    final JPanel centerCarrier = new JPanel();
    centerCarrier.setLayout( new BorderLayout() );
    centerCarrier.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    centerCarrier.add( new JScrollPane( table ), BorderLayout.CENTER );
    centerCarrier.add( new JCheckBox( new ShowHiddenFilesAction() ), BorderLayout.SOUTH );

    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout( new BorderLayout() );
    contentPanel.add( centerCarrier, BorderLayout.CENTER );
    contentPanel.add( createHeaderPanel(), BorderLayout.NORTH );
    return contentPanel;
  }

  protected boolean isCreateFolderAllowed() {
    return false;
  }

  protected boolean isDoubleClickConfirmsDialog() {
    return true;
  }

  protected JPanel createHeaderPanel() {
    final JPanel publishHeaderPanel = new JPanel( new GridBagLayout() );

    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets( 5, 5, 5, 5 );
    publishHeaderPanel.add( new JLabel( Messages.getInstance().getString( "RepositoryPublishDialog.ReportName" ) ), c );

    c.gridy = 1;
    c.insets = new Insets( 2, 5, 0, 5 );
    publishHeaderPanel.add( fileNameTextField, c );

    c.gridy = 4;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets( 2, 5, 0, 5 );
    publishHeaderPanel.add( new JLabel( Messages.getInstance().getString( "RepositoryPublishDialog.Location" ) ), c );

    c.insets = new Insets( 0, 0, 0, 0 );
    c.gridx = 0;
    c.gridy = 5;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.WEST;
    publishHeaderPanel.add( createLocationFieldPanel(), c );
    return publishHeaderPanel;
  }

  protected JPanel createLocationFieldPanel() {
    final GridBagConstraints c = new GridBagConstraints();
    final JPanel locationFieldPanel = new JPanel();
    locationFieldPanel.setLayout( new GridBagLayout() );
    c.insets = new Insets( 0, 5, 5, 0 );
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.WEST;
    locationFieldPanel.add( locationCombo, c );

    c.insets = new Insets( 5, 8, 5, 0 );
    c.gridx = 1;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    c.anchor = GridBagConstraints.WEST;
    locationFieldPanel.add( new BorderlessButton( new LevelUpAction() ), c );

    c.gridx = 2;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    c.anchor = GridBagConstraints.EAST;
    locationFieldPanel.add( new BorderlessButton( new BrowseRepositoryAction() ), c );
    return locationFieldPanel;
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    if ( StringUtils.isEmpty( fileNameTextField.getText() ) ) {
      return false;
    }
    return true;
  }

  public void refresh() throws IOException {
    final WebSolutionFileSystem fileSystem = (WebSolutionFileSystem) this.fileSystemRoot.getFileSystem();
    fileSystem.getLocalFileModel().refresh();
    table.refresh();
  }

}
