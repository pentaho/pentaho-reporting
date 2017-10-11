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

package org.pentaho.reporting.designer.core.settings.ui;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.settings.ProxySettings;
import org.pentaho.reporting.designer.core.settings.ProxyType;
import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User: Martin Date: 03.03.2006 Time: 14:14:22
 */
public class NetworkSettingsPanel extends JPanel implements SettingsPlugin {
  private class EditExceptionsAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private EditExceptionsAction() {
      putValue( Action.NAME, SettingsMessages.getInstance().getString( "ProxySettingsPanel.Exceptions" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final Window window = LibSwingUtil.getWindowAncestor( NetworkSettingsPanel.this );
      final PasswordExceptionsDialog showPasswordsDialog;
      if ( window instanceof Dialog ) {
        showPasswordsDialog = new PasswordExceptionsDialog( (Dialog) window );
      } else if ( window instanceof Frame ) {
        showPasswordsDialog = new PasswordExceptionsDialog( (Frame) window );
      } else {
        showPasswordsDialog = new PasswordExceptionsDialog();
      }
      showPasswordsDialog.performEdit();
    }
  }

  private class ShowPasswordsAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ShowPasswordsAction() {
      putValue( Action.NAME, SettingsMessages.getInstance().getString( "ProxySettingsPanel.ShowPasswords" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final ReportDesignerContext context = getReportDesignerContext();
      if ( context == null ) {
        throw new IllegalStateException();
      }

      final Window window = LibSwingUtil.getWindowAncestor( NetworkSettingsPanel.this );
      final ShowPasswordsDialog showPasswordsDialog;
      if ( window instanceof Dialog ) {
        showPasswordsDialog = new ShowPasswordsDialog( (Dialog) window );
      } else if ( window instanceof Frame ) {
        showPasswordsDialog = new ShowPasswordsDialog( (Frame) window );
      } else {
        showPasswordsDialog = new ShowPasswordsDialog();
      }
      showPasswordsDialog.showDialog( context.getGlobalAuthenticationStore() );
    }
  }

  private class EnableTextFieldsHandler implements ChangeListener {
    private EnableTextFieldsHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      updateState();
    }
  }

  private JRadioButton radioButtonNoProxy;
  private JRadioButton radioButtonAutoDetectProxy;
  private JRadioButton radioButtonUserProxy;

  private JTextField proxyHostTextField;
  private JTextField proxyPortTextField;
  private JLabel proxyHostLabel;
  private JLabel proxyPortLabel;

  private JLabel userLabel;
  private JLabel passwordLabel;
  private JTextField userTextField;
  private JPasswordField passwordField;

  private JCheckBox socksCheckBox;
  private ButtonGroup buttonGroup;
  private JCheckBox offlineMode;
  private JCheckBox rememberPasswordsBox;
  private EditExceptionsAction editExceptionsAction;
  private ReportDesignerContext reportDesignerContext;

  public NetworkSettingsPanel() {
    editExceptionsAction = new EditExceptionsAction();

    offlineMode = new JCheckBox
      ( SettingsMessages.getInstance().getString( "ProxySettingsPanel.OfflineMode" ) );
    offlineMode.setSelected( WorkspaceSettings.getInstance().isOfflineMode() );

    rememberPasswordsBox = new JCheckBox
      ( SettingsMessages.getInstance().getString( "ProxySettingsPanel.RememberPasswords" ) );
    rememberPasswordsBox.setSelected( WorkspaceSettings.getInstance().isRememberPasswords() );
    rememberPasswordsBox.addChangeListener( new EnableTextFieldsHandler() );

    radioButtonNoProxy = new JRadioButton( SettingsMessages.getInstance().getString( "ProxySettingsPanel.noProxy" ) );
    radioButtonNoProxy.addChangeListener( new EnableTextFieldsHandler() );
    radioButtonAutoDetectProxy = new JRadioButton
      ( SettingsMessages.getInstance().getString( "ProxySettingsPanel.autoDetectProxy" ) );
    radioButtonAutoDetectProxy.addChangeListener( new EnableTextFieldsHandler() );
    radioButtonUserProxy =
      new JRadioButton( SettingsMessages.getInstance().getString( "ProxySettingsPanel.userProxy" ) );
    radioButtonUserProxy.addChangeListener( new EnableTextFieldsHandler() );

    buttonGroup = new ButtonGroup();
    buttonGroup.add( radioButtonNoProxy );
    buttonGroup.add( radioButtonAutoDetectProxy );
    buttonGroup.add( radioButtonUserProxy );

    proxyHostTextField = new JTextField( null, 0 );
    proxyPortTextField = new JTextField( null, 0 );

    socksCheckBox = new JCheckBox( SettingsMessages.getInstance().getString( "ProxySettingsPanel.socks" ) );

    proxyHostLabel = new JLabel( SettingsMessages.getInstance().getString( "ProxySettingsPanel.proxyHost" ) );
    proxyPortLabel = new JLabel( SettingsMessages.getInstance().getString( "ProxySettingsPanel.proxyPort" ) );
    userLabel = new JLabel( SettingsMessages.getInstance().getString( "ProxySettingsPanel.user" ) );
    passwordLabel = new JLabel( SettingsMessages.getInstance().getString( "ProxySettingsPanel.password" ) );

    userTextField = new JTextField( null, 0 );
    passwordField = new JPasswordField();
    passwordField.setPreferredSize( userTextField.getPreferredSize() );

    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout( new GridBagLayout() );
    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( createProxySettingsPanel(), c );

    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1;
    c.insets = new Insets( 5, 0, 0, 0 );
    c.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( createNetworkPanel(), c );

    setLayout( new BorderLayout() );
    add( contentPanel, BorderLayout.NORTH );

    reset();
  }

