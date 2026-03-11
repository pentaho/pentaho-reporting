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

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishSettings;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class RepositoryLoginDialog extends CommonDialog {
  private class URLChangeHandler implements ActionListener {
    private URLChangeHandler() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final String serverURL = (String) urlCombo.getSelectedItem();
      final AuthenticationData config = getStoredLoginData( serverURL, context );
      if ( config != null ) {
        timeoutField.setValue( PublishUtil.getTimeout( config ) );
        userField.setText( config.getUsername() );
        userPasswordField.setText( config.getPassword() );
      }
    }
  }

  public enum LoginMethod {
    SSO,
    USERNAME_PASSWORD
  }

  private JComboBox urlCombo;
  private JSpinner timeoutField;
  private JTextField userField;
  private JPasswordField userPasswordField;
  private JCheckBox rememberSettings;
  private JRadioButton ssoRadio;
  private JRadioButton usernamePasswordRadio;
  private JPanel userPanel;
  private LoginMethod selectedLoginMethod = LoginMethod.USERNAME_PASSWORD;
  private ReportDesignerContext context;
  private DefaultComboBoxModel urlModel;
  private boolean loginForPublish;

  public RepositoryLoginDialog( final Dialog owner, final boolean loginForPublish ) throws HeadlessException {
    super( owner );
    init( loginForPublish );
  }

  public RepositoryLoginDialog( final Frame parent, final boolean loginForPublish ) {
    super( parent );
    init( loginForPublish );
  }

  public RepositoryLoginDialog( final boolean loginForPublish ) {
    init( loginForPublish );
  }

  public static AuthenticationData getDefaultData( final ReportDesignerContext designerContext ) {
    final GlobalAuthenticationStore authStore = designerContext.getGlobalAuthenticationStore();
    final String rurl = authStore.getMostRecentEntry();
    if ( rurl != null ) {
      final AuthenticationData loginData = getStoredLoginData( rurl, designerContext );
      if ( loginData != null ) {
        return loginData;
      }
    }

    final String user =
        ReportDesignerBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.designer.extensions.pentaho.repository.ServerUser" );
    final String pass =
        ReportDesignerBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.designer.extensions.pentaho.repository.ServerPassword" );
    final String url =
        ReportDesignerBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.designer.extensions.pentaho.repository.PublishLocation" );

    if ( StringUtils.isEmpty( url ) ) {
      return null;
    }
    return new AuthenticationData( url, user, pass, WorkspaceSettings.getInstance().getConnectionTimeout() );
  }

  public static AuthenticationData getStoredLoginData( final String baseUrl, final ReportDesignerContext context ) {
    final ReportDocumentContext reportRenderContext = context.getActiveContext();
    final AuthenticationStore authStore;
    if ( reportRenderContext == null ) {
      authStore = context.getGlobalAuthenticationStore();
    } else {
      authStore = reportRenderContext.getAuthenticationStore();
    }

    final AuthenticationData data = authStore.getCredentials( baseUrl );
    if ( data == null ) {
      return null;
    }
    return data;
  }

  /**
   * Shows the login dialog and returns authentication data if user confirms.
   * 
   * @param context The designer context
   * @param config Initial authentication config (can be null)
   * @return AuthenticationData or null if cancelled
   */
  public AuthenticationData performLogin( final ReportDesignerContext context, AuthenticationData config ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    this.context = context;
    
    if ( config == null ) {
      config = getDefaultData( context );
    }

    urlModel.removeAllElements();
    final String[] urls;
    final ReportDocumentContext reportRenderContext = context.getActiveContext();
    if ( reportRenderContext == null ) {
      urls = context.getGlobalAuthenticationStore().getKnownURLs();
    } else {
      urls = reportRenderContext.getAuthenticationStore().getKnownURLs();
    }
    for ( int i = 0; i < urls.length; i++ ) {
      urlModel.addElement( urls[i] );
    }

    rememberSettings.setSelected( PublishSettings.getInstance().isRememberSettings() );

    if ( config != null ) {
      timeoutField.setValue( PublishUtil.getTimeout( config ) );
      urlCombo.setSelectedItem( config.getUrl() );
      userField.setText( config.getUsername() );
      userPasswordField.setText( config.getPassword() );
      
      // Check if this is browser auth
      boolean isBrowserAuth = "true".equals( config.getOption( "browserAuth" ) );
      if ( isBrowserAuth ) {
        ssoRadio.setSelected( true );
        selectedLoginMethod = LoginMethod.SSO;
        userPanel.setVisible( false );
      } else {
        usernamePasswordRadio.setSelected( true );
        selectedLoginMethod = LoginMethod.USERNAME_PASSWORD;
        userPanel.setVisible( true );
      }
    } else {
      timeoutField.setValue( WorkspaceSettings.getInstance().getConnectionTimeout() );
      urlCombo.setSelectedItem( null );
      userField.setText( null );
      userPasswordField.setText( null );
      usernamePasswordRadio.setSelected( true );
      selectedLoginMethod = LoginMethod.USERNAME_PASSWORD;
      userPanel.setVisible( true );
    }

    // Repack the dialog to fit the current visibility state of panels.
    // This is necessary because a previously saved dialog size (e.g. from SSO mode where userPanel
    // was hidden) may be too small for username/password mode, causing fields to overlap on first open.
    pack();

    // Show the traditional username/password login dialog
    if ( !super.performEdit() ) {
      return null;
    }

    urlCombo.getModel().setSelectedItem( urlCombo.getEditor().getItem() );

    final String url = getServerURL();
    if ( url == null ) {
      return null;
    }

    PublishSettings.getInstance().setRememberSettings( isRememberSettings() );
    final AuthenticationData data = new AuthenticationData( url, getUsername(), getUserPassword(), getTimeout() );
    data.setOption( PublishUtil.SERVER_VERSION, String.valueOf( getVersion() ) );
    return data;
  }

  protected void init( final boolean loginForPublish ) {
    setTitle( Messages.getInstance().getString( "RepositoryLoginDialog.Title" ) );

    this.loginForPublish = loginForPublish;

    urlModel = new DefaultComboBoxModel();
    urlCombo = new JComboBox( urlModel );

    userField = new JTextField( 25 );
    userPasswordField = new JPasswordField( 25 );

    final SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
    spinnerModel.setMinimum( 0 );
    spinnerModel.setMaximum( 99999 );

    timeoutField = new JSpinner( spinnerModel );
    timeoutField.setEditor( new JSpinner.NumberEditor( timeoutField, "#####" ) );

    // Create radio buttons for login method
    ssoRadio = new JRadioButton( Messages.getInstance().getString( "RepositoryLoginDialog.LoginWithSSO" ) );
    usernamePasswordRadio = new JRadioButton( 
        Messages.getInstance().getString( "RepositoryLoginDialog.LoginWithUsernamePassword" ), true );
    
    ButtonGroup loginMethodGroup = new ButtonGroup();
    loginMethodGroup.add( ssoRadio );
    loginMethodGroup.add( usernamePasswordRadio );
    
    // Add listeners to radio buttons
    ssoRadio.addActionListener( e -> handleLoginMethodChange( LoginMethod.SSO, false ) );
    usernamePasswordRadio.addActionListener( e -> handleLoginMethodChange( LoginMethod.USERNAME_PASSWORD, true ) );

    rememberSettings =
        new JCheckBox( Messages.getInstance().getString( "RepositoryLoginDialog.RememberTheseSettings" ), true );

    urlCombo.setEditable( true );
    urlCombo.addActionListener( new URLChangeHandler() );

    userField.setAction( getConfirmAction() );
    userPasswordField.setAction( getConfirmAction() );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Pentaho.RepositoryLogin";
  }

  protected Component createContentPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );

    final GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets( 10, 10, 5, 10 );
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.WEST;
    contentPane.add( buildServerPanel(), c );

    c.gridy = 1;
    c.insets = new Insets( 0, 10, 5, 10 );
    contentPane.add( buildLoginMethodPanel(), c );

    c.gridy = 2;
    c.insets = new Insets( 0, 10, 5, 10 );
    userPanel = buildUserPanel();
    userPanel.setVisible( true ); // Initially visible (username/password selected by default)
    contentPane.add( userPanel, c );

    c.gridy = 3;
    contentPane.add( rememberSettings, c );

    return contentPane;
  }

  private JPanel buildServerPanel() {
    final JPanel serverPanel = new JPanel( new GridBagLayout() );
    final GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets( 0, 20, 5, 20 );
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    serverPanel.add( new JLabel( Messages.getInstance().getString( "RepositoryLoginDialog.URL" ) ), c );

    c.gridy = 1;
    c.insets = new Insets( 0, 20, 10, 20 );
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    serverPanel.add( urlCombo, c );

    serverPanel.setBorder( BorderFactory.createTitledBorder( Messages.getInstance().getString(
        "RepositoryLoginDialog.Server" ) ) );
    return serverPanel;
  }

  private JPanel buildLoginMethodPanel() {
    final JPanel methodPanel = new JPanel( new GridBagLayout() );
    final GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets( 5, 20, 5, 20 );
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    methodPanel.add( ssoRadio, c );

    c.gridy = 1;
    c.insets = new Insets( 0, 20, 5, 20 );
    methodPanel.add( usernamePasswordRadio, c );

    methodPanel.setBorder( BorderFactory.createTitledBorder( 
        Messages.getInstance().getString( "RepositoryLoginDialog.LoginMethod" ) ) );
    return methodPanel;
  }

  private JPanel buildUserPanel() {
    final JPanel userPanel = new JPanel( new GridBagLayout() );
    userPanel.setBorder( BorderFactory.createTitledBorder( Messages.getInstance().getString(
        "RepositoryLoginDialog.PentahoCredentials" ) ) );
    final JLabel userLabel = new JLabel( Messages.getInstance().getString( "RepositoryLoginDialog.User" ) );
    final JLabel passwordLabel = new JLabel( Messages.getInstance().getString( "RepositoryLoginDialog.Password" ) );

    final GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets( 0, 20, 5, 20 );
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    userPanel.add( userLabel, c );

    c.gridy = 1;
    c.insets = new Insets( 0, 20, 0, 20 );
    userPanel.add( userField, c );

    c.gridy = 2;
    c.insets = new Insets( 0, 20, 0, 20 );
    userPanel.add( passwordLabel, c );

    c.gridy = 3;
    c.insets = new Insets( 0, 20, 10, 20 );
    userPanel.add( userPasswordField, c );
    return userPanel;
  }

  public String getServerURL() {
    final Object o = urlCombo.getSelectedItem();
    if ( o == null ) {
      return null;
    }
    return o.toString();
  }

  public int getVersion() {
    return 5;
  }

  public String getUsername() {
    return userField.getText();
  }

  public String getUserPassword() {
    return new String( userPasswordField.getPassword() );
  }

  public int getTimeout() {
    final Object timeout = timeoutField.getValue();
    if ( timeout instanceof Number ) {
      final Number number = (Number) timeout;
      return number.intValue();
    }
    return WorkspaceSettings.getInstance().getConnectionTimeout();
  }

  public boolean isRememberSettings() {
    return rememberSettings.isSelected();
  }

  public LoginMethod getLoginMethod() {
    return selectedLoginMethod;
  }

  /**
   * Handles login method selection changes (SSO or Username/Password).
   * Extracted to avoid code duplication in radio button listeners.
   */
  private void handleLoginMethodChange( final LoginMethod loginMethod, final boolean showUserPanel ) {
    selectedLoginMethod = loginMethod;
    userPanel.setVisible( showUserPanel );
    pack();
  }

}
