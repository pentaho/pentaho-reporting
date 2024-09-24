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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetComboBoxModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetQuery;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DriverDataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.JndiDataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianUtil;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.ui.JdbcConnectionPanel;
import org.pentaho.reporting.ui.datasources.jdbc.ui.LimitRowsCheckBoxActionListener;
import org.pentaho.reporting.ui.datasources.jdbc.ui.NamedDataSourceDialogModel;
import org.pentaho.reporting.ui.datasources.jdbc.ui.QueryLanguageListCellRenderer;

import javax.script.ScriptEngineFactory;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Michael D'Amour
 */
public abstract class MondrianDataSourceEditor extends CommonDialog {
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

      final FileFilter[] fileFilters = new FileFilter[] { new FilesystemFilter( new String[] { ".xml" }, // NON-NLS
        Messages.getString( "MondrianDataSourceEditor.FileName" ) + " (*.xml)", true ) }; // NON-NLS


      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( "mondrian" ); // NON-NLS
      fileChooser.setSelectedFile( initiallySelectedFile );
      fileChooser.setFilters( fileFilters );
      if ( fileChooser.showDialog( MondrianDataSourceEditor.this, JFileChooser.OPEN_DIALOG ) == false ) {
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
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ConfirmEnableHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      revalidate();
    }