  private JPanel createNetworkPanel() {
    final JPanel panel = new JPanel();
    panel.setLayout( new GridBagLayout() );
    panel.setBorder( BorderFactory.createTitledBorder
      ( SettingsMessages.getInstance().getString( "ProxySettingsPanel.NetworkSettings" ) ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( offlineMode, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( rememberPasswordsBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( new JButton( editExceptionsAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( new JButton( new ShowPasswordsAction() ), gbc );

    return panel;
  }

  private JPanel createProxySettingsPanel() {
    final JPanel panel = new JPanel();
    panel.setLayout( new GridBagLayout() );
    panel.setBorder( BorderFactory.createTitledBorder
      ( SettingsMessages.getInstance().getString( "ProxySettingsPanel.ProxySettings" ) ) );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( radioButtonNoProxy, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add( radioButtonAutoDetectProxy, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add( radioButtonUserProxy, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( proxyHostLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( proxyHostTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    panel.add( proxyPortLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    panel.add( proxyPortTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    panel.add( socksCheckBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( userLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 6;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    panel.add( userTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    panel.add( passwordLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    panel.add( passwordField, gbc );

    return panel;
  }

  protected void updateState() {
    editExceptionsAction.setEnabled( rememberPasswordsBox.isSelected() );

    if ( radioButtonNoProxy.isSelected() ) {
      enableProxyFields( false );
      enableAuthenticationFields( false );
    } else if ( radioButtonAutoDetectProxy.isSelected() ) {
      enableProxyFields( false );
      enableAuthenticationFields( true );
    } else {
      enableProxyFields( true );
      enableAuthenticationFields( true );
    }
  }

  private void enableProxyFields( final boolean enable ) {
    proxyHostTextField.setEnabled( enable );
    proxyPortTextField.setEnabled( enable );
    proxyHostLabel.setEnabled( enable );
    proxyPortLabel.setEnabled( enable );
    socksCheckBox.setEnabled( enable );
  }


  private void enableAuthenticationFields( final boolean enable ) {
    userLabel.setEnabled( enable );
    userTextField.setEnabled( enable );
    passwordLabel.setEnabled( enable );
    passwordField.setEnabled( enable );
  }

  public JComponent getComponent() {
    return this;
  }

  public Icon getIcon() {
    return IconLoader.getInstance().getNetworkIcon32();
  }

  public String getTitle() {
    return SettingsMessages.getInstance().getString( "SettingsDialog.Proxy" );
  }

  public ValidationResult validate( final ValidationResult result ) {
    if ( radioButtonUserProxy.isSelected() ) {
      if ( !NetworkSettingsPanel.isValidPortNumber( proxyPortTextField.getText().trim() ) ) {
        final ValidationMessage validationMessage = new ValidationMessage( ValidationMessage.Severity.ERROR,
          SettingsMessages.getInstance().getString( "ProxySettingsPanel.invalidPortFormat" ) );
        result.addValidationMessage( validationMessage );
      }
    }
    return result;
  }


  private static boolean isValidPortNumber( final String s ) {
    try {
      final int port = Integer.parseInt( s );
      return ( port > 0 && port < 65536 );
    } catch ( NumberFormatException e ) {
      // ignore the exception.
      return false;
    }
  }

  public void apply() {
    WorkspaceSettings.getInstance().setOfflineMode( offlineMode.isSelected() );
    WorkspaceSettings.getInstance().setRememberPasswords( rememberPasswordsBox.isSelected() );

    final ProxySettings proxySettings = ProxySettings.getInstance();

    if ( radioButtonNoProxy.isSelected() ) {
      proxySettings.setProxyType( ProxyType.NO_PROXY );
    } else if ( radioButtonAutoDetectProxy.isSelected() ) {
      proxySettings.setProxyType( ProxyType.AUTO_DETECT_PROXY );
    } else if ( radioButtonUserProxy.isSelected() ) {
      proxySettings.setProxyType( ProxyType.USER_PROXY );
      proxySettings.setUseSocksProxy( socksCheckBox.isSelected() );
      if ( socksCheckBox.isSelected() ) {
        proxySettings.setSocksProxyHost( proxyHostTextField.getText().trim() );
        proxySettings.setSocksProxyPort( ParserUtil.parseInt( proxyPortTextField.getText().trim(), -1 ) );
      } else {
        proxySettings.setHTTPProxyHost( proxyHostTextField.getText().trim() );
        proxySettings.setHTTPProxyPort( ParserUtil.parseInt( proxyPortTextField.getText().trim(), -1 ) );
      }
    }

    proxySettings.setProxyUser( userTextField.getText() );
    proxySettings.setProxyPassword( new String( passwordField.getPassword() ) );

    proxySettings.applySettings();
  }


  public void reset() {
    offlineMode.setSelected( WorkspaceSettings.getInstance().isOfflineMode() );
    rememberPasswordsBox.setSelected( WorkspaceSettings.getInstance().isRememberPasswords() );

    final ProxySettings proxySettings = ProxySettings.getInstance();

    final String httpProxyHost = proxySettings.getHTTPProxyHost();
    final String proxyHost = proxySettings.getSocksProxyHost();
    switch( proxySettings.getProxyType() ) {
      case NO_PROXY: {
        buttonGroup.setSelected( radioButtonNoProxy.getModel(), true );

        proxyHostTextField.setText( "" );
        proxyPortTextField.setText( "" );

        userTextField.setText( "" );
        passwordField.setText( "" );

        enableProxyFields( false );
        enableAuthenticationFields( false );
        break;
      }
      case AUTO_DETECT_PROXY: {
        buttonGroup.setSelected( radioButtonAutoDetectProxy.getModel(), true );

        if ( httpProxyHost != null &&
          httpProxyHost.trim().length() > 0 ) {
          proxyHostTextField.setText( httpProxyHost );
          proxyPortTextField.setText( String.valueOf( proxySettings.getHTTPProxyPort() ) );
          socksCheckBox.setSelected( false );
        } else if ( proxyHost != null && proxyHost.trim().length() > 0 ) {
          proxyHostTextField.setText( proxyHost );
          proxyPortTextField.setText( String.valueOf( proxySettings.getSocksProxyPort() ) );
          socksCheckBox.setSelected( true );
        } else {
          proxyHostTextField.setText( "" );
          proxyPortTextField.setText( "" );
          socksCheckBox.setSelected( false );
        }

        enableProxyFields( false );

        userTextField.setText( "" );
        passwordField.setText( "" );

        enableAuthenticationFields( true );
        break;
      }
      case USER_PROXY: {
        buttonGroup.setSelected( radioButtonUserProxy.getModel(), true );

        if ( proxySettings.isUseSocksProxy() ) {
          proxyHostTextField.setText( proxyHost );
          proxyPortTextField.setText( String.valueOf( proxySettings.getSocksProxyPort() ) );
        } else {
          proxyHostTextField.setText( httpProxyHost );
          proxyPortTextField.setText( String.valueOf( proxySettings.getHTTPProxyPort() ) );
        }

        socksCheckBox.setSelected( proxySettings.isUseSocksProxy() );

        enableProxyFields( true );

        enableAuthenticationFields( true );
        userTextField.setText( proxySettings.getProxyUser() );
        passwordField.setText( proxySettings.getProxyPassword() );

        break;
      }
    }

    userTextField.setText( proxySettings.getProxyUser() );
    passwordField.setText( proxySettings.getProxyPassword() );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    this.reportDesignerContext = reportDesignerContext;
  }
}
