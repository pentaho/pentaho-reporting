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

package org.pentaho.reporting.designer.core.editor.bundle;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.TextAreaPropertyEditorDialog;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringBufferWriter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class BundledResourceEditor extends JDialog {
  private class ImportFileAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ImportFileAction() {
      putValue( Action.NAME, Messages.getString( "BundledResourceEditor.Import" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final ImportBundleFileDialog dialog = new ImportBundleFileDialog( BundledResourceEditor.this );
      if ( dialog.performCreateEntry( bundle ) == false ) {
        return;
      }

      final String entryName = dialog.getEntryName();
      final String fileName = dialog.getFileName();
      final String mimeType = dialog.getMimeType();

      try {
        final FileInputStream fin = new FileInputStream( fileName );
        try {
          final OutputStream outputStream = bundle.createEntry( entryName, mimeType );
          try {
            IOUtils.getInstance().copyStreams( fin, outputStream );
          } finally {
            outputStream.close();
          }
          bundle.getWriteableDocumentMetaData()
            .setEntryAttribute( entryName, BundleUtilities.STICKY_FLAG, "true" ); // NON-NLS

        } finally {
          fin.close();
        }
      } catch ( IOException ioe ) {
        UncaughtExceptionsModel.getInstance().addException( ioe );
      }
      changed = true;
      refreshBundleList();
    }
  }

  private class ExportFileAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ExportFileAction() {
      putValue( Action.NAME, Messages.getString( "BundledResourceEditor.Export" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final String entryName = getSelectedEntry();
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
      if ( fileChooser.showDialog( BundledResourceEditor.this, JFileChooser.OPEN_DIALOG ) == false ) {
        return;
      }
      final File selectedFile = fileChooser.getSelectedFile();
      if ( selectedFile == null ) {
        return;
      }

      try {
        final FileOutputStream fout = new FileOutputStream( selectedFile );
        try {
          final InputStream inputStream = bundle.getEntryAsStream( entryName );
          try {
            IOUtils.getInstance().copyStreams( inputStream, fout );
          } finally {
            inputStream.close();
          }
          bundle.getWriteableDocumentMetaData()
            .setEntryAttribute( entryName, BundleUtilities.STICKY_FLAG, "true" ); // NON-NLS

        } finally {
          fout.close();
        }
      } catch ( IOException ioe ) {
        UncaughtExceptionsModel.getInstance().addException( ioe );
      }

      changed = true;
    }
  }

  private class RemoveFileAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private RemoveFileAction() {
      putValue( Action.NAME, Messages.getString( "BundledResourceEditor.Remove" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final String selectedEntry = getSelectedEntry();
      if ( selectedEntry == null ) {
        return;
      }
      try {
        bundle.removeEntry( selectedEntry );
      } catch ( IOException e1 ) {
        e1.printStackTrace();
      }
      changed = true;
      refreshBundleList();
    }
  }

  private class EditFileAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private EditFileAction() {
      putValue( Action.NAME, Messages.getString( "BundledResourceEditor.Edit" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final String selectedEntry = getSelectedEntry();
      if ( selectedEntry == null ) {
        return;
      }
      final String mimeType = bundle.getEntryMimeType( selectedEntry );

      final StringBufferWriter w = new StringBufferWriter( new StringBuffer() );

      try {
        final InputStream stream = bundle.getEntryAsStream( selectedEntry );
        try {
          final InputStreamReader r = new InputStreamReader( stream, "ISO-8859-1" ); // NON-NLS
          IOUtils.getInstance().copyWriter( r, w );
        } catch ( IOException ioe ) {
          ioe.printStackTrace();
        } finally {
          stream.close();
        }
      } catch ( IOException ioe ) {
        ioe.printStackTrace();
        return;
      }

      final TextAreaPropertyEditorDialog editorDialog = new TextAreaPropertyEditorDialog( BundledResourceEditor.this );
      final String originalValue = w.getBuffer().toString();
      final String editedValue = editorDialog.performEdit( originalValue );
      if ( ObjectUtilities.equal( originalValue, editedValue ) ) {
        return;
      }

      try {
        bundle.removeEntry( selectedEntry );
        final OutputStream outputStream = bundle.createEntry( selectedEntry, mimeType );
        try {
          outputStream.write( editedValue.getBytes( "ISO-8859-1" ) ); // NON-NLS
        } finally {
          outputStream.close();
        }
        bundle.getWriteableDocumentMetaData().setEntryAttribute( selectedEntry, BundleUtilities.STICKY_FLAG, "true" );
      } catch ( IOException ioe ) {
        ioe.printStackTrace();
      }
      changed = true;
    }
  }

  private class CreateFileAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private CreateFileAction() {
      putValue( Action.NAME, "Create" );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final CreateBundleFileDialog dialog = new CreateBundleFileDialog( BundledResourceEditor.this );
      if ( dialog.performCreateEntry( bundle ) == false ) {
        return;
      }

      final String fileEntryName = dialog.getFileName();
      if ( StringUtils.isEmpty( fileEntryName ) ) {
        return;
      }

      try {
        final OutputStream outputStream = bundle.createEntry( fileEntryName, dialog.getMimeType() );
        outputStream.close();
        bundle.getWriteableDocumentMetaData().setEntryAttribute( fileEntryName, BundleUtilities.STICKY_FLAG, "true" );
      } catch ( IOException e1 ) {
        e1.printStackTrace();
      }
      changed = true;
      refreshBundleList();
    }
  }

  private class CloseAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private CloseAction() {
      putValue( Action.NAME, Messages.getString( "BundledResourceEditor.Close" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      dispose();
    }
  }

  private class FileSelectionHandler implements ListSelectionListener {
    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      final boolean enable = resourceList.isSelectionEmpty() == false;
      removeFileAction.setEnabled( enable );
      exportFileAction.setEnabled( enable );

      if ( enable == false ) {
        editFileAction.setEnabled( enable );
      } else {
        final String s = getSelectedEntry();
        if ( s == null ) {
          return;
        }
        if ( "text/plain".equals( bundle.getEntryMimeType( s ) ) || s.endsWith( ".properties" ) ) // NON-NLS
        {
          editFileAction.setEnabled( true );
        } else {
          editFileAction.setEnabled( false );
        }
      }
    }
  }

  private ReportDesignerContext designerContext;
  private CreateFileAction createFileAction;
  private ImportFileAction importFileAction;
  private EditFileAction editFileAction;
  private RemoveFileAction removeFileAction;
  private DefaultListModel fileModel;
  private ExportFileAction exportFileAction;
  private JList resourceList;
  private boolean changed;
  private WriteableDocumentBundle bundle;

  public BundledResourceEditor( final ReportDesignerContext designerContext )
    throws HeadlessException {
    init( designerContext );
  }

  public BundledResourceEditor( final Frame owner, final ReportDesignerContext designerContext )
    throws HeadlessException {
    super( owner );
    init( designerContext );
  }

  public BundledResourceEditor( final Dialog owner, final ReportDesignerContext designerContext )
    throws HeadlessException {
    super( owner );
    init( designerContext );
  }

  private void init( final ReportDesignerContext designerContext ) {
    if ( designerContext == null ) {
      throw new NullPointerException();
    }
    this.designerContext = designerContext;
    setModal( true );
    setTitle( Messages.getString( "BundledResourceEditor.ResourceEditor" ) );
    setDefaultCloseOperation( DISPOSE_ON_CLOSE );

    createFileAction = new CreateFileAction();
    importFileAction = new ImportFileAction();

    editFileAction = new EditFileAction();
    editFileAction.setEnabled( false );
    removeFileAction = new RemoveFileAction();
    removeFileAction.setEnabled( false );
    exportFileAction = new ExportFileAction();
    exportFileAction.setEnabled( false );

    fileModel = new DefaultListModel();

    resourceList = new JList( fileModel );
    resourceList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    resourceList.setVisibleRowCount( 10 );
    resourceList.addListSelectionListener( new FileSelectionHandler() );

    final JPanel editorButtonsPanel = new JPanel();
    editorButtonsPanel.setLayout( new GridLayout( 5, 1, 5, 5 ) );
    editorButtonsPanel.add( new JButton( createFileAction ) );
    editorButtonsPanel.add( new JButton( importFileAction ) );
    editorButtonsPanel.add( new JButton( editFileAction ) );
    editorButtonsPanel.add( new JButton( removeFileAction ) );
    editorButtonsPanel.add( new JButton( exportFileAction ) );

    final JPanel editorButtonCarrier = new JPanel();
    editorButtonCarrier.setLayout( new BorderLayout() );
    editorButtonCarrier.add( editorButtonsPanel, BorderLayout.NORTH );

    final JPanel bottomButtonCarrier = new JPanel();
    bottomButtonCarrier.setLayout( new FlowLayout( FlowLayout.TRAILING ) );
    bottomButtonCarrier.add( new JButton( new CloseAction() ) );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
    contentPane.add( new JScrollPane( resourceList ), BorderLayout.CENTER );
    contentPane.add( editorButtonCarrier, BorderLayout.EAST );
    contentPane.add( bottomButtonCarrier, BorderLayout.SOUTH );
    setContentPane( contentPane );
    pack();
    LibSwingUtil.centerDialogInParent( this );


    final InputMap inputMap = contentPane.getInputMap();
    final ActionMap actionMap = contentPane.getActionMap();

    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "cancel" ); // NON-NLS
    actionMap.put( "cancel", new CloseAction() ); // NON-NLS

  }

  public String getSelectedEntry() {
    return (String) resourceList.getSelectedValue();
  }

  public boolean editResources() {
    final ReportDocumentContext activeContext = designerContext.getActiveContext();
    if ( activeContext == null ) {
      throw new IllegalStateException();
    }

    bundle = (WriteableDocumentBundle) activeContext.getContextRoot().getBundle();
    refreshBundleList();

    changed = false;
    setModal( true );
    setVisible( true );

    return changed;
  }

  private void refreshBundleList() {
    final WriteableDocumentMetaData metaData = bundle.getWriteableDocumentMetaData();
    final ArrayList<String> entries = new ArrayList<String>();
    final String[] manifestEntryNames = metaData.getManifestEntryNames();
    for ( int i = 0; i < manifestEntryNames.length; i++ ) {
      final String manifestEntryName = manifestEntryNames[ i ];

      final String stickyFlag = metaData.getEntryAttribute( manifestEntryName, BundleUtilities.STICKY_FLAG );
      final String hiddenFlag = metaData.getEntryAttribute( manifestEntryName, BundleUtilities.HIDDEN_FLAG );
      if ( "true".equals( stickyFlag ) && "true".equals( hiddenFlag ) == false ) // NON-NLS
      {
        entries.add( manifestEntryName );
      }
    }

    Collections.sort( entries );
    fileModel.clear();
    final int length = entries.size();
    for ( int i = 0; i < length; i++ ) {
      fileModel.addElement( entries.get( i ) );
    }
  }
}
