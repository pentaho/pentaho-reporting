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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

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

  private JComboBox urlCombo;
  private JSpinner timeoutField;
  private JTextField userField;
  private JPasswordField userPasswordField;
  private JCheckBox rememberSettings;
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
    } else {
      timeoutField.setValue( WorkspaceSettings.getInstance().getConnectionTimeout() );
      urlCombo.setSelectedItem( null );
      userField.setText( null );
      userPasswordField.setText( null );
    }

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
    userPasswordField = new JPasswordField();

    final SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
    spinnerModel.setMinimum( 0 );
    spinnerModel.setMaximum( 99999 );

    timeoutField = new JSpinner( spinnerModel );
    timeoutField.setEditor( new JSpinner.NumberEditor( timeoutField, "#####" ) );

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
    contentPane.add( buildUserPanel(), c );

    c.gridy = 2;
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
    c.insets = new Insets( 0, 20, 5, 20 );
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    serverPanel.add( urlCombo, c );

    c.insets = new Insets( 0, 20, 5, 20 );
    c.gridy = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    serverPanel.add( new JLabel( Messages.getInstance().getString( "RepositoryLoginDialog.Timeout" ) ), c );

    c.gridy = 3;
    c.insets = new Insets( 0, 20, 5, 20 );
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    serverPanel.add( timeoutField, c );

    serverPanel.setBorder( BorderFactory.createTitledBorder( Messages.getInstance().getString(
        "RepositoryLoginDialog.Server" ) ) );
    return serverPanel;
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

}
