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

package org.pentaho.reporting.ui.datasources.pmd;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.SimplePmdDataFactory;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author David Kincade
 */
public class SimplePmdDataSourceEditor extends CommonDialog {
  private class BrowseAction extends AbstractAction {
    protected BrowseAction() {
      putValue( Action.NAME, Messages.getString( "PmdDataSourceEditor.Browse.Name" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final File initiallySelectedFile;
      final File reportContextFile = DesignTimeUtil.getContextAsFile( context.getReport() );
      if ( StringUtils.isEmpty( filenameField.getText(), true ) == false ) {
        if ( reportContextFile != null ) {
          initiallySelectedFile = new File( reportContextFile.getParentFile(), filenameField.getText() );
        } else {
          initiallySelectedFile = new File( filenameField.getText() );
        }
      } else {
        initiallySelectedFile = null; // NON-NLS
      }

      final FileFilter[] fileFilters = new FileFilter[] { new FilesystemFilter( new String[] { ".xmi" }, // NON-NLS
        Messages.getString( "PmdDataSourceEditor.XmiFileName" ) + " (*.xmi)", true ) }; // NON-NLS

      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( "xmifile" );
      fileChooser.setSelectedFile( initiallySelectedFile );
      fileChooser.setFilters( fileFilters );
      if ( fileChooser.showDialog( SimplePmdDataSourceEditor.this, JFileChooser.OPEN_DIALOG ) == false ) {
        return;
      }

      final File file = fileChooser.getSelectedFile();
      if ( file == null ) {
        return;
      }

      final String path;
      if ( reportContextFile != null ) {
        path = IOUtils.getInstance().createRelativePath( file.getPath(), reportContextFile.getAbsolutePath() );
      } else {
        path = file.getPath();
      }
      filenameField.setText( path );
    }
  }

  private class DomainTextFieldDocumentListener implements DocumentListener {
    public void insertUpdate( final DocumentEvent e ) {
      update();
    }

    public void removeUpdate( final DocumentEvent e ) {
      update();
    }

    public void changedUpdate( final DocumentEvent e ) {
      update();
    }

    private void update() {
      updateComponents();
    }
  }

  private class FilenameDocumentListener implements DocumentListener {
    public void insertUpdate( final DocumentEvent e ) {
      updateComponents();
    }

    public void removeUpdate( final DocumentEvent e ) {
      updateComponents();
    }

    public void changedUpdate( final DocumentEvent e ) {
      updateComponents();
    }
  }

  private JTextField domainIdTextField;
  private JTextField filenameField;
  private DesignTimeContext context;

  public SimplePmdDataSourceEditor( final DesignTimeContext context ) {
    init( context );
  }

  public SimplePmdDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( owner );
    init( context );
  }

  public SimplePmdDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( owner );
    init( context );
  }

  private void init( final DesignTimeContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    this.context = context;
    setModal( true );
    setTitle( Messages.getString( "PmdDataSourceEditor.Title" ) );

    filenameField = new JTextField( null, 0 );
    filenameField.setColumns( 30 );
    filenameField.getDocument().addDocumentListener( new FilenameDocumentListener() );

    domainIdTextField = new JTextField( null, 0 );
    domainIdTextField.setColumns( 35 );
    domainIdTextField.getDocument().addDocumentListener( new DomainTextFieldDocumentListener() );

    super.init();
  }

  protected String getDialogId() {
    return "PmdDataSourceEditor.Simple";
  }

  protected Component createContentPane() {

    final JPanel filePanel = new JPanel();
    filePanel.setLayout( new BoxLayout( filePanel, BoxLayout.X_AXIS ) );
    filePanel.add( filenameField );
    filePanel.add( new JButton( new BrowseAction() ) );

    final JPanel queryConfigurationPanel = new JPanel();
    queryConfigurationPanel.setLayout( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    queryConfigurationPanel.add( new JLabel( Messages.getString( "PmdDataSourceEditor.XmiFileLabel" ) ) );
    queryConfigurationPanel.add( filePanel );
    queryConfigurationPanel.add( new JLabel( Messages.getString( "PmdDataSourceEditor.DomainId" ) ) );
    queryConfigurationPanel.add( domainIdTextField );

    final JPanel contentPanel = new JPanel( new BorderLayout() );
    contentPanel.add( queryConfigurationPanel, BorderLayout.CENTER );
    contentPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    return contentPanel;
  }

  public SimplePmdDataFactory performConfiguration( final SimplePmdDataFactory dataFactory ) {
    // Load the current configuration
    if ( dataFactory != null ) {
      filenameField.setText( dataFactory.getXmiFile() );
      domainIdTextField.setText( dataFactory.getDomainId() );
    }

    // Prepare the data and the enable the proper buttons
    updateComponents();

    // Enable the dialog
    pack();
    setLocationRelativeTo( getParent() );

    if ( !performEdit() ) {
      return null;
    }

    return createDataFactory();
  }

  private SimplePmdDataFactory createDataFactory() {
    final SimplePmdDataFactory returnDataFactory = new SimplePmdDataFactory();
    returnDataFactory.setXmiFile( filenameField.getText() );
    returnDataFactory.setDomainId( domainIdTextField.getText() );
    returnDataFactory.setConnectionProvider( new PmdConnectionProvider() );

    return returnDataFactory;
  }

  protected void updateComponents() {
    final boolean isFileSelected = !StringUtils.isEmpty( filenameField.getText(), true );
    domainIdTextField.setEnabled( isFileSelected );
  }
}
