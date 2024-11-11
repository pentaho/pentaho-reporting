/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.designer.core.settings.ui;

import org.pentaho.reporting.designer.core.auth.PasswordPolicyManager;
import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PasswordExceptionsDialog extends CommonDialog {

  private class RemovePasswordsAction extends AbstractAction implements ListSelectionListener {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private RemovePasswordsAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon() );
      putValue( Action.SHORT_DESCRIPTION,
        SettingsMessages.getInstance().getString( "PasswordExceptionsDialog.Remove" ) );
      setEnabled( false );
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      final int[] selectedRows = hostList.getSelectedIndices();
      setEnabled( selectedRows.length > 0 );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final Object[] selectedURLs = hostList.getSelectedValues();
      for ( int i = 0; i < selectedURLs.length; i++ ) {
        final String selectedURL = (String) selectedURLs[ i ];
        if ( selectedURL != null ) {
          passwordPolicyManager.setPasswordStoringAllowed( selectedURL, false );
        }
      }
      fillModel();
    }
  }

  private DefaultListModel hostModel;
  private JList hostList;
  private RemovePasswordsAction removePasswordsAction;
  private PasswordPolicyManager passwordPolicyManager;

  /**
   * Creates a new modal dialog.
   */
  public PasswordExceptionsDialog() {
    init();
  }

  public PasswordExceptionsDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public PasswordExceptionsDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( SettingsMessages.getInstance().getString( "PasswordExceptionsDialog.Title" ) );
    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.PasswordExceptions";
  }

  protected Component createContentPane() {
    removePasswordsAction = new RemovePasswordsAction();
    passwordPolicyManager = PasswordPolicyManager.getInstance();

    hostModel = new DefaultListModel();
    hostList = new JList( hostModel );
    hostList.addListSelectionListener( removePasswordsAction );

    final JPanel buttonPanel = new JPanel();
    buttonPanel.setBorder( BorderFactory.createEmptyBorder( 5, 0, 0, 0 ) );
    buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonPanel.add( new BorderlessButton( removePasswordsAction ) );

    final JPanel headerPanel = new JPanel();
    headerPanel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 5, 0 ) );
    headerPanel.setLayout( new BorderLayout() );
    headerPanel.add( buttonPanel, BorderLayout.EAST );
    headerPanel.add( new JLabel( SettingsMessages.getInstance().getString( "PasswordExceptionsDialog.Message" ) ),
      BorderLayout.EAST );

    final JPanel panel = new JPanel();
    panel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    panel.setLayout( new BorderLayout() );
    panel.add( headerPanel, BorderLayout.NORTH );
    panel.add( new JScrollPane( hostList ), BorderLayout.CENTER );

    return panel;
  }

  public boolean performEdit() {
    fillModel();
    return super.performEdit();
  }

  private void fillModel() {
    final String[] strings = passwordPolicyManager.getManagedHosts();
    hostModel.clear();
    for ( int i = 0; i < strings.length; i++ ) {
      hostModel.addElement( strings[ i ] );
    }
  }
}
