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

package org.pentaho.reporting.ui.datasources.olap4j;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetComboBoxModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetQuery;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinitionManager;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.ui.ConnectionPanel;
import org.pentaho.reporting.ui.datasources.jdbc.ui.LimitRowsCheckBoxActionListener;
import org.pentaho.reporting.ui.datasources.jdbc.ui.NamedDataSourceDialogModel;
import org.pentaho.reporting.ui.datasources.jdbc.ui.QueryLanguageListCellRenderer;

import javax.script.ScriptEngineFactory;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Michael D'Amour
 */
public abstract class Olap4JDataSourceEditor extends CommonDialog {
  private class QueryDocumentListener extends DocumentChangeHandler {
    protected void handleChange( final DocumentEvent e ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      final DataSetQuery<String> item = dialogModel.getQueries().getSelectedQuery();
      if ( item == null ) {
        return;
      }

      item.setQuery( queryTextArea.getText() );
      dialogModel.getQueries().fireItemChanged( item );
    }
  }

  private class PreviewAction extends AbstractAction implements PropertyChangeListener {
    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "Olap4JDataSourceEditor.Preview.Name" ) );
      setEnabled( dialogModel.isConnectionSelected() && dialogModel.isQuerySelected() );
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( dialogModel.isConnectionSelected() && dialogModel.isQuerySelected() );
    }

    public void actionPerformed( final ActionEvent evt ) {
      final JdbcConnectionDefinition connectionDefinition =
        (JdbcConnectionDefinition) dialogModel.getConnections().getSelectedItem();
      if ( connectionDefinition == null ) {
        return;
      }
      try {
        final String query = queryNameTextField.getText();
        Integer theMaxRows = 0;
        if ( maxPreviewRowsSpinner.isEnabled() ) {
          theMaxRows = (Integer) maxPreviewRowsSpinner.getValue();
        }
        if ( previewDialog == null ) {
          previewDialog = new DataPreviewDialog( Olap4JDataSourceEditor.this );
        }

        final AbstractMDXDataFactory dataFactory = createDataFactory();
        DataFactoryEditorSupport.configureDataFactoryForPreview( dataFactory, context );

        final Olap4JPreviewWorker worker = new Olap4JPreviewWorker( dataFactory, query, 0, theMaxRows );
        previewDialog.showData( worker );

        final ReportDataFactoryException factoryException = worker.getException();
        if ( factoryException != null ) {
          ExceptionDialog.showExceptionDialog( Olap4JDataSourceEditor.this,
            Messages.getString( "Olap4JDataSourceEditor.PreviewError.Title" ),
            Messages.getString( "Olap4JDataSourceEditor.PreviewError.Message" ), factoryException );
        }

      } catch ( Exception e ) {
        ExceptionDialog.showExceptionDialog( Olap4JDataSourceEditor.this,
          Messages.getString( "Olap4JDataSourceEditor.PreviewError.Title" ),
          Messages.getString( "Olap4JDataSourceEditor.PreviewError.Message" ), e );
      }
    }
  }

  private class QuerySelectedHandler implements ListSelectionListener {
    private QuerySelectedHandler() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      getDialogModel().getQueries().setSelectedItem( queryNameList.getSelectedValue() );

      final boolean querySelected = queryNameList.getSelectedIndex() != -1;
      queryNameTextField.setEnabled( querySelected );
      queryTextArea.setEnabled( dialogModel.isQuerySelected() );
      queryScriptTextArea.setEnabled( dialogModel.isQuerySelected() );
      queryLanguageField.setEnabled( dialogModel.isQuerySelected() );
    }
  }

  private class ConfirmEnableHandler implements PropertyChangeListener {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ConfirmEnableHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      validateInputs( false );
    }
  }

  private class AddQueryAction extends AbstractAction {
    protected AddQueryAction() {
      final URL resource = ConnectionPanel.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/olap4j/resources/Add.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "Olap4JDataSourceEditor.AddQuery.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "Olap4JDataSourceEditor.AddQuery.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final String queryName = dialogModel.generateQueryName();
      dialogModel.addQuery( queryName, "", null, null );
      queryNameList.setSelectedValue( queryName, true );
    }
  }

  private class RemoveQueryAction extends AbstractAction implements PropertyChangeListener {
    protected RemoveQueryAction() {
      final URL resource = ConnectionPanel.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/olap4j/resources/Remove.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "Olap4JDataSourceEditor.RemoveQuery.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "Olap4JDataSourceEditor.RemoveQuery.Description" ) );
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      setEnabled( dialogModel.isQuerySelected() );
    }

    public void actionPerformed( final ActionEvent e ) {
      final NamedDataSourceDialogModel dialogModel = getDialogModel();
      final DefaultComboBoxModel queries = dialogModel.getQueries();
      queries.removeElement( queries.getSelectedItem() );
      queries.setSelectedItem( null );
      queryNameList.clearSelection();
    }
  }

  private class QueryNameTextFieldDocumentListener extends DocumentChangeHandler implements ListDataListener {
    private boolean inUpdate;

    private QueryNameTextFieldDocumentListener() {
    }

    public void intervalAdded( final ListDataEvent e ) {
    }

    public void intervalRemoved( final ListDataEvent e ) {
    }

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
          queryScriptTextArea.setText( null );
          queryLanguageField.setSelectedItem( null );
          return;
        }

        setQueryName( selectedQuery.getQueryName() );
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
      final DataSetQuery item = (DataSetQuery) dialogModel.getQueries().getSelectedItem();
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

  private class GlobalTemplateAction extends AbstractAction {
    private URL resource;

    private GlobalTemplateAction() {
      putValue( Action.NAME, Messages.getString( "Olap4JDataSourceEditor.InsertTemplate" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( resource == null ) {
        return;
      }

      if ( StringUtils.isEmpty( globalScriptTextArea.getText(), true ) == false ) {
        if ( JOptionPane.showConfirmDialog( Olap4JDataSourceEditor.this,
          Messages.getString( "Olap4JDataSourceEditor.OverwriteScript" ),
          Messages.getString( "Olap4JDataSourceEditor.OverwriteScriptTitle" ),
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
      resource = Olap4JDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/scripts/global-template-" + key + ".txt" );
      setEnabled( resource != null );
    }
  }

  private class QueryTemplateAction extends AbstractAction {
    private URL resource;

    private QueryTemplateAction() {
      putValue( Action.NAME, Messages.getString( "Olap4JDataSourceEditor.InsertTemplate" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( resource == null ) {
        return;
      }

      if ( StringUtils.isEmpty( queryScriptTextArea.getText(), true ) == false ) {
        if ( JOptionPane.showConfirmDialog( Olap4JDataSourceEditor.this,
          Messages.getString( "Olap4JDataSourceEditor.OverwriteScript" ),
          Messages.getString( "Olap4JDataSourceEditor.OverwriteScriptTitle" ),
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
      resource = Olap4JDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/scripts/query-template-" + key + ".txt" );
      setEnabled( resource != null );
    }
  }

  private class UpdateScriptLanguageHandler implements ActionListener, ListSelectionListener {
    private UpdateScriptLanguageHandler() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final DataSetQuery<String> query = dialogModel.getQueries().getSelectedQuery();
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
    private QueryScriptDocumentListener() {
    }

    protected void handleChange( final DocumentEvent e ) {
      final DataSetQuery query = dialogModel.getQueries().getSelectedQuery();
      if ( query != null ) {
        query.setScript( queryScriptTextArea.getText() );
      }
    }
  }

  protected static final Log logger = LogFactory.getLog( Olap4JDataSourceEditor.class );

  private JList queryNameList;
  private JTextField queryNameTextField;
  private JTextArea queryTextArea;
  private NamedDataSourceDialogModel dialogModel;
  private JSpinner maxPreviewRowsSpinner;
  private DataPreviewDialog previewDialog;
  private OlapConnectionPanel connectionComponent;
  private DesignTimeContext context;

  private RSyntaxTextArea globalScriptTextArea;
  private SmartComboBox<ScriptEngineFactory> globalLanguageField;
  private RSyntaxTextArea queryScriptTextArea;
  private SmartComboBox<ScriptEngineFactory> queryLanguageField;
  private QueryLanguageListCellRenderer queryLanguageListCellRenderer;
  private GlobalTemplateAction globalTemplateAction;
  private QueryTemplateAction queryTemplateAction;

  public Olap4JDataSourceEditor( final DesignTimeContext context ) {
    init( context );
  }

  public Olap4JDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( owner );
    init( context );
  }

  public Olap4JDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( owner );
    init( context );
  }

  protected void init( final DesignTimeContext designTimeContext ) {
    setModal( true );

    this.context = designTimeContext;

    final QueryNameTextFieldDocumentListener updateHandler = new QueryNameTextFieldDocumentListener();

    globalTemplateAction = new GlobalTemplateAction();
    queryTemplateAction = new QueryTemplateAction();

    dialogModel = new NamedDataSourceDialogModel
      ( new JdbcConnectionDefinitionManager( "org/pentaho/reporting/ui/datasources/olap4j/Settings" ) );
    dialogModel.addPropertyChangeListener( NamedDataSourceDialogModel.CONNECTION_SELECTED, new ConfirmEnableHandler() );
    dialogModel.getQueries().addListDataListener( updateHandler );

    connectionComponent = new OlapConnectionPanel( dialogModel, designTimeContext );
    connectionComponent.setBorder( BorderFactory.createEmptyBorder( 0, 8, 0, 8 ) );

    maxPreviewRowsSpinner = new JSpinner( new SpinnerNumberModel( 10000, 1, Integer.MAX_VALUE, 1 ) );

    queryNameTextField = new JTextField();
    queryNameTextField.setColumns( 35 );
    queryNameTextField.setEnabled( dialogModel.isQuerySelected() );
    queryNameTextField.getDocument().addDocumentListener( updateHandler );

    queryTextArea = new JTextArea( (String) null );
    queryTextArea.setWrapStyleWord( true );
    queryTextArea.setLineWrap( true );
    queryTextArea.setRows( 10 );
    queryTextArea.setColumns( 50 );
    queryTextArea.setEnabled( dialogModel.isQuerySelected() );
    queryTextArea.getDocument().addDocumentListener( new QueryDocumentListener() );

    queryNameList = new JList( dialogModel.getQueries() );
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.addListSelectionListener( new QuerySelectedHandler() );

    globalScriptTextArea = new RSyntaxTextArea();
    globalScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );

    globalLanguageField = new SmartComboBox<ScriptEngineFactory>(
      new DefaultComboBoxModel( DataFactoryEditorSupport.getScriptEngineLanguages() ) );
    globalLanguageField.setRenderer( new QueryLanguageListCellRenderer() );
    globalLanguageField.addActionListener( new UpdateScriptLanguageHandler() );

    queryScriptTextArea = new RSyntaxTextArea();
    queryScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );
    queryScriptTextArea.getDocument().addDocumentListener( new QueryScriptDocumentListener() );

    queryLanguageListCellRenderer = new QueryLanguageListCellRenderer();

    queryLanguageField = new SmartComboBox<ScriptEngineFactory>(
      new DefaultComboBoxModel( DataFactoryEditorSupport.getScriptEngineLanguages() ) );
    queryLanguageField.setRenderer( queryLanguageListCellRenderer );
    queryLanguageField.addActionListener( new UpdateScriptLanguageHandler() );

    super.init();
    // Return the center panel
  }

  private void updateComponents() {
    final ScriptEngineFactory globalLanguage = (ScriptEngineFactory) globalLanguageField.getSelectedItem();
    globalScriptTextArea.setSyntaxEditingStyle
      ( DataFactoryEditorSupport.mapLanguageToSyntaxHighlighting( globalLanguage ) );
    queryLanguageListCellRenderer.setDefaultValue( globalLanguage );

    final ScriptEngineFactory queryScriptLanguage = (ScriptEngineFactory) queryLanguageField.getSelectedItem();
    if ( queryScriptLanguage == null ) {
      queryScriptTextArea.setSyntaxEditingStyle( globalScriptTextArea.getSyntaxEditingStyle() );
    } else {
      queryScriptTextArea
        .setSyntaxEditingStyle( DataFactoryEditorSupport.mapLanguageToSyntaxHighlighting( queryScriptLanguage ) );
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
    queryHeader2.add( new JLabel( Messages.getString( "Olap4JDataSourceEditor.QueryScript" ) ), BorderLayout.CENTER );
    queryHeader2.add( new JButton( queryTemplateAction ), BorderLayout.EAST );

    final JPanel queryScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    queryScriptHeader.add( new JLabel( Messages.getString( "Olap4JDataSourceEditor.QueryScriptLanguage" ) ) );
    queryScriptHeader.add( queryLanguageField );
    queryScriptHeader.add( queryHeader2 );

    final JPanel queryScriptContentHolder = new JPanel( new BorderLayout() );
    queryScriptContentHolder.add( queryScriptHeader, BorderLayout.NORTH );
    queryScriptContentHolder.add( new RTextScrollPane( 700, 300, queryScriptTextArea, true ), BorderLayout.CENTER );
    return queryScriptContentHolder;
  }

  private JPanel createGlobalScriptTab() {
    final JPanel globalHeader2 = new JPanel( new BorderLayout() );
    globalHeader2.add( new JLabel( Messages.getString( "Olap4JDataSourceEditor.GlobalScript" ) ), BorderLayout.CENTER );
    globalHeader2.add( new JButton( globalTemplateAction ), BorderLayout.EAST );

    final JPanel globalScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    globalScriptHeader.add( new JLabel( Messages.getString( "Olap4JDataSourceEditor.GlobalScriptLanguage" ) ) );
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
    tabbedPane.addTab( Messages.getString( "Olap4JDataSourceEditor.DataSource" ), dialogContent );
    tabbedPane.addTab( Messages.getString( "Olap4JDataSourceEditor.GlobalScripting" ), createGlobalScriptTab() );

    final JPanel contentPane = new JPanel( new BorderLayout() );
    contentPane.add( BorderLayout.SOUTH, createPreviewButtonsPanel() );
    contentPane.add( BorderLayout.CENTER, tabbedPane );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
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
    queryNamePanel
      .add( new JLabel( Messages.getString( "Olap4JDataSourceEditor.QueryNameLabel" ) ), BorderLayout.NORTH );
    queryNamePanel.add( queryNameTextField, BorderLayout.SOUTH );

    final JPanel queryControlsPanel = new JPanel( new BorderLayout() );
    queryControlsPanel
      .add( new JLabel( Messages.getString( "Olap4JDataSourceEditor.QueryLabel" ) ), BorderLayout.WEST );

    final JPanel queryPanel = new JPanel( new BorderLayout() );
    queryPanel.add( queryControlsPanel, BorderLayout.NORTH );
    queryPanel.add( new JScrollPane( queryTextArea ), BorderLayout.CENTER );
    queryPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 0, 8 ) );

    final JTabbedPane queryScriptTabPane = new JTabbedPane();
    queryScriptTabPane.addTab( Messages.getString( "Olap4JDataSourceEditor.StaticQuery" ), queryPanel );
    queryScriptTabPane.addTab( Messages.getString( "Olap4JDataSourceEditor.QueryScripting" ), createQueryScriptTab() );

    // Create the query details panel
    final JPanel queryDetailsPanel = new JPanel( new BorderLayout() );
    queryDetailsPanel.add( BorderLayout.NORTH, queryNamePanel );
    queryDetailsPanel.add( BorderLayout.CENTER, queryScriptTabPane );
    return queryDetailsPanel;
  }

  private JPanel createQueryListPanel() {
    // Create the query list panel
    final RemoveQueryAction queryRemoveAction = new RemoveQueryAction();
    dialogModel.addPropertyChangeListener( queryRemoveAction );

    final JPanel theQueryButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    theQueryButtonsPanel.add( new BorderlessButton( new AddQueryAction() ) );
    theQueryButtonsPanel.add( new BorderlessButton( queryRemoveAction ) );

    final JPanel theQueryControlsPanel = new JPanel( new BorderLayout() );
    theQueryControlsPanel
      .add( new JLabel( Messages.getString( "Olap4JDataSourceEditor.AvailableQueriesLabel" ) ), BorderLayout.WEST );
    theQueryControlsPanel.add( theQueryButtonsPanel, BorderLayout.EAST );

    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 8, 0, 8 ) );
    queryListPanel.add( BorderLayout.NORTH, theQueryControlsPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }

  public DataFactory performConfiguration( final AbstractNamedMDXDataFactory dataFactory,
                                           final String selectedQueryName ) {
    // Reset the ok / cancel flag
    dialogModel.clear();
    connectionComponent.setRoleField( null );

    // Initialize the internal storage

    // Load the current configuration
    if ( dataFactory != null ) {
      globalScriptTextArea.setText( dataFactory.getGlobalScript() );
      setGlobalScriptingLanguage( dataFactory.getGlobalScriptLanguage() );

      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[ i ];
        final String query = dataFactory.getQuery( queryName );
        final String scriptLanguage = dataFactory.getScriptingLanguage( queryName );
        final String script = dataFactory.getScript( queryName );
        dialogModel.addQuery( queryName, query, scriptLanguage, script );
      }
      dialogModel.setSelectedQuery( selectedQueryName );

      final OlapConnectionProvider currentJNDISource = dataFactory.getConnectionProvider();
      final JdbcConnectionDefinition definition = getConnectionPanel().createConnectionDefinition( currentJNDISource );
      getDialogModel().addConnection( definition );
      getDialogModel().getConnections().setSelectedItem( definition );

      getDialogModel().setJdbcUserField( dataFactory.getJdbcUserField() );
      getDialogModel().setJdbcPasswordField( dataFactory.getJdbcPasswordField() );
      connectionComponent.setRoleField( dataFactory.getRoleField() );
    }

    // Enable the dialog

    if ( !performEdit() ) {
      return null;
    }

    return createDataFactory();
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

  protected OlapConnectionProvider createConnectionProvider() {
    final JdbcConnectionDefinition connectionDefinition =
      (JdbcConnectionDefinition) getDialogModel().getConnections().getSelectedItem();
    final OlapConnectionProvider connectionProvider;
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
      throw new IllegalStateException();
    }
    return connectionProvider;
  }

  protected void configureQueries( final AbstractNamedMDXDataFactory dataFactory ) {
    dataFactory.setGlobalScriptLanguage( getGlobalScriptingLanguage() );
    if ( StringUtils.isEmpty( globalScriptTextArea.getText() ) == false ) {
      dataFactory.setGlobalScript( globalScriptTextArea.getText() );
    }

    final DataSetComboBoxModel<String> queries = dialogModel.getQueries();
    for ( int i = 0; i < queries.getSize(); i++ ) {
      final DataSetQuery<String> query = queries.getQuery( i );
      dataFactory.setQuery( query.getQueryName(), query.getQuery(), query.getScriptLanguage(), query.getScript() );
    }
  }

  protected abstract AbstractNamedMDXDataFactory createDataFactory();

  protected String getQueryName() {
    return queryNameTextField.getText();
  }

  protected void setQueryName( final String queryName ) {
    this.queryNameTextField.setText( queryName );
  }

  protected NamedDataSourceDialogModel getDialogModel() {
    return dialogModel;
  }

  protected OlapConnectionPanel getConnectionPanel() {
    return connectionComponent;
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    return ( dialogModel.isConnectionSelected() );
  }
}
