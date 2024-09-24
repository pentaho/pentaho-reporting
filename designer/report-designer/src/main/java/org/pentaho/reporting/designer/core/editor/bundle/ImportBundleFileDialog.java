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
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImportBundleFileDialog extends CommonDialog {
  private class ValidateHandler extends DocumentChangeHandler implements ActionListener {
    protected void handleChange( final DocumentEvent e ) {
      final String s = entryNameField.getText();
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

  private class BrowseAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private BrowseAction() {
      putValue( Action.NAME, ".." );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final FileFilter[] filters = {
        new FilesystemFilter( ".properties", // NON-NLS
          Messages.getString( "BundledResourceEditor.PropertiesTranslations" ) ),
        new FilesystemFilter( new String[] { ".xml", ".report", ".prpt", ".prpti", ".prptstyle" }, // NON-NLS
          Messages.getString( "BundledResourceEditor.Resources" ), true ),
        new FilesystemFilter( new String[] { ".gif", ".jpg", ".jpeg", ".png", ".svg", ".wmf" }, // NON-NLS
          Messages.getString( "BundledResourceEditor.Images" ), true ),
      };

      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( "resources" );//NON-NLS
      fileChooser.setFilters( filters );
      if ( fileChooser.showDialog( ImportBundleFileDialog.this, JFileChooser.OPEN_DIALOG ) == false ) {
        return;
      }
      final File selectedFile = fileChooser.getSelectedFile();
      if ( selectedFile == null ) {
        return;
      }

      fileNameField.setText( selectedFile.getPath() );
    }
  }

  private JTextField fileNameField;
  private JComboBox mimeTypeBox;
  private JTextField entryNameField;
  private DocumentBundle bundle;

  public ImportBundleFileDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );

    final ValidateHandler listener = new ValidateHandler();

    mimeTypeBox = new JComboBox();
    mimeTypeBox.setEditable( true );
    mimeTypeBox.addItem( "text/plain" ); // NON-NLS
    mimeTypeBox.addItem( "text/xml" ); // NON-NLS
    mimeTypeBox.addItem( "image/jpeg" ); // NON-NLS
    mimeTypeBox.addItem( "image/png" ); // NON-NLS
    mimeTypeBox.addItem( "image/gif" ); // NON-NLS
    mimeTypeBox.addItem( "application/octet-stream" ); // NON-NLS
    mimeTypeBox.setSelectedIndex( 0 );
    mimeTypeBox.addActionListener( listener );

    entryNameField = new JTextField();
    entryNameField.getDocument().addDocumentListener( listener );

    fileNameField = new JTextField();
    fileNameField.getDocument().addDocumentListener( listener );

    init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ImportBundleFile";
  }

  protected Component createContentPane() {
    final JPanel panel = new JPanel();
    panel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add( new JLabel( Messages.getString( "ImportBundleFileDialog.FileName" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add( new JLabel( Messages.getString( "ImportBundleFileDialog.EntryName" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add( new JLabel( Messages.getString( "ImportBundleFileDialog.ContentType" ) ), gbc );

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
    panel.add( entryNameField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add( mimeTypeBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    panel.add( new JButton( new BrowseAction() ), gbc );

    return panel;
  }

  public boolean performCreateEntry( final DocumentBundle bundle ) {
    this.bundle = bundle;
    mimeTypeBox.setSelectedIndex( 0 );
    entryNameField.setText( "" );
    fileNameField.setText( "" );
    LibSwingUtil.centerDialogInParent( this );
    return super.performEdit();
  }

  public String getMimeType() {
    return (String) mimeTypeBox.getSelectedItem();
  }

  public String getEntryName() {
    return entryNameField.getText();
  }

  public String getFileName() {
    return fileNameField.getText();
  }
}