    private void revalidate() {
      validateInputs( false );
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      // Enable Preview button since we have a schema file
      if ( ( dialogModel.isConnectionSelected() &&
        dialogModel.isQuerySelected() ) && ( dialogModel.isPreviewPossible() == false ) ) {
        dialogModel.setPreviewPossible( true );
      }

      revalidate();
    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      // Disable Preview button if no schema file has been specified
      if ( ( dialogModel.getSchemaFileNameField().getText().isEmpty() ) &&
        dialogModel.isPreviewPossible() ) {
        dialogModel.setPreviewPossible( false );
      }

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

  private class AddQueryAction extends AbstractAction {
    protected AddQueryAction() {
      final URL resource = MondrianDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/mondrian/resources/Add.png" );// NON-NLS
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "MondrianDataSourceEditor.AddQuery.Name" ) );// NON-NLS
      }
      putValue( Action.SHORT_DESCRIPTION,
        Messages.getString( "MondrianDataSourceEditor.AddQuery.Description" ) );// NON-NLS
    }

    public void actionPerformed( final ActionEvent e ) {
      // Find a unique query name
      final String queryName = dialogModel.generateQueryName();
      dialogModel.addQuery( queryName, "", null, null );
      queryNameList.setSelectedValue( queryName, true );
      queryNameList.setSelectedIndex( queryNameList.getLastVisibleIndex() );
    }
  }

  private class RemoveQueryAction extends AbstractAction implements PropertyChangeListener {
    protected RemoveQueryAction() {
      final URL resource = MondrianDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/mondrian/resources/Remove.png" );// NON-NLS
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "MondrianDataSourceEditor.RemoveQuery.Name" ) );// NON-NLS
      }
      putValue( Action.SHORT_DESCRIPTION,
        Messages.getString( "MondrianDataSourceEditor.RemoveQuery.Description" ) );// NON-NLS
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

  private class PreviewAction extends AbstractAction implements PropertyChangeListener {
    private DataPreviewDialog previewDialog;

    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "MondrianDataSourceEditor.Preview.Name" ) );
      setEnabled( dialogModel.isConnectionSelected() && dialogModel.isQuerySelected() );
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( dialogModel.isConnectionSelected() && dialogModel.isQuerySelected() && (
        dialogModel.getSchemaFileNameField().getText().length() != 0 ) );
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
          previewDialog = new DataPreviewDialog( MondrianDataSourceEditor.this );
        }

        final AbstractMDXDataFactory dataFactory = createDataFactory();
        DataFactoryEditorSupport.configureDataFactoryForPreview( dataFactory, context );

        final MondrianPreviewWorker worker = new MondrianPreviewWorker( dataFactory, query, 0, theMaxRows );
        previewDialog.showData( worker );

        final ReportDataFactoryException factoryException = worker.getException();
        if ( factoryException != null ) {
          ExceptionDialog.showExceptionDialog( MondrianDataSourceEditor.this,
            Messages.getString( "MondrianDataSourceEditor.PreviewError.Title" ),
            Messages.getString( "MondrianDataSourceEditor.PreviewError.Message" ), factoryException );
        }

      } catch ( Exception e ) {
        ExceptionDialog.showExceptionDialog( MondrianDataSourceEditor.this,
          Messages.getString( "MondrianDataSourceEditor.PreviewError.Title" ),
          Messages.getString( "MondrianDataSourceEditor.PreviewError.Message" ), e );
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

      item.setQuery( queryTextArea.getText() );
      dialogModel.getQueries().fireItemChanged( item );
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

      queryTemplateAction.update();
      if ( dialogModel.isQuerySelected() == false ) {
        queryTemplateAction.setEnabled( false );
      }
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
      securityDialog.setMondrianProperties( mondrianProperties );

      if ( securityDialog.performEdit() ) {
        roleText = securityDialog.getRole();
        roleField = securityDialog.getRoleField();
        jdbcUserText = securityDialog.getJdbcUser();
        jdbcUserField = securityDialog.getJdbcUserField();
        jdbcPasswordText = securityDialog.getJdbcPassword();
        jdbcPasswordField = securityDialog.getJdbcPasswordField();
        mondrianProperties = securityDialog.getMondrianProperties();
      }
    }
  }

  private class GlobalTemplateAction extends AbstractAction {
    private URL resource;

    private GlobalTemplateAction() {
      putValue( Action.NAME, Messages.getString( "MondrianDataSourceEditor.InsertTemplate" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( resource == null ) {
        return;
      }

      if ( StringUtils.isEmpty( globalScriptTextArea.getText(), true ) == false ) {
        if ( JOptionPane.showConfirmDialog( MondrianDataSourceEditor.this,
          Messages.getString( "MondrianDataSourceEditor.OverwriteScript" ),
          Messages.getString( "MondrianDataSourceEditor.OverwriteScriptTitle" ),
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
        logger.warn( "Unable to read template.", ex );// NON-NLS
      }
    }

    public void update() {
      String key = globalScriptTextArea.getSyntaxEditingStyle();
      if ( key.startsWith( "text/" ) )// NON-NLS
      {
        key = key.substring( 5 );
      }
      resource = MondrianDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/scripts/global-template-" + key
          + ".txt" );// NON-NLS
      setEnabled( resource != null );
    }
  }

  private class QueryTemplateAction extends AbstractAction {
    private URL resource;

    private QueryTemplateAction() {
      putValue( Action.NAME, Messages.getString( "MondrianDataSourceEditor.InsertTemplate" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( resource == null ) {
        return;
      }

      if ( StringUtils.isEmpty( queryScriptTextArea.getText(), true ) == false ) {
        if ( JOptionPane.showConfirmDialog( MondrianDataSourceEditor.this,
          Messages.getString( "MondrianDataSourceEditor.OverwriteScript" ),
          Messages.getString( "MondrianDataSourceEditor.OverwriteScriptTitle" ),
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
        logger.warn( "Unable to read template.", ex );// NON-NLS
      }
    }

    public void update() {
      String key = queryScriptTextArea.getSyntaxEditingStyle();
      if ( key.startsWith( "text/" ) )// NON-NLS
      {
        key = key.substring( 5 );
      }
      resource = MondrianDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/scripts/query-template-" + key
          + ".txt" );// NON-NLS
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
      final DataSetQuery<String> query = dialogModel.getQueries().getSelectedQuery();
      if ( query != null ) {
        query.setScript( queryScriptTextArea.getText() );
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

  protected static final Log logger = LogFactory.getLog( MondrianDataSourceEditor.class );

  private JList queryNameList;
  private JTextField queryNameTextField;
  private JTextField filenameField;
  private JTextField cubeConnectionNameField;
  private JTextArea queryTextArea;
  private NamedDataSourceDialogModel dialogModel;
  private JSpinner maxPreviewRowsSpinner;
  private DesignTimeContext context;
  private MondrianSecurityDialog securityDialog;
  private String jdbcUserText;
  private String jdbcUserField;
  private String jdbcPasswordText;
  private String jdbcPasswordField;
  private String roleText;
  private String roleField;
  private Properties mondrianProperties;

  private RSyntaxTextArea globalScriptTextArea;
  private SmartComboBox<ScriptEngineFactory> globalLanguageField;
  private RSyntaxTextArea queryScriptTextArea;
  private SmartComboBox<ScriptEngineFactory> queryLanguageField;
  private QueryLanguageListCellRenderer queryLanguageListCellRenderer;
  private GlobalTemplateAction globalTemplateAction;
  private QueryTemplateAction queryTemplateAction;

  public MondrianDataSourceEditor( final DesignTimeContext context ) {
    init( context );
  }

  public MondrianDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( owner );
    init( context );
  }

  public MondrianDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( owner );
    init( context );
  }

  protected void init( final DesignTimeContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    securityDialog = new MondrianSecurityDialog( this, context );

    setModal( true );

    this.context = context;

    globalTemplateAction = new GlobalTemplateAction();
    queryTemplateAction = new QueryTemplateAction();

    final QueryNameTextFieldDocumentListener updateHandler = new QueryNameTextFieldDocumentListener();
    final ConfirmEnableHandler confirmAction = new ConfirmEnableHandler();

    dialogModel = new NamedDataSourceDialogModel();
    dialogModel.getQueries().addListDataListener( updateHandler );
    dialogModel.addPropertyChangeListener( confirmAction );

    maxPreviewRowsSpinner = new JSpinner( new SpinnerNumberModel( 10000, 1, Integer.MAX_VALUE, 1 ) );

    cubeConnectionNameField = new JTextField( null, 0 );
    cubeConnectionNameField.setColumns( 30 );
    cubeConnectionNameField.getDocument().addDocumentListener( confirmAction );

    filenameField = new JTextField( null, 0 );
    filenameField.setColumns( 30 );
    filenameField.getDocument().addDocumentListener( confirmAction );
    dialogModel.setSchemaFileNameField( filenameField );

    queryNameTextField = new JTextField( null, 0 );
    queryNameTextField.setColumns( 35 );
    queryNameTextField.setEnabled( dialogModel.isQuerySelected() );
    queryNameTextField.getDocument().addDocumentListener( updateHandler );

    queryTextArea = new JTextArea( (String) null );
    queryTextArea.setWrapStyleWord( true );
    queryTextArea.setLineWrap( true );
    queryTextArea.setRows( 5 );
    queryTextArea.getDocument().addDocumentListener( new QueryDocumentListener() );

    queryNameList = new JList( getDialogModel().getQueries() );
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

    // Return the center panel
    super.init();
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
    queryHeader2.add( new JLabel( Messages.getString( "MondrianDataSourceEditor.QueryScript" ) ), BorderLayout.CENTER );
    queryHeader2.add( new JButton( queryTemplateAction ), BorderLayout.EAST );

    final JPanel queryScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    queryScriptHeader.add( new JLabel( Messages.getString( "MondrianDataSourceEditor.QueryScriptLanguage" ) ) );
    queryScriptHeader.add( queryLanguageField );
    queryScriptHeader.add( queryHeader2 );

    final JPanel queryScriptContentHolder = new JPanel( new BorderLayout() );
    queryScriptContentHolder.add( queryScriptHeader, BorderLayout.NORTH );
    queryScriptContentHolder.add( new RTextScrollPane( 700, 300, queryScriptTextArea, true ), BorderLayout.CENTER );
    return queryScriptContentHolder;
  }

  private JPanel createGlobalScriptTab() {
    final JPanel globalHeader2 = new JPanel( new BorderLayout() );
    globalHeader2
      .add( new JLabel( Messages.getString( "MondrianDataSourceEditor.GlobalScript" ) ), BorderLayout.CENTER );
    globalHeader2.add( new JButton( globalTemplateAction ), BorderLayout.EAST );

    final JPanel globalScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    globalScriptHeader.add( new JLabel( Messages.getString( "MondrianDataSourceEditor.GlobalScriptLanguage" ) ) );
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

    final JdbcConnectionPanel connectionPanel = new JdbcConnectionPanel( dialogModel, context );
    connectionPanel.setSecurityConfigurationAvailable( false );

    // Create the content panel
    final JPanel dialogContent = new JPanel( new BorderLayout() );
    dialogContent.add( BorderLayout.NORTH, createConnectionTopPanel() );
    dialogContent.add( BorderLayout.WEST, connectionPanel );
    dialogContent.add( BorderLayout.CENTER, queryContentPanel );

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab( Messages.getString( "MondrianDataSourceEditor.DataSource" ), dialogContent );
    tabbedPane.addTab( Messages.getString( "MondrianDataSourceEditor.GlobalScripting" ), createGlobalScriptTab() );

    final JPanel contentPane = new JPanel( new BorderLayout() );
    contentPane.add( BorderLayout.SOUTH, createPreviewButtonsPanel() );
    contentPane.add( BorderLayout.CENTER, tabbedPane );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
    return contentPane;
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

  private JPanel createQueryListPanel() {
    // Create the query list panel
    final RemoveQueryAction queryRemoveAction = new RemoveQueryAction();
    dialogModel.addPropertyChangeListener( queryRemoveAction );

    final JPanel theQueryButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    theQueryButtonsPanel.add( new BorderlessButton( new AddQueryAction() ) );
    theQueryButtonsPanel.add( new BorderlessButton( queryRemoveAction ) );

    final JPanel theQueryControlsPanel = new JPanel( new BorderLayout() );
    theQueryControlsPanel
      .add( new JLabel( Messages.getString( "MondrianDataSourceEditor.AvailableQueriesLabel" ) ), BorderLayout.WEST );
    theQueryControlsPanel.add( theQueryButtonsPanel, BorderLayout.EAST );

    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 8, 0, 8 ) );
    queryListPanel.add( BorderLayout.NORTH, theQueryControlsPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }


  private JPanel createQueryDetailsPanel() {
    final JPanel queryNamePanel = new JPanel( new BorderLayout() );
    queryNamePanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 0, 8 ) );
    queryNamePanel
      .add( new JLabel( Messages.getString( "MondrianDataSourceEditor.QueryNameLabel" ) ), BorderLayout.NORTH );
    queryNamePanel.add( queryNameTextField, BorderLayout.SOUTH );

    final JPanel queryControlPanel = new JPanel( new BorderLayout() );
    queryControlPanel
      .add( new JLabel( Messages.getString( "MondrianDataSourceEditor.QueryLabel" ) ), BorderLayout.WEST );

    final JPanel queryPanel = new JPanel( new BorderLayout() );
    queryPanel.add( queryControlPanel, BorderLayout.NORTH );
    queryPanel.add( new JScrollPane( queryTextArea ), BorderLayout.CENTER );
    queryPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 0, 8 ) );

    final JTabbedPane queryScriptTabPane = new JTabbedPane();
    queryScriptTabPane.addTab( Messages.getString( "MondrianDataSourceEditor.StaticQuery" ), queryPanel );
    queryScriptTabPane
      .addTab( Messages.getString( "MondrianDataSourceEditor.QueryScripting" ), createQueryScriptTab() );

    // Create the query details panel
    final JPanel queryDetailsPanel = new JPanel( new BorderLayout() );
    queryDetailsPanel.add( BorderLayout.NORTH, queryNamePanel );
    queryDetailsPanel.add( BorderLayout.CENTER, queryScriptTabPane );
    return queryDetailsPanel;
  }

  private JPanel createPreviewButtonsPanel() {
    final JPanel previewButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    previewButtonsPanel.add( new JCheckBox( new LimitRowsCheckBoxActionListener( maxPreviewRowsSpinner ) ) );
    previewButtonsPanel.add( maxPreviewRowsSpinner );

    final PreviewAction previewAction = new PreviewAction();
    dialogModel.addPropertyChangeListener( previewAction );
    previewButtonsPanel.add( new JButton( previewAction ) );
    return previewButtonsPanel;
  }

  protected abstract AbstractMDXDataFactory createDataFactory();

  public DataFactory performConfiguration( final AbstractNamedMDXDataFactory dataFactory,
                                           final String selectedQueryName ) {
    // Reset the ok / cancel flag
    getDialogModel().clear();
    roleText = null;
    roleField = null;
    jdbcUserText = null;
    jdbcUserField = null;
    jdbcPasswordText = null;
    jdbcPasswordField = null;
    mondrianProperties = null;

    // Load the current configuration
    if ( dataFactory != null ) {
      roleText = dataFactory.getRole();
      roleField = dataFactory.getRoleField();
      jdbcUserText = dataFactory.getJdbcUser();
      jdbcUserField = dataFactory.getJdbcUserField();
      jdbcPasswordText = dataFactory.getJdbcPassword();
      jdbcPasswordField = dataFactory.getJdbcPasswordField();
      mondrianProperties = dataFactory.getBaseConnectionProperties();

      setGlobalScriptingLanguage( dataFactory.getGlobalScriptLanguage() );
      globalScriptTextArea.setText( dataFactory.getGlobalScript() );

      final CubeFileProvider fileProvider = dataFactory.getCubeFileProvider();
      if ( fileProvider != null ) {
        setSchemaFileName( fileProvider.getDesignTimeFile() );
        cubeConnectionNameField.setText( fileProvider.getCubeConnectionName() );
      } else {
        setSchemaFileName( "" );
        cubeConnectionNameField.setText( "" );
      }

      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[ i ];
        final String query = dataFactory.getQuery( queryName );
        final String scriptLanguage = dataFactory.getScriptingLanguage( queryName );
        final String script = dataFactory.getScript( queryName );
        dialogModel.addQuery( queryName, query, scriptLanguage, script );
      }
      dialogModel.setSelectedQuery( selectedQueryName );

      final JdbcConnectionDefinition definition = createConnectionDefinition( dataFactory );
      getDialogModel().addConnection( definition );
      getDialogModel().getConnections().setSelectedItem( definition );
    }

    // Enable the dialog
    if ( !performEdit() ) {
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
        ( customName, dcp.getDriver(), dcp.getUrl(), null, null,
          dcp.getProperty( "::pentaho-reporting::hostname" ),// NON-NLS
          dcp.getProperty( "::pentaho-reporting::database-name" ),// NON-NLS
          dcp.getProperty( "::pentaho-reporting::database-type" ),// NON-NLS
          dcp.getProperty( "::pentaho-reporting::port" ),// NON-NLS
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
      return new JndiConnectionDefinition( customName, jcp.getConnectionPath(), null, null, null );
    }

    return null;
  }

  protected String getFileName() {
    return filenameField.getText();
  }

  public void setFileName( final String fileName ) {
    filenameField.setText( fileName );
  }

  protected NamedDataSourceDialogModel getDialogModel() {
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
    dataFactory.setBaseConnectionProperties( mondrianProperties );

    final JdbcConnectionDefinition connectionDefinition =
      (JdbcConnectionDefinition) getDialogModel().getConnections().getSelectedItem();
    dataFactory.setDesignTimeName( connectionDefinition.getName() );

    if ( connectionDefinition instanceof DriverConnectionDefinition ) {
      final DriverConnectionDefinition dcd = (DriverConnectionDefinition) connectionDefinition;
      final DriverDataSourceProvider dataSourceProvider = new DriverDataSourceProvider();
      dataSourceProvider.setUrl( dcd.getConnectionString() );
      dataSourceProvider.setDriver( dcd.getDriverClass() );
      final Properties properties = dcd.getProperties();
      final Enumeration keys = properties.keys();
      while ( keys.hasMoreElements() ) {
        final String key = (String) keys.nextElement();
        dataSourceProvider.setProperty( key, properties.getProperty( key ) );
      }
      dataFactory.setDataSourceProvider( dataSourceProvider );
    } else {
      final JndiConnectionDefinition jcd = (JndiConnectionDefinition) connectionDefinition;
      dataFactory.setDataSourceProvider( new JndiDataSourceProvider( jcd.getJndiName() ) );
    }
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

  protected String getQueryName() {
    return queryNameTextField.getText();
  }

  protected void setQueryName( final String queryName ) {
    this.queryNameTextField.setText( queryName );
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    final NamedDataSourceDialogModel dialogModel = getDialogModel();
    if ( dialogModel.isConnectionSelected() == false ) {
      return false;
    }

    if ( StringUtils.isEmpty( filenameField.getText(), true ) ) {
      return false;
    }

    return true;
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
