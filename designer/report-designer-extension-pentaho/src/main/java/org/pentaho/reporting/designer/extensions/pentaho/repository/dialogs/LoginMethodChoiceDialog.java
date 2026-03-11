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

import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog that presents users with two login options:
 * 1. Login through browser (SSO-style)
 * 2. Login with username/password (traditional)
 */
public class LoginMethodChoiceDialog extends CommonDialog {

  public enum LoginMethod {
    BROWSER,
    USERNAME_PASSWORD,
    CANCELLED
  }

  private LoginMethod selectedMethod = LoginMethod.CANCELLED;

  public LoginMethodChoiceDialog( final Frame parent ) {
    super( parent );
    init();
  }

  public LoginMethodChoiceDialog( final Dialog parent ) {
    super( parent );
    init();
  }

  public LoginMethodChoiceDialog() {
    super();
    init();
  }

  protected void init() {
    setTitle( Messages.getInstance().getString( "LoginMethodChoice.Title" ) );
    setModal( true );
    super.init();
    
    // Hide OK/Cancel buttons since we have our own buttons
    setButtonPaneVisible( false );
  }

  @Override
  protected String getDialogId() {
    return "ReportDesigner.Pentaho.LoginMethodChoice";
  }

  @Override
  protected Component createContentPane() {
    JPanel panel = new JPanel();
    panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
    panel.setBorder( BorderFactory.createEmptyBorder( 20, 20, 20, 20 ) );

    // Title
    JLabel titleLabel = new JLabel( Messages.getInstance().getString( "LoginMethodChoice.ChooseMethod" ) );
    titleLabel.setFont( titleLabel.getFont().deriveFont( Font.BOLD, 16f ) );
    titleLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
    panel.add( titleLabel );

    panel.add( Box.createVerticalStrut( 10 ) );

    // Description
    JLabel descLabel = new JLabel( Messages.getInstance().getString( "LoginMethodChoice.Description" ) );
    descLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
    panel.add( descLabel );

    panel.add( Box.createVerticalStrut( 30 ) );

    // Browser Login Button
    JButton browserButton = new JButton( Messages.getInstance().getString( "LoginMethodChoice.BrowserLogin" ) );
    browserButton.setAlignmentX( Component.CENTER_ALIGNMENT );
    browserButton.setPreferredSize( new Dimension( 300, 50 ) );
    browserButton.setMaximumSize( new Dimension( 300, 50 ) );
    browserButton.setFont( browserButton.getFont().deriveFont( 14f ) );
    browserButton.setFocusPainted( false ); // Remove dotted focus border
    browserButton.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        selectedMethod = LoginMethod.BROWSER;
        dispose();
      }
    } );
    panel.add( browserButton );

    panel.add( Box.createVerticalStrut( 15 ) );

    // Username/Password Login Button
    JButton credentialsButton = new JButton( Messages.getInstance().getString( "LoginMethodChoice.CredentialsLogin" ) );
    credentialsButton.setAlignmentX( Component.CENTER_ALIGNMENT );
    credentialsButton.setPreferredSize( new Dimension( 300, 50 ) );
    credentialsButton.setMaximumSize( new Dimension( 300, 50 ) );
    credentialsButton.setFont( credentialsButton.getFont().deriveFont( 14f ) );
    credentialsButton.setFocusPainted( false ); // Remove dotted focus border
    credentialsButton.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        selectedMethod = LoginMethod.USERNAME_PASSWORD;
        dispose();
      }
    } );
    panel.add( credentialsButton );

    panel.add( Box.createVerticalStrut( 20 ) );

    // Help text
    JLabel helpLabel = new JLabel( "<html><center>" + 
      Messages.getInstance().getString( "LoginMethodChoice.BrowserHint" ) + 
      "</center></html>" );
    helpLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
    helpLabel.setFont( helpLabel.getFont().deriveFont( Font.PLAIN, 11f ) );
    panel.add( helpLabel );

    return panel;
  }

  /**
   * Shows the dialog and returns the selected login method.
   * 
   * @return The selected login method (BROWSER, USERNAME_PASSWORD, or CANCELLED)
   */
  public LoginMethod showDialog() {
    selectedMethod = LoginMethod.CANCELLED;
    pack();
    setLocationRelativeTo( getParent() );
    setVisible( true );
    return selectedMethod;
  }

}
