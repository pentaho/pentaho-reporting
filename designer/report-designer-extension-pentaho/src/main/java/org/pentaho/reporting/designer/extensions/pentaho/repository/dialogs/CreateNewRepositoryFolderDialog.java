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
