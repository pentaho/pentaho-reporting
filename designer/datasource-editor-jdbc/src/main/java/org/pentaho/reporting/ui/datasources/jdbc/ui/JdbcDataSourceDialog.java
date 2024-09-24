/*
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
 * Copyright (c) 2008 - 2020 Hitachi Vantara, .  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.script.ScriptEngineFactory;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nickyb.sqleonardo.querybuilder.QueryBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetComboBoxModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetQuery;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.LinkedMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.ui.datasources.jdbc.Messages;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;
import org.pentaho.reporting.engine.classic.core.MasterReport;

/**
 * @author David Kincade
 */
@SuppressWarnings( "HardCodedStringLiteral" )
public class JdbcDataSourceDialog extends CommonDialog {

  private class PreviewAction extends AbstractAction implements PropertyChangeListener {
    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "JdbcDataSourceDialog.Preview" ) );
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      setEnabled( dialogModel.isConnectionSelected() && dialogModel.isQuerySelected() );
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      setEnabled( dialogModel.isConnectionSelected() && dialogModel.isQuerySelected() );
    }

    public void actionPerformed( final ActionEvent evt ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      final JdbcConnectionDefinition connectionDefinition = (JdbcConnectionDefinition) dialogModel.getConnections().getSelectedItem();
      if ( connectionDefinition == null ) {
        return;
      }

      try {
        final String query = getQueryName();
        final DataPreviewDialog dialog = new DataPreviewDialog( JdbcDataSourceDialog.this );
        Integer maxRows = 0;
        if ( maxPreviewRowsSpinner.isEnabled() ) {
          maxRows = (Integer) maxPreviewRowsSpinner.getValue();
        }
        if ( maxRows == null ) {
          maxRows = 0;
        }
        final SQLReportDataFactory dataFactory = createDataFactory( connectionDefinition );
        DataFactoryEditorSupport.configureDataFactoryForPreview( dataFactory, designTimeContext );

        MasterReport report = (MasterReport) designTimeContext.getReport();
        ReportParameterDefinition parameters = null;

        if ( report != null ) {
          parameters = report.getParameterDefinition();
        }

        final JdbcPreviewWorker previewWorker = new JdbcPreviewWorker( dataFactory, query, 0, maxRows, parameters );
        dialog.showData( previewWorker );

        final ReportDataFactoryException theException = previewWorker.getException();
        if ( theException != null ) {
          ExceptionDialog.showExceptionDialog( JdbcDataSourceDialog.this,
              Messages.getString( "PreviewDialog.PreviewError.Title" ),
              Messages.getString( "PreviewDialog.PreviewError.Message" ), theException );
        }
      } catch ( Exception e ) {
        logger.warn( "Preview failed:", e );
        if ( designTimeContext != null ) {
          designTimeContext.userError( e );
        }
      }

    }
  }

  protected class InvokeQueryDesignerAction extends AbstractAction implements PropertyChangeListener {
    public static final String DEFAULT_SCHEMA = "PUBLIC";
    protected InvokeQueryDesignerAction() {
      final URL location = ConnectionPanel.class.getResource( "/org/pentaho/reporting/ui/datasources/jdbc/resources/Edit.png" );
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      }
      final NamedDataSourceDialogModel dialogModel = getDialogModel();

      enable( dialogModel );
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      enable( dialogModel );
    }

    /**
     * Enable or disable the query editor button (SQLeonardo)
     * <p/>
     * This is currently disabled for Hadoop's Hive JDBC as not
     * enough SQL is supported by the driver.
     *
     * @param dialogModel
     * @return
     */
    private void enable( final NamedDataSourceDialogModel dialogModel ) {
      boolean enable = true;

      if ( !( dialogModel.isConnectionSelected() && dialogModel.isQuerySelected() ) ) {
        // At least one of Connection or Query is not selected
        enable = false;
      } else {
        // Fetch the currently selected connection for inspection
        final Object connection = dialogModel.getConnections().getSelectedItem();

        if ( connection instanceof DriverConnectionDefinition ) {
          final DriverConnectionDefinition dcd = (DriverConnectionDefinition) connection;

          // There are some instances where the DatabaseType is coming back null
          if ( dcd.getDatabaseType() != null ) {
            if ( dcd.getDatabaseType().equalsIgnoreCase( "HIVE" ) ) { //$NON-NLS-1$
              // Disable SQLeonardo for Hadoop's Hive JDBC
              enable = false;
            }
          }
        }
      }

      setEnabled( enable );
    }

    public void actionPerformed( final ActionEvent e ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      final JdbcConnectionDefinition connectionDefinition = (JdbcConnectionDefinition) dialogModel.getConnections().getSelectedItem();
      if ( connectionDefinition == null ) {
        return;
      }

      Connection conn = null;
      try {
        final SQLReportDataFactory factory = createDataFactory( connectionDefinition );
        DataFactoryEditorSupport.configureDataFactoryForPreview( factory, designTimeContext );
        conn = factory.getConnectionProvider().createConnection( null, null );
        if ( conn == null ) {
          JOptionPane.showMessageDialog( JdbcDataSourceDialog.this,
              Messages.getString( "JdbcDataSourceDialog.InvokeQueryDesignerError" ),
              Messages.getString( "JdbcDataSourceDialog.InvokeQueryDesignerError.Title" ),
              JOptionPane.ERROR_MESSAGE );
          return;
        }

        final String schema = performQuerySchema( conn );

        final QueryBuilder queryBuilder = new QueryBuilder( conn );
        QueryBuilder.autoAlias = false;
        final JdbcQueryDesignerDialog queryDesigner = new JdbcQueryDesignerDialog( JdbcDataSourceDialog.this, queryBuilder );
        final String query = queryDesigner.designQuery( designTimeContext, factory.getConnectionProvider(),
                                                       schema, queryTextArea.getText() );
        if ( query != null ) {
          queryTextArea.setText( query );
        }
      } catch ( Exception e1 ) {
        logger.warn( "Invoking the query designer failed", e1 );
        ExceptionDialog.showExceptionDialog( JdbcDataSourceDialog.this,
            Messages.getString( "JdbcDataSourceDialog.QueryDesignerFailed.Title" ),
            Messages.getString( "JdbcDataSourceDialog.QueryDesignerFailed.Message" ),
            e1 );
      } finally {
        if ( conn != null ) {
          try {
            conn.close();
          } catch ( SQLException sqle ) {
            logger.warn( "Could not close database connection", sqle );
          }
        }
      }
    }

    protected String performQuerySchema( final Connection conn ) {
      String schema = DEFAULT_SCHEMA;
      try {
        final DatabaseMetaData data = conn.getMetaData();
        final boolean isHsql = ( "HSQL Database Engine".equals( data.getDatabaseProductName() ) );
        if ( data.supportsSchemasInTableDefinitions() ) {
          schema = null;
          final LinkedMap schemas = new LinkedMap();
          final ResultSet rs = data.getSchemas();
          while ( rs.next() ) {
            final String schemaName = rs.getString( 1 ).trim();
            if ( isHsql && "INFORMATION_SCHEMA".equals( schemaName ) ) {
              continue;
            }

            schemas.put( schemaName, Boolean.TRUE );
          }
          rs.close();

          // bring up schema selection dialog only if preferences is set
          final String[] schemasArray = (String[]) schemas.keys( new String[schemas.size()] );
          if ( schemas.size() > 1 ) {
            final Preferences properties = Preferences.userRoot().node( "org/pentaho/reporting/ui/datasources/jdbc/Settings" ); // NON-NLS
            if ( properties.getBoolean( "show-schema-dialog", false ) ) {
              final SchemaSelectionDialog schemaSelectionDialog = new SchemaSelectionDialog( JdbcDataSourceDialog.this, schemasArray );
              schema = schemaSelectionDialog.getSchema();
            }
          } else if ( schemas.size() == 1 ) {
            // Usually PUBLIC schema
            schema = schemasArray[0];
          }
        }
      } catch ( Exception ex ) {
        logger.warn( "Error on InvokeQueryDesignerAction.performQuerySchema()", ex );
      }
      return schema;
    }
  }

  private class QuerySelectedHandler implements ListSelectionListener {
    private QuerySelectedHandler() { }

    public void valueChanged( final ListSelectionEvent e ) {
      getDialogModel().getQueries().setSelectedItem( queryNameList.getSelectedValue() );

      final boolean querySelected = queryNameList.getSelectedIndex() != -1;
      queryNameTextField.setEnabled( querySelected );
      queryTextArea.setEnabled( dialogModel.isQuerySelected() );
      queryScriptTextArea.setEnabled( dialogModel.isQuerySelected() );
      queryLanguageField.setEnabled( dialogModel.isQuerySelected() );
    }
  }

  private class ConfirmValidationHandler implements PropertyChangeListener {
    private ConfirmValidationHandler() { }

    public void propertyChange( final PropertyChangeEvent evt ) {
      validateInputs( false );
    }
  }

  private class QueryAddAction extends AbstractAction {
    private QueryAddAction() {
      final URL location = ConnectionPanel.class.getResource( "/org/pentaho/reporting/ui/datasources/jdbc/resources/Add.png" );
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getString( "JdbcDataSourceDialog.Add.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "JdbcDataSourceDialog.Add.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final String queryName = dialogModel.generateQueryName();
      dialogModel.addQuery( queryName, "", null, null );
      queryNameList.setSelectedValue( queryName, true );
      queryNameList.setSelectedIndex( queryNameList.getLastVisibleIndex() );
    }
  }

  private class QueryRemoveAction extends AbstractAction implements PropertyChangeListener {
    private QueryRemoveAction() {
      final URL resource = ConnectionPanel.class.getResource( "/org/pentaho/reporting/ui/datasources/jdbc/resources/Remove.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "JdbcDataSourceDialog.Remove.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "JdbcDataSourceDialog.Remove.Description" ) );
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      setEnabled( dialogModel.isQuerySelected() );
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      setEnabled( dialogModel.isQuerySelected() );
    }

    public void actionPerformed( final ActionEvent e ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      final DefaultComboBoxModel queries = dialogModel.getQueries();
      queries.removeElement( queries.getSelectedItem() );
      if ( queryNameList.getLastVisibleIndex() != -1 ) {
        queryNameList.setSelectedValue( dialogModel.getQueries().getQuery( queryNameList.getLastVisibleIndex() ), true );
        queryNameList.setSelectedIndex( queryNameList.getLastVisibleIndex() );
        queries.setSelectedItem( dialogModel.getQueries().getQuery( queryNameList.getLastVisibleIndex( ) ) );
        queryTextArea.setEnabled( true );
      } else {
        queries.setSelectedItem( null );
        queryNameList.clearSelection();
        queryTextArea.setEnabled( false );
      }
    }
  }

  private class QueryNameTextFieldDocumentListener extends DocumentChangeHandler implements ListDataListener {
    private boolean inUpdate;

    private QueryNameTextFieldDocumentListener() { }

    public void intervalAdded( final ListDataEvent e ) { }

    public void intervalRemoved( final ListDataEvent e ) { }

    public void contentsChanged( final ListDataEvent e ) {
      if ( inUpdate ) {
        return;
      }
      if ( e.getIndex0() != -1 ) {
        return;
      }

      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      try {
        inUpdate = true;

        final DataSetQuery<String> selectedQuery = dialogModel.getQueries().getSelectedQuery();
        if ( selectedQuery == null ) {
          setQueryName( null );
          queryTextArea.setText( null );
          queryTextArea.setEnabled( false );
          queryLanguageField.setSelectedItem( null );
          queryScriptTextArea.setText( null );
          return;
        }

        setQueryName( selectedQuery.getQueryName() );
        setEnabled( true );
        queryTextArea.setText( selectedQuery.getQuery() );
        queryScriptTextArea.setText( selectedQuery.getScript() );
        setScriptingLanguage( selectedQuery.getScriptLanguage(), queryLanguageField );
      } finally {
        inUpdate = false;
      }
    }

    protected void handleChange( final DocumentEvent e ) {
      if ( inUpdate ) {
        return;
      }
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      final DataSetQuery item = dialogModel.getQueries().getSelectedQuery();
      if ( item == null ) {
        return;
      }

      try {
        inUpdate = true;
        item.setQueryName( getQueryName() );
        dialogModel.getQueries().fireItemChanged( item );
      } finally {
        inUpdate = false;
      }
    }
  }

  private class QueryDocumentListener extends DocumentChangeHandler {
    protected void handleChange( final DocumentEvent e ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      final DataSetQuery<String> item = dialogModel.getQueries().getSelectedQuery();
      if ( item == null ) {
        return;
      }
      //this is where we should set the selected item
      item.setQuery( queryTextArea.getText() );
      dialogModel.getQueries().fireItemChanged( item );
    }
  }

  private class GlobalTemplateAction extends AbstractAction {
    private URL resource;

    private GlobalTemplateAction() {
      putValue( Action.NAME, Messages.getString( "JdbcDataSourceDialog.InsertTemplate" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( resource == null ) {
        return;
      }

      if ( StringUtils.isEmpty( globalScriptTextArea.getText(), true ) == false ) {
        if ( JOptionPane.showConfirmDialog( JdbcDataSourceDialog.this,
            Messages.getString( "JdbcDataSourceDialog.OverwriteScript" ),
            Messages.getString( "JdbcDataSourceDialog.OverwriteScriptTitle" ),
            JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION ) {
          return;
        }
      }

      try {
        final InputStreamReader r = new InputStreamReader( resource.openStream(), "UTF-8" );
        try {
          final StringWriter w = new StringWriter();
          IOUtils.getInstance().copyWriter( r, w );

          globalScriptTextArea.setText( w.toString() );
        } finally {
          r.close();
        }
      } catch ( IOException ex ) {
        logger.warn( "Unable to read template.", ex );
      }
    }

    public void update() {
      String key = globalScriptTextArea.getSyntaxEditingStyle();
      if ( key.startsWith( "text/" ) ) {
        key = key.substring( 5 );
      }
      resource = JdbcDataSourceDialog.class.getResource( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/scripts/global-template-" + key + ".txt" );
      setEnabled( resource != null );
    }
  }

  private class QueryTemplateAction extends AbstractAction {
    private URL resource;

    private QueryTemplateAction() {
      putValue( Action.NAME, Messages.getString( "JdbcDataSourceDialog.InsertTemplate" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( resource == null ) {
        return;
      }

      if ( StringUtils.isEmpty( queryScriptTextArea.getText(), true ) == false ) {
        if ( JOptionPane.showConfirmDialog( JdbcDataSourceDialog.this,
            Messages.getString( "JdbcDataSourceDialog.OverwriteScript" ),
            Messages.getString( "JdbcDataSourceDialog.OverwriteScriptTitle" ),
            JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION ) {
          return;
        }
      }
      try {
        final InputStreamReader r = new InputStreamReader( resource.openStream(), "UTF-8" );
        try {
          final StringWriter w = new StringWriter();
          IOUtils.getInstance().copyWriter( r, w );

          queryScriptTextArea.insert( w.toString(), 0 );
        } finally {
          r.close();
        }
      } catch ( IOException ex ) {
        logger.warn( "Unable to read template.", ex );
      }
    }

    public void update() {
      String key = queryScriptTextArea.getSyntaxEditingStyle();
      if ( key.startsWith( "text/" ) ) {
        key = key.substring( 5 );
      }
      resource = JdbcDataSourceDialog.class.getResource( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/scripts/query-template-" + key + ".txt" );
      setEnabled( resource != null );
    }
  }

  private class UpdateScriptLanguageHandler implements ActionListener, ListSelectionListener {
    private UpdateScriptLanguageHandler() { }

    public void actionPerformed( final ActionEvent e ) {
      final DataSetQuery query = dialogModel.getQueries().getSelectedQuery();
      if ( query != null ) {
        final ScriptEngineFactory selectedItem = (ScriptEngineFactory) queryLanguageField.getSelectedItem();
        if ( selectedItem != null ) {
          query.setScriptLanguage( selectedItem.getLanguageName() );
        } else {
          query.setScriptLanguage( null );
        }
      }
      updateComponents();
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      updateComponents();
    }
  }

  private class QueryScriptDocumentListener extends DocumentChangeHandler {
    private QueryScriptDocumentListener() { }

    protected void handleChange( final DocumentEvent e ) {
      final DataSetQuery query = dialogModel.getQueries().getSelectedQuery();
      if ( query != null ) {
        String text = queryScriptTextArea.getText();
        query.setScript( text );
      }
    }
  }

  protected static final Log logger = LogFactory.getLog( JdbcDataSourceDialog.class );

  private JTextField queryNameTextField;
  private RSyntaxTextArea queryTextArea;
  private JList queryNameList;
  private NamedDataSourceDialogModel dialogModel;
  private JdbcConnectionPanel connectionComponent;
  private DesignTimeContext designTimeContext;
  private JSpinner maxPreviewRowsSpinner;

  private RSyntaxTextArea globalScriptTextArea;
  private SmartComboBox<ScriptEngineFactory> globalLanguageField;
  private RSyntaxTextArea queryScriptTextArea;
  private SmartComboBox<ScriptEngineFactory> queryLanguageField;
  private QueryLanguageListCellRenderer queryLanguageListCellRenderer;
  private GlobalTemplateAction globalTemplateAction;
  private QueryTemplateAction queryTemplateAction;

  public JdbcDataSourceDialog( final DesignTimeContext designTimeContext ) {
    initDialog( designTimeContext );
  }

  public JdbcDataSourceDialog( final DesignTimeContext designTimeContext, final JDialog parent ) {
    super( parent );
    initDialog( designTimeContext );
  }

  public JdbcDataSourceDialog( final DesignTimeContext designTimeContext, final JFrame parent ) {
    super( parent );
    initDialog( designTimeContext );
  }

  /**
   * Displays the dialog and returns the newly created JNDIDataSetReportElement
   *
   * @param dataFactory the datafactory to be configured or null to create a new one
   * @return the a clone of the configured datafactory or null on cancel.
   */
  public SQLReportDataFactory performConfiguration( final SQLReportDataFactory dataFactory,
                                                   final String selectedQueryName ) {
    dialogModel.clear();

    // Load the data from the current report element
    if ( dataFactory != null ) {
      dialogModel.setJdbcPasswordField( dataFactory.getPasswordField() );
      dialogModel.setJdbcUserField( dataFactory.getUserField() );
      setGlobalScriptingLanguage( dataFactory.getGlobalScriptLanguage() );
      globalScriptTextArea.setText( dataFactory.getGlobalScript() );

      // Save the JNDI query information
      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[i];
        final String query = dataFactory.getQuery( queryName );
        final String scriptLanguage = dataFactory.getScriptingLanguage( queryName );
        final String script = dataFactory.getScript( queryName );
        dialogModel.addQuery( queryName, query, scriptLanguage, script );
        queryNameList.setSelectedValue( queryName, true );
        queryNameList.setSelectedIndex( i );
      }

      final ConnectionProvider currentConnectionProvider = dataFactory.getConnectionProvider();
      final JdbcConnectionDefinition definition = connectionComponent.createConnectionDefinition( currentConnectionProvider );
      dialogModel.addConnection( definition );
      dialogModel.getConnections().setSelectedItem( definition );

      String selectedQuery = selectedQueryName;
      if ( StringUtils.isEmpty( selectedQuery ) ) {
        DataSetQuery query = dialogModel.getFirstQueryName();
        if ( query != null ) {
          selectedQuery = query.getQueryName();
        }
      }

      if ( StringUtils.isEmpty( selectedQuery ) == false ) {
        dialogModel.setSelectedQuery( selectedQuery );
        queryNameList.setSelectedIndex( dialogModel.getQueries().getIndexForQuery( selectedQuery ) );
      }
    }

    // Enable the dialog
    if ( performEdit() == false ) {
      return null;
    }

    final JdbcConnectionDefinition connectionDefinition = (JdbcConnectionDefinition) dialogModel.getConnections().getSelectedItem();
    if ( connectionDefinition == null ) {
      return null;
    }

    return createDataFactory( connectionDefinition );
  }

  private void setGlobalScriptingLanguage( final String lang ) {
    setScriptingLanguage( lang, globalLanguageField );
  }

  protected void setScriptingLanguage( final String lang, final JComboBox languageField ) {
    if ( lang == null ) {
      languageField.setSelectedItem( null );
      return;
    }

    final ListModel model = languageField.getModel();
    for ( int i = 0; i < model.getSize(); i++ ) {
      final ScriptEngineFactory elementAt = (ScriptEngineFactory) model.getElementAt( i );
      if ( elementAt == null ) {
        continue;
      }
      if ( elementAt.getNames().contains( lang ) ) {
        languageField.setSelectedItem( elementAt );
        return;
      }
    }
  }

  private String getGlobalScriptingLanguage() {
    final ScriptEngineFactory selectedValue = (ScriptEngineFactory) globalLanguageField.getSelectedItem();
    if ( selectedValue == null ) {
      return null;
    }
    return selectedValue.getLanguageName();
  }

  private SQLReportDataFactory createDataFactory( final JdbcConnectionDefinition connectionDefinition ) {
    final ConnectionProvider connectionProvider;
    if ( connectionDefinition instanceof JndiConnectionDefinition ) {
      final JndiConnectionDefinition jcd = (JndiConnectionDefinition) connectionDefinition;
      final JndiConnectionProvider provider = new JndiConnectionProvider();
      provider.setConnectionPath( jcd.getJndiName() );
      provider.setUsername( jcd.getUsername() );
      provider.setPassword( jcd.getPassword() );
      connectionProvider = provider;
    } else if ( connectionDefinition instanceof DriverConnectionDefinition ) {
      final DriverConnectionDefinition dcd = (DriverConnectionDefinition) connectionDefinition;
      final DriverConnectionProvider provider = new DriverConnectionProvider();
      provider.setDriver( dcd.getDriverClass() );
      provider.setUrl( dcd.getConnectionString() );

      final Properties properties = dcd.getProperties();
      final Enumeration keys = properties.keys();
      while ( keys.hasMoreElements() ) {
        final String key = (String) keys.nextElement();
        provider.setProperty( key, properties.getProperty( key ) );
      }
      connectionProvider = provider;
    } else {
      return null;
    }

    final SQLReportDataFactory newDataFactory = new SQLReportDataFactory( connectionProvider );
    newDataFactory.setPasswordField( dialogModel.getJdbcPasswordField() );
    newDataFactory.setUserField( dialogModel.getJdbcUserField() );
    newDataFactory.setGlobalScriptLanguage( getGlobalScriptingLanguage() );
    if ( StringUtils.isEmpty( globalScriptTextArea.getText() ) == false ) {
      newDataFactory.setGlobalScript( globalScriptTextArea.getText() );
    }

    final DataSetComboBoxModel<String> queries = dialogModel.getQueries();
    for ( int i = 0; i < queries.getSize(); i++ ) {
      final DataSetQuery<String> query = queries.getQuery( i );
      newDataFactory.setQuery( query.getQueryName(), query.getQuery(), query.getScriptLanguage(), query.getScript() );
    }
    return newDataFactory;
  }

  /**
   * Creates the panel which holds the main content of the dialog
   */
  private void initDialog( final DesignTimeContext designTimeContext ) {
    this.designTimeContext = designTimeContext;

    setTitle( Messages.getString( "JdbcDataSourceDialog.Title" ) );
    setModal( true );

    globalTemplateAction = new GlobalTemplateAction();
    queryTemplateAction = new QueryTemplateAction();

    dialogModel = new NamedDataSourceDialogModel();
    dialogModel.addPropertyChangeListener( new ConfirmValidationHandler() );

    connectionComponent = new JdbcConnectionPanel( dialogModel, designTimeContext );
    maxPreviewRowsSpinner = new JSpinner( new SpinnerNumberModel( 10000, 1, Integer.MAX_VALUE, 1 ) );

    final QueryNameTextFieldDocumentListener updateHandler = new QueryNameTextFieldDocumentListener();
    dialogModel.getQueries().addListDataListener( updateHandler );

    queryNameList = new JList( dialogModel.getQueries() );
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.addListSelectionListener( new QuerySelectedHandler() );

    queryNameTextField = new JTextField();
    queryNameTextField.setColumns( 35 );
    queryNameTextField.setEnabled( dialogModel.isQuerySelected() );
    queryNameTextField.getDocument().addDocumentListener( updateHandler );

    queryTextArea = new RSyntaxTextArea();
    queryTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_SQL );
    queryTextArea.setEnabled( dialogModel.isQuerySelected() );
    queryTextArea.getDocument().addDocumentListener( new QueryDocumentListener() );

    globalScriptTextArea = new RSyntaxTextArea();
    globalScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );

    globalLanguageField = new SmartComboBox<ScriptEngineFactory>( new DefaultComboBoxModel( DataFactoryEditorSupport.getScriptEngineLanguages() ) );
    globalLanguageField.setRenderer( new QueryLanguageListCellRenderer() );
    globalLanguageField.addActionListener( new UpdateScriptLanguageHandler() );

    queryScriptTextArea = new RSyntaxTextArea();
    queryScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );
    queryScriptTextArea.getDocument().addDocumentListener( new QueryScriptDocumentListener() );

    queryLanguageListCellRenderer = new QueryLanguageListCellRenderer();

    queryLanguageField = new SmartComboBox<ScriptEngineFactory>( new DefaultComboBoxModel( DataFactoryEditorSupport.getScriptEngineLanguages() ) );
    queryLanguageField.setRenderer( queryLanguageListCellRenderer );
    queryLanguageField.addActionListener( new UpdateScriptLanguageHandler() );

    super.init();
  }

  protected String getDialogId() {
    return "JdbcDataSourceEditor";
  }

  private void updateComponents() {
    final ScriptEngineFactory globalLanguage = (ScriptEngineFactory) globalLanguageField.getSelectedItem();
    globalScriptTextArea.setSyntaxEditingStyle( DataFactoryEditorSupport.mapLanguageToSyntaxHighlighting( globalLanguage ) );
    queryLanguageListCellRenderer.setDefaultValue( globalLanguage );

    final ScriptEngineFactory queryScriptLanguage = (ScriptEngineFactory) queryLanguageField.getSelectedItem();
    if ( queryScriptLanguage == null ) {
      queryScriptTextArea.setSyntaxEditingStyle( globalScriptTextArea.getSyntaxEditingStyle() );
    } else {
      queryScriptTextArea.setSyntaxEditingStyle( DataFactoryEditorSupport.mapLanguageToSyntaxHighlighting( queryScriptLanguage ) );
    }

    final boolean querySelected = dialogModel.isQuerySelected();
    queryScriptTextArea.setEnabled( querySelected );
    queryLanguageField.setEnabled( querySelected );
    queryTemplateAction.update();
    if ( querySelected == false ) {
      queryTemplateAction.setEnabled( false );
    }

    globalTemplateAction.update();

  }

  private JPanel createQueryScriptTab() {
    final JPanel queryHeader2 = new JPanel( new BorderLayout() );
    queryHeader2.add( new JLabel( Messages.getString( "JdbcDataSourceDialog.QueryScript" ) ), BorderLayout.CENTER );
    queryHeader2.add( new JButton( queryTemplateAction ), BorderLayout.EAST );

    final JPanel queryScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    queryScriptHeader.add( new JLabel( Messages.getString( "JdbcDataSourceDialog.QueryScriptLanguage" ) ) );
    queryScriptHeader.add( queryLanguageField );
    queryScriptHeader.add( queryHeader2 );

    final JPanel queryScriptContentHolder = new JPanel( new BorderLayout() );
    queryScriptContentHolder.add( queryScriptHeader, BorderLayout.NORTH );
    queryScriptContentHolder.add( new RTextScrollPane( 700, 300, queryScriptTextArea, true ), BorderLayout.CENTER );
    return queryScriptContentHolder;
  }

  private JPanel createGlobalScriptTab() {
    final JPanel globalHeader2 = new JPanel( new BorderLayout() );
    globalHeader2.add( new JLabel( Messages.getString( "JdbcDataSourceDialog.GlobalScript" ) ), BorderLayout.CENTER );
    globalHeader2.add( new JButton( globalTemplateAction ), BorderLayout.EAST );

    final JPanel globalScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    globalScriptHeader.add( new JLabel( Messages.getString( "JdbcDataSourceDialog.GlobalScriptLanguage" ) ) );
    globalScriptHeader.add( globalLanguageField );
    globalScriptHeader.add( globalHeader2 );

    final JPanel globalScriptContentHolder = new JPanel( new BorderLayout() );
    globalScriptContentHolder.add( globalScriptHeader, BorderLayout.NORTH );
    globalScriptContentHolder.add( new RTextScrollPane( 700, 600, globalScriptTextArea, true ), BorderLayout.CENTER );
    return globalScriptContentHolder;
  }

  protected Component createContentPane() {
    // Create the connection panel
    final JPanel queryContentPanel = new JPanel( new BorderLayout() );
    queryContentPanel.add( BorderLayout.NORTH, createQueryListPanel() );
    queryContentPanel.add( BorderLayout.CENTER, createQueryDetailsPanel() );

    // Create the content panel
    final JPanel dialogContent = new JPanel( new BorderLayout() );
    dialogContent.add( BorderLayout.WEST, connectionComponent );
    dialogContent.add( BorderLayout.CENTER, queryContentPanel );

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab( Messages.getString( "JdbcDataSourceDialog.DataSource" ), dialogContent );
    tabbedPane.addTab( Messages.getString( "JdbcDataSourceDialog.GlobalScripting" ), createGlobalScriptTab() );

    final JPanel contentPane = new JPanel( new BorderLayout() );
    contentPane.add( BorderLayout.SOUTH, createPreviewButtonsPanel() );
    contentPane.add( BorderLayout.CENTER, tabbedPane );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );

    // Return the center panel
    return contentPane;
  }

  private JPanel createPreviewButtonsPanel() {
    final JPanel previewButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    previewButtonsPanel.add( new JCheckBox( new LimitRowsCheckBoxActionListener( maxPreviewRowsSpinner ) ) );
    previewButtonsPanel.add( maxPreviewRowsSpinner );

    final PreviewAction thePreviewAction = new PreviewAction();
    dialogModel.addPropertyChangeListener( thePreviewAction );
    previewButtonsPanel.add( new JButton( thePreviewAction ) );
    return previewButtonsPanel;
  }

  private JPanel createQueryDetailsPanel() {
    final JPanel queryNamePanel = new JPanel( new BorderLayout() );
    queryNamePanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 0, 8 ) );
    queryNamePanel.add( new JLabel( Messages.getString( "JdbcDataSourceDialog.QueryStringLabel" ) ), BorderLayout.NORTH );
    queryNamePanel.add( queryNameTextField, BorderLayout.SOUTH );

    final InvokeQueryDesignerAction queryDesignerAction = new InvokeQueryDesignerAction();
    dialogModel.addPropertyChangeListener( queryDesignerAction );

    final JPanel queryButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    queryButtonsPanel.add( new BorderlessButton( queryDesignerAction ) );

    final JPanel queryControlsPanel = new JPanel( new BorderLayout() );
    queryControlsPanel.add( new JLabel( Messages.getString( "JdbcDataSourceDialog.QueryDetailsLabel" ) ), BorderLayout.WEST );
    queryControlsPanel.add( queryButtonsPanel, BorderLayout.EAST );

    final JPanel queryPanel = new JPanel( new BorderLayout() );
    queryPanel.add( queryControlsPanel, BorderLayout.NORTH );
    queryPanel.add( new RTextScrollPane( 500, 300, queryTextArea, true ), BorderLayout.CENTER );
    queryPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 0, 8 ) );

    final JTabbedPane queryScriptTabPane = new JTabbedPane();
    queryScriptTabPane.addTab( Messages.getString( "JdbcDataSourceDialog.StaticQuery" ), queryPanel );
    queryScriptTabPane.addTab( Messages.getString( "JdbcDataSourceDialog.QueryScripting" ), createQueryScriptTab() );

    // Create the query details panel
    final JPanel queryDetailsPanel = new JPanel( new BorderLayout() );
    queryDetailsPanel.add( BorderLayout.NORTH, queryNamePanel );
    queryDetailsPanel.add( BorderLayout.CENTER, queryScriptTabPane );
    return queryDetailsPanel;
  }

  private JPanel createQueryListPanel() {
    // Create the query list panel
    final QueryRemoveAction queryRemoveAction = new QueryRemoveAction();
    dialogModel.addPropertyChangeListener( queryRemoveAction );

    final JPanel theQueryButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    theQueryButtonsPanel.add( new BorderlessButton( new QueryAddAction() ) );
    theQueryButtonsPanel.add( new BorderlessButton( queryRemoveAction ) );

    final JPanel theQueryControlsPanel = new JPanel( new BorderLayout() );
    theQueryControlsPanel.add( new JLabel( Messages.getString( "JdbcDataSourceDialog.AvailableQueries" ) ), BorderLayout.WEST );
    theQueryControlsPanel.add( theQueryButtonsPanel, BorderLayout.EAST );

    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 8, 0, 8 ) );
    queryListPanel.add( BorderLayout.NORTH, theQueryControlsPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }

  protected NamedDataSourceDialogModel getDialogModel() {
    return dialogModel;
  }

  protected String getQueryName() {
    return queryNameTextField.getText();
  }

  protected void setQueryName( final String queryName ) {
    this.queryNameTextField.setText( queryName );
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    final NamedDataSourceDialogModel dialogModel = getDialogModel();
    return dialogModel.isConnectionSelected();
  }
}
