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
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProvider;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProviderService;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishSettings;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class RepositoryLoginDialog extends CommonDialog {
  private class URLChangeHandler implements ActionListener {
    private URLChangeHandler() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final String serverURL = (String) urlCombo.getSelectedItem();
      if ( context != null ) {
        final AuthenticationData config = getStoredLoginData( serverURL, context );
        if ( config != null ) {
          timeoutField.setValue( PublishUtil.getTimeout( config ) );
          if ( rememberSettings != null && rememberSettings.isSelected() ) {
            userField.setText( config.getUsername() );
            userPasswordField.setText( config.getPassword() );
          }
        }
      }
      fetchOAuthProviders();
    }
  }

  public enum LoginMethod {
    SSO,
    USERNAME_PASSWORD
  }

  public enum DialogMode {
    FULL,
    SSO_ONLY,
    CREDENTIALS_ONLY
  }

  private JComboBox urlCombo;
  private JSpinner timeoutField;
  private JTextField userField;
  private JPasswordField userPasswordField;
  private JCheckBox rememberSettings;
  private JRadioButton ssoRadio;
  private JRadioButton usernamePasswordRadio;
  private JPanel userPanel;
  private JPanel ssoProviderPanel;
  private JComboBox<OAuthProvider> providerCombo;
  private DefaultComboBoxModel<OAuthProvider> providerModel;
  private JLabel providerStatusLabel;
  private Timer providerFetchTimer;
  private transient List<OAuthProvider> cachedProviders = new ArrayList<>();
  private volatile String activeFetchUrl;
  private LoginMethod selectedLoginMethod = LoginMethod.USERNAME_PASSWORD;
  private transient OAuthProvider confirmedProvider;
  private DialogMode dialogMode = DialogMode.FULL;
  private ReportDesignerContext context;
  private DefaultComboBoxModel urlModel;
  private boolean loginForPublish;
  private JPanel loginMethodPanel;
  private JLabel timeoutLabel;

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

    final boolean remember = PublishSettings.getInstance().isRememberSettings();
    rememberSettings.setSelected( remember );

    // URL and timeout are non-secret connection details, always pre-populate
    // them when available so the user does not have to retype the server URL.
    // Username/password are only repopulated when the user previously asked
    // us to remember their settings -- otherwise the dialog opens with empty
    // credential fields, as the user expects.
    if ( config != null ) {
      timeoutField.setValue( PublishUtil.getTimeout( config ) );
      urlCombo.setSelectedItem( config.getUrl() );
      if ( remember ) {
        userField.setText( config.getUsername() );
        userPasswordField.setText( config.getPassword() );
      } else {
        userField.setText( null );
        userPasswordField.setText( null );
      }
    } else {
      timeoutField.setValue( WorkspaceSettings.getInstance().getConnectionTimeout() );
      urlCombo.setSelectedItem( null );
      userField.setText( null );
      userPasswordField.setText( null );
    }

    // Always default to Username & Password
    usernamePasswordRadio.setSelected( true );
    selectedLoginMethod = LoginMethod.USERNAME_PASSWORD;
    userPanel.setVisible( true );
    ssoProviderPanel.setVisible( false );

    applyDialogMode();
    pack();

    // Show the traditional username/password login dialog
    if ( !super.performEdit() ) {
      return null;
    }

    // Capture the user-entered values NOW, before the urlCombo update below
    // triggers URLChangeHandler which overwrites the text fields with stored
    // credentials (which may belong to a previous SSO session).
    final String enteredUsername = getUsername();
    final String enteredPassword = getUserPassword();
    final int enteredTimeout = getTimeout();

    // Save the selected provider before urlCombo update triggers fetchOAuthProviders(),
    // which clears the provider model and causes getSelectedOAuthProvider() to return null.
    confirmedProvider = ( selectedLoginMethod == LoginMethod.SSO
        && providerCombo.getSelectedItem() instanceof OAuthProvider oauthProvider )
        ? oauthProvider : null;

    urlCombo.getModel().setSelectedItem( urlCombo.getEditor().getItem() );

    final String url = getServerURL();
    if ( url == null ) {
      return null;
    }

    PublishSettings.getInstance().setRememberSettings( isRememberSettings() );
    final AuthenticationData data = new AuthenticationData( url, enteredUsername, enteredPassword, enteredTimeout );
    data.setOption( PublishUtil.SERVER_VERSION, String.valueOf( getVersion() ) );
    return data;
  }

  public AuthenticationData performReLogin( final AuthenticationData previousConfig ) {
    urlModel.removeAllElements();
    if ( previousConfig != null && previousConfig.getUrl() != null ) {
      urlModel.addElement( previousConfig.getUrl() );
    }

    final boolean rememberRe = PublishSettings.getInstance().isRememberSettings();
    rememberSettings.setSelected( rememberRe );

    if ( previousConfig != null ) {
      timeoutField.setValue( PublishUtil.getTimeout( previousConfig ) );
      urlCombo.setSelectedItem( previousConfig.getUrl() );
      // Same rule as performLogin: only pre-fill credentials when
      // "Remember settings" is enabled.
      if ( rememberRe ) {
        userField.setText( previousConfig.getUsername() );
        userPasswordField.setText( previousConfig.getPassword() );
      } else {
        userField.setText( null );
        userPasswordField.setText( null );
      }

      boolean isBrowserAuth = "true".equals( previousConfig.getOption( "browserAuth" ) );
      if ( isBrowserAuth ) {
        ssoRadio.setSelected( true );
        selectedLoginMethod = LoginMethod.SSO;
        userPanel.setVisible( false );
        ssoProviderPanel.setVisible( true );
      } else {
        usernamePasswordRadio.setSelected( true );
        selectedLoginMethod = LoginMethod.USERNAME_PASSWORD;
        userPanel.setVisible( true );
        ssoProviderPanel.setVisible( false );
      }
    } else {
      timeoutField.setValue( WorkspaceSettings.getInstance().getConnectionTimeout() );
      urlCombo.setSelectedItem( null );
      userField.setText( null );
      userPasswordField.setText( null );
      usernamePasswordRadio.setSelected( true );
      selectedLoginMethod = LoginMethod.USERNAME_PASSWORD;
      userPanel.setVisible( true );
      ssoProviderPanel.setVisible( false );
    }

    applyDialogMode();
    pack();

    if ( !super.performEdit() ) {
      return null;
    }

    // Capture the user-entered values NOW, before the urlCombo update below
    // triggers URLChangeHandler which overwrites the text fields with stored
    // credentials (which may belong to a previous SSO session).
    final String enteredUsername = getUsername();
    final String enteredPassword = getUserPassword();
    final int enteredTimeout = getTimeout();

    confirmedProvider = ( selectedLoginMethod == LoginMethod.SSO
        && providerCombo.getSelectedItem() instanceof OAuthProvider oauthProvider )
        ? oauthProvider : null;

    urlCombo.getModel().setSelectedItem( urlCombo.getEditor().getItem() );

    final String url = getServerURL();
    if ( url == null ) {
      return null;
    }

    PublishSettings.getInstance().setRememberSettings( isRememberSettings() );
    final AuthenticationData data = new AuthenticationData( url, enteredUsername, enteredPassword, enteredTimeout );
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

    // OAuth provider dropdown
    providerModel = new DefaultComboBoxModel<>();
    providerCombo = new JComboBox<>( providerModel );
    providerCombo.setEnabled( false );
    // Ensure the combo has a reasonable visible width even when empty
    providerCombo.setPrototypeDisplayValue( new OAuthProvider( "", "", "MMMMMMMMMMMMMMMMMMMM", "", false ) );
    providerCombo.setMaximumRowCount( 10 );

    providerStatusLabel = new JLabel( " " );

    // Debounce timer — fires 2000ms after the last URL keystroke to auto-fetch providers
    providerFetchTimer = new Timer( 2000, e -> fetchOAuthProviders() );
    providerFetchTimer.setRepeats( false );

    rememberSettings =
      new JCheckBox( Messages.getInstance().getString( "RepositoryLoginDialog.RememberTheseSettings" ), true );

    urlCombo.setEditable( true );
    urlCombo.addActionListener( new URLChangeHandler() );

    // Listen for URL edits so providers auto-refresh when the URL is typed/changed
    final Component editorComp = urlCombo.getEditor().getEditorComponent();
    if ( editorComp instanceof JTextComponent jTextComponent ) {
      jTextComponent.getDocument().addDocumentListener( new DocumentListener() {
        public void insertUpdate( DocumentEvent e ) { onUrlTextChanged(); }
        public void removeUpdate( DocumentEvent e ) { onUrlTextChanged(); }
        public void changedUpdate( DocumentEvent e ) { onUrlTextChanged(); }
      } );
    }

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
    loginMethodPanel = buildLoginMethodPanel();
    contentPane.add( loginMethodPanel, c );

    c.gridy = 2;
    c.insets = new Insets( 0, 10, 5, 10 );
    ssoProviderPanel = buildSSOProviderPanel();
    ssoProviderPanel.setVisible( false ); // Initially hidden (username/password selected by default)
    contentPane.add( ssoProviderPanel, c );

    c.gridy = 3;
    c.insets = new Insets( 0, 10, 5, 10 );
    userPanel = buildUserPanel();
    userPanel.setVisible( true ); // Initially visible (username/password selected by default)
    contentPane.add( userPanel, c );

    c.gridy = 4;
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

    c.gridy = 2;
    c.insets = new Insets( 0, 20, 5, 20 );
    c.weightx = 0;
    timeoutLabel = new JLabel( Messages.getInstance().getString( "RepositoryLoginDialog.Timeout" ) );
    serverPanel.add( timeoutLabel, c );

    c.gridy = 3;
    c.insets = new Insets( 0, 20, 10, 20 );
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    serverPanel.add( timeoutField, c );

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

  public OAuthProvider getSelectedOAuthProvider() {
    if ( selectedLoginMethod != LoginMethod.SSO ) {
      return null;
    }
    return confirmedProvider;
  }

  public void setDialogMode( final DialogMode mode ) {
    this.dialogMode = mode;
  }

  private void applyDialogMode() {
    switch ( dialogMode ) {
      case SSO_ONLY:
        loginMethodPanel.setVisible( false );
        userPanel.setVisible( false );
        ssoProviderPanel.setVisible( true );
        timeoutLabel.setVisible( false );
        timeoutField.setVisible( false );
        ssoRadio.setSelected( true );
        selectedLoginMethod = LoginMethod.SSO;
        getConfirmAction().setEnabled( !cachedProviders.isEmpty() );
        if ( cachedProviders.isEmpty() ) {
          fetchOAuthProviders();
        }
        break;
      case CREDENTIALS_ONLY:
        loginMethodPanel.setVisible( false );
        ssoProviderPanel.setVisible( false );
        userPanel.setVisible( true );
        timeoutLabel.setVisible( true );
        timeoutField.setVisible( true );
        usernamePasswordRadio.setSelected( true );
        selectedLoginMethod = LoginMethod.USERNAME_PASSWORD;
        getConfirmAction().setEnabled( true );
        break;
      case FULL:
      default:
        loginMethodPanel.setVisible( true );
        timeoutLabel.setVisible( true );
        timeoutField.setVisible( true );
        if ( selectedLoginMethod == LoginMethod.SSO ) {
          getConfirmAction().setEnabled( !cachedProviders.isEmpty() );
        } else {
          getConfirmAction().setEnabled( true );
        }
        break;
    }
  }

  private void handleLoginMethodChange( final LoginMethod loginMethod, final boolean showUserPanel ) {
    selectedLoginMethod = loginMethod;
    userPanel.setVisible( showUserPanel );
    ssoProviderPanel.setVisible( !showUserPanel );
    if ( !showUserPanel && cachedProviders.isEmpty() ) {
      fetchOAuthProviders();
    }
    if ( showUserPanel ) {
      getConfirmAction().setEnabled( true );
    } else {
      getConfirmAction().setEnabled( !cachedProviders.isEmpty() );
    }
    pack();
  }

  private void updateConfirmForSSO( final boolean providerAvailable ) {
    if ( selectedLoginMethod == LoginMethod.SSO ) {
      getConfirmAction().setEnabled( providerAvailable );
    }
  }

  private JPanel buildSSOProviderPanel() {
    final JPanel panel = new JPanel( new GridBagLayout() );
    panel.setBorder( BorderFactory.createTitledBorder(
      Messages.getInstance().getString( "RepositoryLoginDialog.SSOProvider" ) ) );

    final GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets( 0, 20, 5, 20 );
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    panel.add( new JLabel(
      Messages.getInstance().getString( "RepositoryLoginDialog.SelectProvider" ) ), c );

    c.gridy = 1;
    c.insets = new Insets( 0, 20, 5, 20 );
    c.weightx = 1.0;
    c.gridwidth = 1;
    panel.add( providerCombo, c );

    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.insets = new Insets( 0, 20, 10, 20 );
    panel.add( providerStatusLabel, c );

    return panel;
  }

  private String getCurrentEditorUrl() {
    final Component editorComp = urlCombo.getEditor().getEditorComponent();
    if ( editorComp instanceof JTextComponent jTextComponent ) {
      return jTextComponent.getText();
    }
    return getServerURL();
  }

  private void onUrlTextChanged() {
    final String newUrl = getCurrentEditorUrl();
    if ( newUrl != null && newUrl.equals( activeFetchUrl ) ) {
      return;
    }
    activeFetchUrl = null;
    providerModel.removeAllElements();
    cachedProviders.clear();
    providerCombo.setEnabled( false );
    updateConfirmForSSO( false );
    if ( newUrl != null && !newUrl.trim().isEmpty() ) {
      providerStatusLabel.setText(
        Messages.getInstance().getString( "RepositoryLoginDialog.FetchingProviders" ) );
    } else {
      providerStatusLabel.setText(
        Messages.getInstance().getString( "RepositoryLoginDialog.EnterURLFirst" ) );
    }
    providerFetchTimer.restart();
  }

  private void fetchOAuthProviders() {
    providerFetchTimer.stop();
    final String serverUrl = getCurrentEditorUrl();
    if ( serverUrl == null || serverUrl.trim().isEmpty() ) {
      activeFetchUrl = null;
      providerModel.removeAllElements();
      cachedProviders.clear();
      providerCombo.setEnabled( false );
      updateConfirmForSSO( false );
      providerStatusLabel.setText(
        Messages.getInstance().getString( "RepositoryLoginDialog.EnterURLFirst" ) );
      return;
    }

    providerModel.removeAllElements();
    cachedProviders.clear();

    activeFetchUrl = serverUrl;
    providerStatusLabel.setText(
      Messages.getInstance().getString( "RepositoryLoginDialog.FetchingProviders" ) );
    providerCombo.setEnabled( false );
    setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

    new SwingWorker<List<OAuthProvider>, Void>() {
      @Override
      protected List<OAuthProvider> doInBackground() throws Exception {
        return OAuthProviderService.fetchProviders( serverUrl );
      }

      @Override
      protected void done() {
        try {
          if ( !serverUrl.equals( activeFetchUrl ) ) {
            return;
          }
          applyFetchedProviders( get() );
        } catch ( InterruptedException ex ) {
          Thread.currentThread().interrupt();
          applyProviderFetchError( serverUrl );
        } catch ( Exception ex ) {
          applyProviderFetchError( serverUrl );
        } finally {
          setCursor( Cursor.getDefaultCursor() );
          ssoProviderPanel.revalidate();
          ssoProviderPanel.repaint();
          pack();
        }
      }
    }.execute();
  }

  private void applyFetchedProviders( final List<OAuthProvider> providers ) {
    providerModel.removeAllElements();
    cachedProviders.clear();

    if ( providers.isEmpty() ) {
      providerStatusLabel.setText(
        Messages.getInstance().getString( "RepositoryLoginDialog.NoProvidersFound" ) );
      providerCombo.setEnabled( false );
      updateConfirmForSSO( false );
    } else {
      for ( OAuthProvider p : providers ) {
        providerModel.addElement( p );
      }
      cachedProviders.addAll( providers );
      providerCombo.setEnabled( true );
      providerCombo.setSelectedIndex( 0 );
      providerStatusLabel.setText(
        Messages.getInstance().formatMessage( "RepositoryLoginDialog.ProvidersLoaded",
          String.valueOf( providers.size() ) ) );
      updateConfirmForSSO( true );
    }
  }

  private void applyProviderFetchError( final String expectedUrl ) {
    if ( expectedUrl.equals( activeFetchUrl ) ) {
      providerStatusLabel.setText(
        Messages.getInstance().getString( "RepositoryLoginDialog.ProviderFetchError" ) );
      providerCombo.setEnabled( false );
      updateConfirmForSSO( false );
    }
  }

}
