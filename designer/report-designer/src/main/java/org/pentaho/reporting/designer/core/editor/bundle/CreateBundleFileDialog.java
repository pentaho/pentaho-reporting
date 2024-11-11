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


package org.pentaho.reporting.designer.core.editor.bundle;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateBundleFileDialog extends CommonDialog {
  private class ValidateHandler extends DocumentChangeHandler implements ActionListener {
    protected void handleChange( final DocumentEvent e ) {
      final String s = fileNameField.getText();
      if ( StringUtils.isEmpty( s ) ) {
        getConfirmAction().setEnabled( false );
        return;
      }

      if ( StringUtils.isEmpty( (String) mimeTypeBox.getSelectedItem() ) ) {
        getConfirmAction().setEnabled( false );
        return;
      }

      getConfirmAction().setEnabled( bundle.isEntryExists( s ) == false );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      handleChange( null );
    }
  }

  private JComboBox mimeTypeBox;
  private JTextField fileNameField;
  private DocumentBundle bundle;

  public CreateBundleFileDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );

    final ValidateHandler listener = new ValidateHandler();

    mimeTypeBox = new JComboBox();
    mimeTypeBox.setEditable( true );
    mimeTypeBox.addItem( "text/plain" ); // NON-NLS
    mimeTypeBox.addItem( "text/xml" ); // NON-NLS
    mimeTypeBox.addItem( "application/octet-stream" ); // NON-NLS
    mimeTypeBox.setSelectedIndex( 0 );
    mimeTypeBox.addActionListener( listener );

    fileNameField = new JTextField();
    fileNameField.getDocument().addDocumentListener( listener );
    init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.CreateBundleFile";
  }

  protected Component createContentPane() {
    final JPanel panel = new JPanel();
    panel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add( new JLabel( Messages.getString( "CreateBundleFileDialog.EntryName" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add( new JLabel( Messages.getString( "CreateBundleFileDialog.ContentType" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add( fileNameField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add( mimeTypeBox, gbc );

    return panel;
  }

  public boolean performCreateEntry( final DocumentBundle bundle ) {
    this.bundle = bundle;
    mimeTypeBox.setSelectedIndex( 0 );
    fileNameField.setText( "" );

    LibSwingUtil.centerDialogInParent( this );
    return super.performEdit();
  }

  public String getMimeType() {
    return (String) mimeTypeBox.getSelectedItem();
  }

  public String getFileName() {
    return fileNameField.getText();
  }
}
