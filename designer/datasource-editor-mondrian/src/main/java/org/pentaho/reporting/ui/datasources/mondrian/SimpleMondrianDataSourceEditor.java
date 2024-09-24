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

package org.pentaho.reporting.ui.datasources.mondrian;


import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DriverDataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.JndiDataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianUtil;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.ui.JdbcConnectionPanel;
import org.pentaho.reporting.ui.datasources.jdbc.ui.SimpleDataSourceDialogModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Michael D'Amour
 */
public abstract class SimpleMondrianDataSourceEditor extends CommonDialog {
  private class BrowseAction extends AbstractAction {
    protected BrowseAction() {
      putValue( Action.NAME, Messages.getString( "MondrianDataSourceEditor.Browse.Name" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final File reportContextFile = DesignTimeUtil.getContextAsFile( context.getReport() );
      final File initiallySelectedFile;

      if ( StringUtils.isEmpty( getFileName(), true ) == false ) {
        if ( reportContextFile == null ) {
          initiallySelectedFile = new File( getFileName() );
        } else {
          initiallySelectedFile = new File( reportContextFile.getParentFile(), getFileName() );
        }
      } else {
        initiallySelectedFile = null;
      }

      final FileFilter[] fileFilters = new FileFilter[] { new FilesystemFilter( new String[] { ".xml" },
        Messages.getString( "MondrianDataSourceEditor.FileName" ) + " (*.xml)", true ) };


      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( "mondrian" );
      fileChooser.setSelectedFile( initiallySelectedFile );
      fileChooser.setFilters( fileFilters );
      if ( fileChooser.showDialog( SimpleMondrianDataSourceEditor.this, JFileChooser.OPEN_DIALOG ) == false ) {
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
      setFileName( path );
      autoRefreshSchemaName();
    }
  }

  private class ConfirmEnableHandler implements PropertyChangeListener, DocumentListener {
    private ConfirmEnableHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      revalidate();
    }

    private void revalidate() {
      final SimpleDataSourceDialogModel dialogModel = getDialogModel();
      getConfirmAction().setEnabled(
        dialogModel.isConnectionSelected() && StringUtils.isEmpty( filenameField.getText(), true ) == false );
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      revalidate();
    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      revalidate();
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate( final DocumentEvent e ) {
      revalidate();
    }
  }

  private class EditSecurityAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private EditSecurityAction() {
      putValue( Action.NAME, Messages.getString( "MondrianDataSourceEditor.EditSecurityAction.Name" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      securityDialog.setRoleField( roleField );
      securityDialog.setRole( roleText );
      securityDialog.setJdbcPassword( jdbcPasswordText );
      securityDialog.setJdbcPasswordField( jdbcPasswordField );
      securityDialog.setJdbcUser( jdbcUserText );
      securityDialog.setJdbcUserField( jdbcUserField );

      if ( securityDialog.performEdit() ) {
        roleText = securityDialog.getRole();
        roleField = securityDialog.getRoleField();
        jdbcUserText = securityDialog.getJdbcUser();
        jdbcUserField = securityDialog.getJdbcUserField();
        jdbcPasswordText = securityDialog.getJdbcPassword();
        jdbcPasswordField = securityDialog.getJdbcPasswordField();
      }
    }
  }

  private class RefreshSchemaNameAction extends AbstractAction {
    /**
     * Creates an {@code Action}.
     */
    private RefreshSchemaNameAction() {
      putValue( Action.NAME, Messages.getString( "MondrianDataSourceEditor.UpdateSchema.Name" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      refreshSchemaName();
    }
  }

  private JTextField filenameField;
  private JTextField cubeConnectionNameField;
  private SimpleDataSourceDialogModel dialogModel;
  private DesignTimeContext context;
  private MondrianSecurityDialog securityDialog;
  private String jdbcUserText;
  private String jdbcUserField;
  private String jdbcPasswordText;
  private String jdbcPasswordField;
  private String roleText;
  private String roleField;

  public SimpleMondrianDataSourceEditor( final DesignTimeContext context ) {
    init( context );
  }

  public SimpleMondrianDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( owner );
    init( context );
  }

  public SimpleMondrianDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( owner );
    init( context );
  }

  protected void init( final DesignTimeContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    setModal( true );

    securityDialog = new MondrianSecurityDialog( this, context );

    this.context = context;

    dialogModel = new SimpleDataSourceDialogModel();

    final ConfirmEnableHandler confirmAction = new ConfirmEnableHandler();
    dialogModel.addPropertyChangeListener( confirmAction );

    cubeConnectionNameField = new JTextField( null, 0 );
    cubeConnectionNameField.setColumns( 30 );
    cubeConnectionNameField.getDocument().addDocumentListener( confirmAction );

    filenameField = new JTextField( null, 0 );
    filenameField.setColumns( 30 );
    filenameField.getDocument().addDocumentListener( confirmAction );

    super.init();
  }

  protected Component createContentPane() {
    // Create the content panel
    final JPanel contentPanel = new JPanel( new BorderLayout() );
    contentPanel.add( BorderLayout.NORTH, createConnectionTopPanel() );
    contentPanel.add( BorderLayout.CENTER, new JdbcConnectionPanel( dialogModel, context ) );
    contentPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );

    return contentPanel;
  }

  private JPanel createConnectionTopPanel() {
    final JPanel masterPanel = new JPanel();
    masterPanel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 4;
    gbc.anchor = GridBagConstraints.WEST;
    masterPanel.add( new JLabel( Messages.getString( "MondrianDataSourceEditor.SchemaFileLabel" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    masterPanel.add( filenameField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    masterPanel.add( new JButton( new BrowseAction() ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    masterPanel.add( Box.createHorizontalStrut( 20 ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    masterPanel.add( new JButton( new EditSecurityAction() ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 4;
    gbc.anchor = GridBagConstraints.WEST;
    masterPanel.add( new JLabel( Messages.getString( "MondrianDataSourceEditor.CubeConnectionName" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 5;
    masterPanel.add( cubeConnectionNameField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    masterPanel.add( new JButton( new RefreshSchemaNameAction() ), gbc );

    return masterPanel;
  }

  protected abstract AbstractMDXDataFactory createDataFactory();


  public DataFactory performConfiguration( final AbstractMDXDataFactory dataFactory ) {
    // Reset the ok / cancel flag
    setConfirmed( false );
    getDialogModel().clear();

    roleText = null;
    roleField = null;
    jdbcUserText = null;
    jdbcUserField = null;
    jdbcPasswordText = null;
    jdbcPasswordField = null;

    // Load the current configuration
    if ( dataFactory != null ) {
      roleText = dataFactory.getRole();
      roleField = dataFactory.getRoleField();
      jdbcUserText = dataFactory.getJdbcUser();
      jdbcUserField = dataFactory.getJdbcUserField();
      jdbcPasswordText = dataFactory.getJdbcPassword();
      jdbcPasswordField = dataFactory.getJdbcPasswordField();

      final CubeFileProvider fileProvider = dataFactory.getCubeFileProvider();
      if ( fileProvider != null ) {
        setSchemaFileName( fileProvider.getDesignTimeFile() );
      } else {
        setSchemaFileName( "" );
      }

      final JdbcConnectionDefinition definition = createConnectionDefinition( dataFactory );
      getDialogModel().addConnection( definition );
      getDialogModel().getConnections().setSelectedItem( definition );
    }

    // Enable the dialog
    pack();
    setLocationRelativeTo( getParent() );
    setVisible( true );

    if ( !isConfirmed() ) {
      return null;
    }

    return createDataFactory();
  }

  protected JdbcConnectionDefinition createConnectionDefinition( final AbstractMDXDataFactory dataFactory ) {
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }

    String customName = dataFactory.getDesignTimeName();
    if ( customName == null ) {
      customName = Messages.getString( "MondrianDataSourceEditor.CustomConnection" );
    }

    final DataSourceProvider provider = dataFactory.getDataSourceProvider();
    if ( provider instanceof DriverDataSourceProvider ) {
      final DriverDataSourceProvider dcp = (DriverDataSourceProvider) provider;
      final ListModel model = dialogModel.getConnections();
      for ( int i = 0; i < model.getSize(); i++ ) {
        final JdbcConnectionDefinition definition = (JdbcConnectionDefinition) model.getElementAt( i );
        if ( definition instanceof DriverConnectionDefinition == false ) {
          continue;
        }
        final DriverConnectionDefinition dcd = (DriverConnectionDefinition) definition;
        if ( ObjectUtilities.equal( dcd.getDriverClass(), dcp.getDriver() ) &&
          ObjectUtilities.equal( dcd.getUsername(), dcp.getProperty( "user" ) ) &&
          ObjectUtilities.equal( dcd.getPassword(), dcp.getProperty( "password" ) ) &&
          ObjectUtilities.equal( dcd.getConnectionString(), dcp.getUrl() ) &&
          ObjectUtilities.equal( dcd.getName(), dcp.getProperty( "::pentaho-reporting::name" ) ) ) {
          return definition;
        }
      }

      final String[] strings = dcp.getPropertyNames();
      final Properties p = new Properties();
      for ( int i = 0; i < strings.length; i++ ) {
        final String string = strings[ i ];
        p.put( string, dcp.getProperty( string ) );
      }

      return new DriverConnectionDefinition
        ( customName, dcp.getDriver(), dcp.getUrl(),
          dataFactory.getJdbcUser(), dataFactory.getJdbcPassword(),
          dcp.getProperty( "::pentaho-reporting::hostname" ),
          dcp.getProperty( "::pentaho-reporting::database-name" ),
          dcp.getProperty( "::pentaho-reporting::database-type" ),
          dcp.getProperty( "::pentaho-reporting::port" ),
          p );
    } else if ( provider instanceof JndiDataSourceProvider ) {
      final JndiDataSourceProvider jcp = (JndiDataSourceProvider) provider;
      final ListModel model = dialogModel.getConnections();
      for ( int i = 0; i < model.getSize(); i++ ) {
        final JdbcConnectionDefinition definition = (JdbcConnectionDefinition) model.getElementAt( i );
        if ( definition instanceof JndiConnectionDefinition == false ) {
          continue;
        }
        final JndiConnectionDefinition dcd = (JndiConnectionDefinition) definition;

        if ( ObjectUtilities.equal( dcd.getJndiName(), jcp.getConnectionPath() ) ) {
          return dcd;
        }
      }
      return new JndiConnectionDefinition( customName, jcp.getConnectionPath(), null,
        dataFactory.getJdbcUser(), dataFactory.getJdbcPassword() );
    }

    return null;
  }

  protected String getFileName() {
    return filenameField.getText();
  }

  public void setFileName( final String fileName ) {
    filenameField.setText( fileName );
  }

  protected SimpleDataSourceDialogModel getDialogModel() {
    return dialogModel;
  }

  protected void setSchemaFileName( final String schema ) {
    this.filenameField.setText( schema );
  }

  protected String getSchemaFileName() {
    return this.filenameField.getText();
  }

  protected void configureConnection( final AbstractMDXDataFactory dataFactory ) {
    final CubeFileProvider cubeFileProvider =
      ClassicEngineBoot.getInstance().getObjectFactory().get( CubeFileProvider.class );
    cubeFileProvider.setDesignTimeFile( getSchemaFileName() );
    cubeFileProvider.setCubeConnectionName( cubeConnectionNameField.getText() );

    dataFactory.setCubeFileProvider( cubeFileProvider );
    dataFactory.setRole( roleText );
    dataFactory.setRoleField( roleField );
    dataFactory.setJdbcUser( jdbcUserText );
    dataFactory.setJdbcUserField( jdbcUserField );
    dataFactory.setJdbcPassword( jdbcPasswordText );
    dataFactory.setJdbcPasswordField( jdbcPasswordField );

    final JdbcConnectionDefinition connectionDefinition =
      (JdbcConnectionDefinition) getDialogModel().getConnections().getSelectedItem();
    dataFactory.setDesignTimeName( connectionDefinition.getName() );

    if ( connectionDefinition instanceof DriverConnectionDefinition ) {
      final DriverConnectionDefinition dcd = (DriverConnectionDefinition) connectionDefinition;
      dataFactory.setJdbcUser( dcd.getUsername() );
      dataFactory.setJdbcPassword( dcd.getPassword() );

      final DriverDataSourceProvider dataSourceProvider = new DriverDataSourceProvider();
      dataSourceProvider.setUrl( dcd.getConnectionString() );
      dataSourceProvider.setDriver( dcd.getDriverClass() );
      final Properties properties = dcd.getProperties();
      final Enumeration keys = properties.keys();
      while ( keys.hasMoreElements() ) {
        final String key = (String) keys.nextElement();
        if ( "user".equals( key ) || "password".equals( key ) ) {
          continue;
        }
        dataSourceProvider.setProperty( key, properties.getProperty( key ) );
      }
      dataFactory.setDataSourceProvider( dataSourceProvider );
    } else {
      final JndiConnectionDefinition jcd = (JndiConnectionDefinition) connectionDefinition;
      dataFactory.setDataSourceProvider( new JndiDataSourceProvider( jcd.getJndiName() ) );
      dataFactory.setJdbcUser( jcd.getUsername() );
      dataFactory.setJdbcPassword( jcd.getPassword() );
    }
  }

  protected void autoRefreshSchemaName() {
    if ( StringUtils.isEmpty( cubeConnectionNameField.getText() ) == false ) {
      return;
    }

    cubeConnectionNameField.setText( lookupSchemaName() );
  }

  private String lookupSchemaName() {
    final AbstractReportDefinition report = context.getReport();
    final MasterReport masterReport = DesignTimeUtil.getMasterReport( report );

    final ResourceManager resourceManager = masterReport.getResourceManager();
    final ResourceKey contextKey = masterReport.getContentBase();
    final String designTimeFile = filenameField.getText();
    return MondrianUtil.parseSchemaName( resourceManager, contextKey, designTimeFile );
  }

  protected void refreshSchemaName() {
    cubeConnectionNameField.setText( "" );
    autoRefreshSchemaName();
  }
}
