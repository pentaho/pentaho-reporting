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

package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

public class CreateNewRepositoryFolderDialog extends CommonDialog {
  private JTextField nameTextField;
  private JTextField descTextField;

  public CreateNewRepositoryFolderDialog( final Dialog dialog ) {
    super( dialog );
    init();
  }

  public void init() {
    setTitle( Messages.getInstance().getString( "CreateNewRepositoryFolderDialog.Title" ) );
    nameTextField = new JTextField( 40 );
    descTextField = new JTextField( 40 );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Pentaho.CreateNewRepositoryFolder";
  }

  public boolean performEdit() {
    return super.performEdit();
  }

  protected Component createContentPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );

    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 0.0;
    c.insets = new Insets( 5, 10, 0, 10 );
    contentPane.add( new JLabel( Messages.getInstance().getString( "CreateNewRepositoryFolderDialog.Name" ) ), c );

    c.gridx = 0;
    c.gridy = 1;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 1.0;
    c.insets = new Insets( 0, 10, 5, 10 );
    contentPane.add( nameTextField, c );

    c.gridx = 0;
    c.gridy = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 0.0;
    c.insets = new Insets( 5, 10, 0, 10 );
    contentPane
        .add( new JLabel( Messages.getInstance().getString( "CreateNewRepositoryFolderDialog.Description" ) ), c );

    c.gridx = 0;
    c.gridy = 3;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 1.0;
    c.insets = new Insets( 0, 10, 5, 10 );
    contentPane.add( descTextField, c );

    return contentPane;
  }

  public String getDescription() {
    return descTextField.getText();
  }

  public String getFolderName() {
    return nameTextField.getText();
  }

}
