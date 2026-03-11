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


package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.BrowserLoginHandler;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProvider;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.GenericCancelHandler;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.pensol.WebSolutionFileObject;
import org.pentaho.reporting.libraries.pensol.JCRSolutionFileSystem;
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

import javax.swing.JOptionPane;

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
  private AuthenticationData currentLoginData;
  private boolean sessionCheckInProgress;

  /** Listener notified when a re-login completes so the caller can update its own loginData reference. */
  public interface ReLoginListener {
    void onReLoginSuccess( AuthenticationData newLoginData );
  }

  private ReLoginListener reLoginListener;

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

      // After the table loads, if it is empty check whether the session expired
      checkForExpiredSession();
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

  public void setReLoginListener( final ReLoginListener listener ) {
    this.reLoginListener = listener;
  }

  public String performOpen( final AuthenticationData loginData, final String previousSelection )
    throws FileSystemException, UnsupportedEncodingException {
    this.currentLoginData = loginData;
    connectAndNavigate( loginData, previousSelection );

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

  /**
   * Establishes the VFS connection and navigates to the given path.
   */
  protected void connectAndNavigate( final AuthenticationData loginData, final String previousSelection )
    throws FileSystemException {
    fileSystemRoot = PublishUtil.createVFSConnection( VFS.getManager(), loginData );

    // Always refresh to get latest files from server
    try {
      if ( fileSystemRoot.getFileSystem() instanceof JCRSolutionFileSystem ) {
        final JCRSolutionFileSystem fileSystem = (JCRSolutionFileSystem) fileSystemRoot.getFileSystem();
        fileSystem.getLocalFileModel().refresh();
      } else if ( fileSystemRoot.getFileSystem() instanceof WebSolutionFileSystem ) {
        final WebSolutionFileSystem fileSystem = (WebSolutionFileSystem) fileSystemRoot.getFileSystem();
        fileSystem.getLocalFileModel().refresh();
      }
      fileSystemRoot.refresh();
    } catch ( Exception e ) {
      logger.debug( "Failed to refresh file system", e );
      // Re-throw authentication errors only for SSO sessions so the caller
      // can offer re-login.  For username/password sessions a 401 simply
      // means wrong credentials — let the table stay empty.
      if ( AuthenticationHelper.isAuthenticationError( e )
          && AuthenticationHelper.isBrowserAuth( loginData ) ) {
        throw new FileSystemException( "vfs.provider/connect.error", e );
      }
    }

    if ( previousSelection == null ) {
      setSelectedView( fileSystemRoot );
    } else {
      try {
        final FileObject view = fileSystemRoot.resolveFile( previousSelection );
        if ( view == null || !view.exists() ) {
          setSelectedView( fileSystemRoot );
        } else if ( view.getType() == FileType.FOLDER ) {
          setSelectedView( view );
        } else {
          setSelectedView( view.getParent() );
        }
      } catch ( FileSystemException e ) {
        logger.debug( "Could not resolve previous selection, falling back to root", e );
        setSelectedView( fileSystemRoot );
      }
    }
  }

  /**
   * Called after folder navigation when the table is empty.
   * Performs a lightweight HTTP check to see if the session expired.
   * Only triggers re-login when the server confirms 401/403.
   * <p>
   * When the table has rows, the connection was successful and no check is
   * needed.  The primary expired-session detection for cached VFS data lives
   * in {@link #connectAndNavigate}, which re-throws authentication errors
   * for SSO sessions so that the caller can offer re-login.
   */
  private void checkForExpiredSession() {
    // Session-expiry detection is only relevant for SSO (browser-auth) sessions.
    // Username/password sessions use per-request Basic Auth and have no
    // expiring token, so the HTTP check would falsely return 401.
    if ( !AuthenticationHelper.isBrowserAuth( currentLoginData ) ) {
      return;
    }
    if ( sessionCheckInProgress || currentLoginData == null || table.getRowCount() > 0 ) {
      return;
    }
    sessionCheckInProgress = true;
    try {
      if ( !isSessionStillActive() ) {
        handleSessionExpired();
      }
    } finally {
      sessionCheckInProgress = false;
    }
  }

  /**
   * Checks whether the server session is still valid by making a direct
   * HTTP request.  Returns {@code true} unless the server responds with
   * 401 or 403.  Connection errors are treated as "still valid" to
   * avoid false positives.
   */
  private boolean isSessionStillActive() {
    if ( currentLoginData == null || currentLoginData.getUrl() == null ) {
      return true;
    }
    try {
      final HttpClient client = SessionAuthenticationUtil.createSessionAuthenticatedClient(
          currentLoginData, currentLoginData.getUrl(), 5000 );
      String checkUrl = currentLoginData.getUrl();
      if ( checkUrl.endsWith( "/" ) ) {
        checkUrl = checkUrl.substring( 0, checkUrl.length() - 1 );
      }
      checkUrl += "/api/repo/files/tree?depth=0";
      final HttpGet request = new HttpGet( checkUrl );
      final HttpResponse response = client.execute( request );
      final int status = response.getStatusLine().getStatusCode();
      return status != 401 && status != 403;
    } catch ( Exception e ) {
      logger.debug( "Session validation request failed — assuming still active", e );
      return true;
    }
  }

  /**
   * Shows the session-expired dialog and, on success, reconnects the VFS
   * and navigates back to the path where the user left off.
   */
  protected void handleSessionExpired() {
    final String currentPath = getCurrentPath();

    final String[] options = {
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.LoginAgain" ),
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.Cancel" )
    };
    final int choice = JOptionPane.showOptionDialog( this,
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.Message" ),
        Messages.getInstance().getString( "RepositoryOpenDialog.SessionExpired.Title" ),
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0] );

    if ( choice != JOptionPane.YES_OPTION ) {
      return;
    }

    final AuthenticationData newLoginData = showReLoginDialog();
    if ( newLoginData == null ) {
      return;
    }

    this.currentLoginData = newLoginData;

    // Keep the global session cache in sync so subsequent operations use the refreshed credentials.
    RepositorySessionManager.getInstance().setSession( newLoginData, newLoginData.getUsername() );

    if ( reLoginListener != null ) {
      reLoginListener.onReLoginSuccess( newLoginData );
    }

    try {
      connectAndNavigate( newLoginData, currentPath );
      table.refresh();
    } catch ( Exception ex ) {
      logger.error( "Failed to reconnect after re-login", ex );
      UncaughtExceptionsModel.getInstance().addException( ex );
    }
  }

  /**
   * Returns the decoded path of the currently selected view, or {@code null}.
   */
  protected String getCurrentPath() {
    if ( selectedView == null ) {
      return null;
    }
    try {
      return selectedView.getName().getPathDecoded();
    } catch ( FileSystemException e ) {
      logger.debug( "Unable to decode current path", e );
      return null;
    }
  }

  /**
   * Shows the standard Pentaho login dialog for re-authentication.
   * If the user selects SSO, the browser login flow is triggered
   * (identical to the initial login in {@code LoginTask}).
   *
   * @return new {@link AuthenticationData} on success, or {@code null} if cancelled
   */
  protected AuthenticationData showReLoginDialog() {
    final RepositoryLoginDialog loginDialog;
    if ( getOwner() instanceof Frame ) {
      loginDialog = new RepositoryLoginDialog( (Frame) getOwner(), false );
    } else if ( getOwner() instanceof Dialog ) {
      loginDialog = new RepositoryLoginDialog( (Dialog) getOwner(), false );
    } else {
      loginDialog = new RepositoryLoginDialog( false );
    }

    // Show SSO-only re-login when the expired session was SSO,
    // otherwise show the classic username/password dialog.
    if ( AuthenticationHelper.isBrowserAuth( currentLoginData ) ) {
      loginDialog.setDialogMode( RepositoryLoginDialog.DialogMode.SSO_ONLY );
    } else {
      loginDialog.setDialogMode( RepositoryLoginDialog.DialogMode.CREDENTIALS_ONLY );
    }

    final AuthenticationData basicData = loginDialog.performReLogin( currentLoginData );
    if ( basicData == null ) {
      return null;
    }
    if ( loginDialog.getLoginMethod() == RepositoryLoginDialog.LoginMethod.SSO ) {
      return performBrowserReLogin( basicData.getUrl(), loginDialog.getSelectedOAuthProvider() );
    }
    return basicData;
  }

  /**
   * Performs the SSO browser-based login flow during re-authentication.
   * Opens the system browser, waits for the authentication callback,
   * and returns credentials that include a valid session ID.
   *
   * @param serverUrl     the Pentaho server URL
   * @param oauthProvider the selected OAuth provider, or {@code null}
   * @return authenticated {@link AuthenticationData} or {@code null} if cancelled/failed
   */
  private AuthenticationData performBrowserReLogin( final String serverUrl, final OAuthProvider oauthProvider ) {
    while ( true ) {
      final AuthenticationData[] result = new AuthenticationData[1];
      final Thread browserLoginThread = new Thread( () -> {
        final BrowserLoginHandler handler = new BrowserLoginHandler();
        if ( oauthProvider != null ) {
          handler.setOAuthProvider( oauthProvider );
        }
        result[0] = handler.startBrowserLogin( serverUrl );
      } );
      browserLoginThread.setDaemon( true );
      browserLoginThread.setName( "BrowserReLoginThread" );

      final GenericCancelHandler cancelHandler = new GenericCancelHandler( browserLoginThread );
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog(
          browserLoginThread, cancelHandler, this,
          Messages.getInstance().getString( "LoginTask.BrowserLogin.WaitingMessage" ) );

      if ( cancelHandler.isCancelled() ) {
        return null;
      }
      if ( result[0] != null ) {
        return result[0];
      }

      final int choice = JOptionPane.showOptionDialog( this,
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Failed.Message" ),
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Error.Title" ),
          JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
          new String[] {
              Messages.getInstance().getString( "LoginTask.BrowserLogin.Retry" ),
              Messages.getInstance().getString( "LoginTask.BrowserLogin.Cancel" )
          },
          Messages.getInstance().getString( "LoginTask.BrowserLogin.Retry" ) );
      if ( choice != JOptionPane.YES_OPTION ) {
        return null;
      }
    }
  }

  protected AuthenticationData getCurrentLoginData() {
    return currentLoginData;
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
    // When the user clicks OK, verify that an SSO session is still valid.
    // If the session expired while the dialog was open, trigger re-login
    // and keep the dialog open so the user can retry.
    if ( onConfirm && AuthenticationHelper.isBrowserAuth( currentLoginData ) && !isSessionStillActive() ) {
      handleSessionExpired();
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
