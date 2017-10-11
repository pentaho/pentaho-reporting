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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactory;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.base.util.XMLParserFactoryProducer;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.ui.datasources.pmd.util.CreateMqlEditorTask;
import org.pentaho.reporting.ui.datasources.pmd.util.DataSetQuery;
import org.pentaho.reporting.ui.datasources.pmd.util.LimitRowsCheckBoxActionListener;
import org.pentaho.reporting.ui.datasources.pmd.util.LoadRepositoryRunnable;
import org.pentaho.reporting.ui.datasources.pmd.util.QueryLanguageListCellRenderer;
import org.pentaho.reporting.ui.datasources.pmd.util.QueryNameListCellRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author David Kincade
 */
public class PmdDataSourceEditor extends CommonDialog {

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
      if ( fileChooser.showDialog( PmdDataSourceEditor.this, JFileChooser.OPEN_DIALOG ) == false ) {
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

  private class QueryNameListSelectionListener implements ListSelectionListener {
    public void valueChanged( final ListSelectionEvent e ) {
      if ( inQueryNameUpdate ) {
        return;
      }

      final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
      if ( query != null ) {
        queryNameTextField.setText( query.getQueryName() );
        queryTextArea.setText( query.getQuery() );
        queryScriptTextArea.setText( query.getScript() );
        setScriptingLanguage( query.getScriptLanguage(), queryLanguageField );
        updateComponents();
      } else {
        queryNameTextField.setText( "" );
        queryTextArea.setText( "" );
        queryScriptTextArea.setText( "" );
        setScriptingLanguage( null, queryLanguageField );
        updateComponents();
      }
    }

  }

  private class AddQueryAction extends AbstractAction {
    public AddQueryAction() {
      final URL resource = PmdDataSourceEditor.class.getResource(
        "/org/pentaho/reporting/ui/datasources/pmd/resources/Add.png" ); // NON-NLS
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "PmdDataSourceEditor.AddQuery.Name" ) );
      }

      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "PmdDataSourceEditor.AddQuery.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      // Find a unique query name
      String queryName = "PmdDataSourceEditor.Query";
      for ( int i = 1; i < 1000; ++i ) {
        final String newQueryName = Messages.getString( "PmdDataSourceEditor.Query" ) + " " + i;
        if ( !queries.containsKey( newQueryName ) ) {
          queryName = newQueryName;
          break;
        }
      }

      final DataSetQuery newQuery = new DataSetQuery( queryName, "", null, null );
      queries.put( newQuery.getQueryName(), newQuery );

      inModifyingQueryNameList = true;
      updateQueryList();
      queryNameList.setSelectedValue( newQuery, true );
      inModifyingQueryNameList = false;
      updateComponents();
    }
  }

  private class PrepareAndInvokeMqlEditorTask implements Runnable {
    private PrepareAndInvokeMqlEditorTask() {
    }

    public void run() {
      final DataSetQuery query = queries.get( queryNameTextField.getText() );
      if ( query == null ) {
        return;
      }

      try {
        final LoadRepositoryRunnable loadRepositoryRunnable =
          new LoadRepositoryRunnable( context, domainIdTextField.getText(), filenameField.getText() );
        final Thread loadRepositoryThread = new Thread( loadRepositoryRunnable );

        BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( loadRepositoryThread, null,
          PmdDataSourceEditor.this, Messages.getString( "PmdDataSourceEditor.PreviewTask" ) );
        final IMetadataDomainRepository repository = loadRepositoryRunnable.getRepository();
        if ( repository == null ) {
          return;
        }


        SwingUtilities.invokeLater( new CreateMqlEditorTask( repository, context, query, queryTextArea ) );
      } catch ( Exception exc ) {
        context.error( exc );
      }
    }
  }

  private class QueryDesignerAction extends AbstractAction {
    public QueryDesignerAction() {
      final URL resource = PmdDataSourceModule.class.getResource(
        "/org/pentaho/reporting/ui/datasources/pmd/resources/Edit.png" ); // NON-NLS
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "PmdDataSourceEditor.EditQuery.Name" ) );
      }

      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "PmdDataSourceEditor.EditQuery.Description" ) );
      setEnabled( false );
    }

    public void actionPerformed( final ActionEvent e ) {
      final Thread t = new Thread( new PrepareAndInvokeMqlEditorTask() );
      t.start();
    }
  }

  private class RemoveQueryAction extends AbstractAction {
    public RemoveQueryAction() {
      final URL resource = PmdDataSourceEditor.class.getResource(
        "/org/pentaho/reporting/ui/datasources/pmd/resources/Remove.png" ); // NON-NLS
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "PmdDataSourceEditor.RemoveQuery.Name" ) );
      }

      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "PmdDataSourceEditor.RemoveQuery.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
      if ( query != null ) {
        queries.remove( query.getQueryName() );
      }

      inModifyingQueryNameList = true;
      updateQueryList();
      queryNameList.clearSelection();
      inModifyingQueryNameList = false;
      updateComponents();
    }
  }

  private class QueryDocumentListener extends DocumentChangeHandler {
    private QueryDocumentListener() {
    }

    protected void handleChange( final DocumentEvent e ) {
      final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
      if ( query != null ) {
        query.setQuery( queryTextArea.getText() );
      }
    }
  }

  private class QueryScriptDocumentListener extends DocumentChangeHandler {
    private QueryScriptDocumentListener() {
    }

    protected void handleChange( final DocumentEvent e ) {
      final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
      if ( query != null ) {
        query.setScript( queryScriptTextArea.getText() );
      }
    }
  }

  private class QueryNameTextFieldDocumentListener extends DocumentChangeHandler {
    protected void handleChange( final DocumentEvent e ) {
      if ( inModifyingQueryNameList ) {
        return;
      }
      final String queryName = queryNameTextField.getText();
      final DataSetQuery currentQuery = (DataSetQuery) queryNameList.getSelectedValue();
      if ( currentQuery == null ) {
        return;
      }
      if ( queryName.equals( currentQuery.getQueryName() ) ) {
        return;
      }
      if ( queries.containsKey( queryName ) ) {
        return;
      }

      inQueryNameUpdate = true;
      queries.remove( currentQuery.getQueryName() );
      currentQuery.setQueryName( queryName );
      queries.put( currentQuery.getQueryName(), currentQuery );
      updateQueryList();
      queryNameList.setSelectedValue( currentQuery, true );
      inQueryNameUpdate = false;
    }
  }


  private class DomainTextFieldDocumentListener extends DocumentChangeHandler implements Runnable {
    protected void handleChange( final DocumentEvent e ) {
      updateComponents();
      SwingUtilities.invokeLater( this );
    }

    public void run() {
      updateQueries();
    }
  }

  private class FilenameDocumentListener extends DocumentChangeHandler {
    protected void handleChange( final DocumentEvent e ) {
      updateComponents();
    }
  }

  private class PreviewAction extends AbstractAction {
    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "PmdDataSourceEditor.Preview.Name" ) );
    }

    public void actionPerformed( final ActionEvent evt ) {
      try {
        final DataPreviewDialog previewDialog = new DataPreviewDialog( PmdDataSourceEditor.this );
        final String query = queryNameTextField.getText();
        Integer theMaxRows = 0;
        if ( maxPreviewRowsSpinner.isEnabled() ) {
          theMaxRows = (Integer) maxPreviewRowsSpinner.getValue();
        }

        final PmdDataFactory dataFactory = createDataFactory();
        DataFactoryEditorSupport.configureDataFactoryForPreview( dataFactory, context );

        final PmdPreviewWorker worker = new PmdPreviewWorker( dataFactory, query, 0, theMaxRows );
        previewDialog.showData( worker );

        final ReportDataFactoryException factoryException = worker.getException();
        if ( factoryException != null ) {
          ExceptionDialog.showExceptionDialog( PmdDataSourceEditor.this,
            Messages.getString( "PmdDataSourceEditor.PreviewError.Title" ),
            Messages.getString( "PmdDataSourceEditor.PreviewError.Message" ), factoryException );
        }
      } catch ( Exception e ) {
        context.error( e );
        ExceptionDialog.showExceptionDialog( PmdDataSourceEditor.this,
          Messages.getString( "PmdDataSourceEditor.PreviewError.Title" ),
          Messages.getString( "PmdDataSourceEditor.PreviewError.Message" ), e );
      }
    }
  }

  private class UpdateScriptLanguageHandler implements ActionListener, ListSelectionListener {
    private UpdateScriptLanguageHandler() {
    }

    public void actionPerformed( final ActionEvent e ) {
      final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
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

  private class GlobalTemplateAction extends AbstractAction {
    private URL resource;

    private GlobalTemplateAction() {
      putValue( Action.NAME, Messages.getString( "PmdDataSourceEditor.InsertTemplate" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( resource == null ) {
        return;
      }

      if ( StringUtils.isEmpty( globalScriptTextArea.getText(), true ) == false ) {
        if ( JOptionPane.showConfirmDialog( PmdDataSourceEditor.this,
          Messages.getString( "PmdDataSourceEditor.OverwriteScript" ),
          Messages.getString( "PmdDataSourceEditor.OverwriteScriptTitle" ),
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
      resource = PmdDataSourceEditor.class.getResource( "resources/global-template-" + key + ".txt" );
      setEnabled( resource != null );
    }
  }

  private class QueryTemplateAction extends AbstractAction {
    private URL resource;

    private QueryTemplateAction() {
      putValue( Action.NAME, Messages.getString( "PmdDataSourceEditor.InsertTemplate" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      if ( resource == null ) {
        return;
      }

      if ( StringUtils.isEmpty( queryScriptTextArea.getText(), true ) == false ) {
        if ( JOptionPane.showConfirmDialog( PmdDataSourceEditor.this,
          Messages.getString( "PmdDataSourceEditor.OverwriteScript" ),
          Messages.getString( "PmdDataSourceEditor.OverwriteScriptTitle" ),
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
      resource = PmdDataSourceEditor.class.getResource( "resources/query-template-" + key + ".txt" );
      setEnabled( resource != null );
    }
  }


  private static final Log logger = LogFactory.getLog( PmdDataSourceEditor.class );

  private JList queryNameList;
  private JButton queryRemoveButton;
  private JButton queryDesignerButton;
  private JButton queryAddButton;
  private JTextField domainIdTextField;
  private JTextField queryNameTextField;
  private JTextField filenameField;
  private RSyntaxTextArea queryTextArea;
  private Map<String, DataSetQuery> queries;
  private boolean inQueryNameUpdate;
  private boolean inModifyingQueryNameList;
  private DesignTimeContext context;
  private JSpinner maxPreviewRowsSpinner;
  private Action previewAction;

  private RSyntaxTextArea globalScriptTextArea;
  private SmartComboBox globalLanguageField;
  private RSyntaxTextArea queryScriptTextArea;
  private SmartComboBox queryLanguageField;
  private QueryLanguageListCellRenderer queryLanguageListCellRenderer;
  private GlobalTemplateAction globalTemplateAction;
  private QueryTemplateAction queryTemplateAction;

  public PmdDataSourceEditor( final DesignTimeContext context ) {
    init( context );
  }

  public PmdDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( owner );
    init( context );
  }

  public PmdDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
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

    maxPreviewRowsSpinner = new JSpinner( new SpinnerNumberModel( 10000, 1, Integer.MAX_VALUE, 1 ) );
    previewAction = new PreviewAction();
    globalTemplateAction = new GlobalTemplateAction();
    queryTemplateAction = new QueryTemplateAction();

    filenameField = new JTextField( null, 0 );
    filenameField.setColumns( 30 );
    filenameField.getDocument().addDocumentListener( new FilenameDocumentListener() );

    queryNameList = new JList();
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.addListSelectionListener( new QueryNameListSelectionListener() );
    queryNameList.setCellRenderer( new QueryNameListCellRenderer() );

    queryAddButton = new BorderlessButton( new AddQueryAction() );
    queryRemoveButton = new BorderlessButton( new RemoveQueryAction() );

    queryNameTextField = new JTextField( null, 0 );
    queryNameTextField.setColumns( 35 );
    queryNameTextField.getDocument().addDocumentListener( new QueryNameTextFieldDocumentListener() );

    domainIdTextField = new JTextField( null, 0 );
    domainIdTextField.setColumns( 35 );
    domainIdTextField.getDocument().addDocumentListener( new DomainTextFieldDocumentListener() );

    queryTextArea = new RSyntaxTextArea();
    queryTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_XML );
    queryTextArea.setWrapStyleWord( true );
    queryTextArea.setLineWrap( true );
    queryTextArea.setRows( 5 );
    queryTextArea.getDocument().addDocumentListener( new QueryDocumentListener() );

    queryDesignerButton = new JButton( new QueryDesignerAction() );
    queryDesignerButton.setEnabled( false );
    queryDesignerButton.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );

    globalScriptTextArea = new RSyntaxTextArea();
    globalScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );

    globalLanguageField = new SmartComboBox( new DefaultComboBoxModel( getScriptEngineLanguages() ) );
    globalLanguageField.setRenderer( new QueryLanguageListCellRenderer() );
    globalLanguageField.addActionListener( new UpdateScriptLanguageHandler() );

    queryScriptTextArea = new RSyntaxTextArea();
    queryScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );
    queryScriptTextArea.getDocument().addDocumentListener( new QueryScriptDocumentListener() );

    queryLanguageListCellRenderer = new QueryLanguageListCellRenderer();

    queryLanguageField = new SmartComboBox( new DefaultComboBoxModel( getScriptEngineLanguages() ) );
    queryLanguageField.setRenderer( queryLanguageListCellRenderer );
    queryLanguageField.addActionListener( new UpdateScriptLanguageHandler() );

    super.init();
  }

  protected String getDialogId() {
    return "PmdDataSourceEditor";
  }

  private ScriptEngineFactory[] getScriptEngineLanguages() {
    final LinkedHashSet<ScriptEngineFactory> langSet = new LinkedHashSet<ScriptEngineFactory>();
    langSet.add( null );
    final List<ScriptEngineFactory> engineFactories = new ScriptEngineManager().getEngineFactories();
    for ( final ScriptEngineFactory engineFactory : engineFactories ) {
      langSet.add( engineFactory );
    }
    return langSet.toArray( new ScriptEngineFactory[ langSet.size() ] );
  }

  protected Component createContentPane() {
    final JPanel queryTextAreaHeaderPanel = new JPanel( new BorderLayout() );
    queryTextAreaHeaderPanel
      .add( new JLabel( Messages.getString( "PmdDataSourceEditor.QueryLabel" ) ), BorderLayout.WEST );
    queryTextAreaHeaderPanel.add( queryDesignerButton, BorderLayout.EAST );

    final JPanel queryConfigurationPanel = new JPanel();
    queryConfigurationPanel.setLayout( new BorderLayout() );
    queryConfigurationPanel.add( queryTextAreaHeaderPanel, BorderLayout.NORTH );
    queryConfigurationPanel.add( new RTextScrollPane( 700, 500, queryTextArea, true ), BorderLayout.CENTER );

    final JTabbedPane queryScriptTabPane = new JTabbedPane();
    queryScriptTabPane.addTab( Messages.getString( "PmdDataSourceEditor.StaticQuery" ), queryConfigurationPanel );
    queryScriptTabPane.addTab( Messages.getString( "PmdDataSourceEditor.QueryScripting" ), createQueryScriptTab() );

    final JPanel queryAreaPanel = new JPanel();
    queryAreaPanel.setLayout( new BorderLayout() );
    queryAreaPanel.add( createGlobalPropertiesPanel(), BorderLayout.NORTH );
    queryAreaPanel.add( queryScriptTabPane, BorderLayout.CENTER );

    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab( Messages.getString( "PmdDataSourceEditor.DataSource" ), queryAreaPanel );
    tabbedPane.addTab( Messages.getString( "PmdDataSourceEditor.GlobalScripting" ), createGlobalScriptTab() );

    final JPanel contentPanel = new JPanel( new BorderLayout() );
    contentPanel.add( tabbedPane, BorderLayout.CENTER );
    contentPanel.add( createPreviewButtonsPanel(), BorderLayout.SOUTH );
    contentPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    return contentPanel;
  }

  private JPanel createQueryScriptTab() {
    final JPanel queryHeader2 = new JPanel( new BorderLayout() );
    queryHeader2.add( new JLabel( Messages.getString( "PmdDataSourceEditor.QueryScript" ) ), BorderLayout.CENTER );
    queryHeader2.add( new JButton( queryTemplateAction ), BorderLayout.EAST );

    final JPanel queryScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    queryScriptHeader.add( new JLabel( Messages.getString( "PmdDataSourceEditor.QueryScriptLanguage" ) ) );
    queryScriptHeader.add( queryLanguageField );
    queryScriptHeader.add( queryHeader2 );

    final JPanel queryScriptContentHolder = new JPanel( new BorderLayout() );
    queryScriptContentHolder.add( queryScriptHeader, BorderLayout.NORTH );
    queryScriptContentHolder.add( new RTextScrollPane( 700, 300, queryScriptTextArea, true ), BorderLayout.CENTER );
    return queryScriptContentHolder;
  }

  private JPanel createGlobalScriptTab() {
    final JPanel globalHeader2 = new JPanel( new BorderLayout() );
    globalHeader2.add( new JLabel( Messages.getString( "PmdDataSourceEditor.GlobalScript" ) ), BorderLayout.CENTER );
    globalHeader2.add( new JButton( globalTemplateAction ), BorderLayout.EAST );

    final JPanel globalScriptHeader = new JPanel( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    globalScriptHeader.add( new JLabel( Messages.getString( "PmdDataSourceEditor.GlobalScriptLanguage" ) ) );
    globalScriptHeader.add( globalLanguageField );
    globalScriptHeader.add( globalHeader2 );

    final JPanel globalScriptContentHolder = new JPanel( new BorderLayout() );
    globalScriptContentHolder.add( globalScriptHeader, BorderLayout.NORTH );
    globalScriptContentHolder.add( new RTextScrollPane( 700, 600, globalScriptTextArea, true ), BorderLayout.CENTER );
    return globalScriptContentHolder;
  }

  private JPanel createGlobalPropertiesPanel() {
    final JPanel filePanel = new JPanel();
    filePanel.setLayout( new BoxLayout( filePanel, BoxLayout.X_AXIS ) );
    filePanel.add( filenameField );
    filePanel.add( new JButton( new BrowseAction() ) );

    final JPanel queryListButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    queryListButtonsPanel.add( queryAddButton );
    queryListButtonsPanel.add( queryRemoveButton );

    final JPanel queryListButtonsPanelWrapper = new JPanel( new BorderLayout() );
    queryListButtonsPanelWrapper
      .add( new JLabel( Messages.getString( "PmdDataSourceEditor.AvailableQueries" ) ), BorderLayout.WEST );
    queryListButtonsPanelWrapper.add( queryListButtonsPanel, BorderLayout.EAST );

    final JPanel dataSourceConfigurationPanel = new JPanel();
    dataSourceConfigurationPanel.setLayout( new VerticalLayout( 5, VerticalLayout.BOTH, VerticalLayout.TOP ) );
    dataSourceConfigurationPanel.add( new JLabel( Messages.getString( "PmdDataSourceEditor.XmiFileLabel" ) ) );
    dataSourceConfigurationPanel.add( filePanel );
    dataSourceConfigurationPanel.add( new JLabel( Messages.getString( "PmdDataSourceEditor.DomainId" ) ) );
    dataSourceConfigurationPanel.add( domainIdTextField );
    dataSourceConfigurationPanel.add( queryListButtonsPanelWrapper );
    dataSourceConfigurationPanel.add( new JScrollPane( queryNameList ) );
    dataSourceConfigurationPanel.add( new JLabel( Messages.getString( "PmdDataSourceEditor.QueryName" ) ) );
    dataSourceConfigurationPanel.add( queryNameTextField );
    return dataSourceConfigurationPanel;
  }

  private JPanel createPreviewButtonsPanel() {
    final JPanel previewButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    previewButtonsPanel.add( new JCheckBox( new LimitRowsCheckBoxActionListener( maxPreviewRowsSpinner ) ) );
    previewButtonsPanel.add( maxPreviewRowsSpinner );
    previewButtonsPanel.add( new JButton( previewAction ) );
    return previewButtonsPanel;
  }

  public PmdDataFactory performConfiguration( final PmdDataFactory dataFactory, final String selectedQuery ) {
    // Initialize the internal storage
    queries = new TreeMap<String, DataSetQuery>();

    // Load the current configuration
    if ( dataFactory != null ) {
      filenameField.setText( dataFactory.getXmiFile() );
      domainIdTextField.setText( dataFactory.getDomainId() );
      setGlobalScriptingLanguage( dataFactory.getGlobalScriptLanguage() );
      globalScriptTextArea.setText( dataFactory.getGlobalScript() );

      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[ i ];
        final String query = dataFactory.getQuery( queryName );
        final String scriptLanguage = dataFactory.getScriptingLanguage( queryName );
        final String script = dataFactory.getScript( queryName );
        queries.put( queryName, new DataSetQuery( queryName, query, scriptLanguage, script ) );
      }
    }

    // Prepare the data and the enable the proper buttons
    updateComponents();
    updateQueryList();
    setSelectedQuery( selectedQuery );

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

  private PmdDataFactory createDataFactory() {
    final PmdDataFactory returnDataFactory = new PmdDataFactory();
    returnDataFactory.setXmiFile( filenameField.getText() );
    returnDataFactory.setDomainId( domainIdTextField.getText() );
    returnDataFactory.setConnectionProvider( new PmdConnectionProvider() );
    returnDataFactory.setGlobalScriptLanguage( getGlobalScriptingLanguage() );
    if ( StringUtils.isEmpty( globalScriptTextArea.getText() ) == false ) {
      returnDataFactory.setGlobalScript( globalScriptTextArea.getText() );
    }

    for ( final DataSetQuery query : this.queries.values() ) {
      returnDataFactory
        .setQuery( query.getQueryName(), query.getQuery(), query.getScriptLanguage(), query.getScript() );
    }
    return returnDataFactory;
  }

  protected void updateQueryList() {
    if ( !queries.isEmpty() ) {
      queryNameList.setListData( queries.values().toArray( new DataSetQuery[ queries.size() ] ) );
    } else {
      queryNameList.setListData( new Object[ 0 ] );
    }
  }

  private void setSelectedQuery( final String query ) {
    final ListModel listModel = queryNameList.getModel();
    for ( int i = 0; i < listModel.getSize(); i++ ) {
      final DataSetQuery dataSet = (DataSetQuery) listModel.getElementAt( i );
      if ( dataSet.getQueryName().equals( query ) ) {
        queryNameList.setSelectedValue( dataSet, true );
        break;
      }
    }
  }

  protected void updateComponents() {
    final boolean querySelected = queryNameList.getSelectedIndex() != -1;
    final boolean hasQueries = queryNameList.getModel().getSize() > 0;
    final boolean isFileSelected = !StringUtils.isEmpty( filenameField.getText(), true );
    final boolean hasDomain = !StringUtils.isEmpty( domainIdTextField.getText(), true );

    queryLanguageListCellRenderer.setDefaultValue( (ScriptEngineFactory) globalLanguageField.getSelectedItem() );

    domainIdTextField.setEnabled( isFileSelected );

    previewAction.setEnabled( isFileSelected && querySelected );
    queryNameTextField.setEnabled( querySelected );
    queryTextArea.setEnabled( querySelected );
    queryRemoveButton.setEnabled( querySelected );
    queryDesignerButton.setEnabled( hasDomain && querySelected && isFileSelected );
    queryAddButton.setEnabled( true );

    globalScriptTextArea.setSyntaxEditingStyle( mapLanguageToSyntaxHighlighting(
      (ScriptEngineFactory) globalLanguageField.getSelectedItem() ) );

    final ScriptEngineFactory queryScriptLanguage = (ScriptEngineFactory) queryLanguageField.getSelectedItem();
    if ( queryScriptLanguage == null ) {
      queryScriptTextArea.setSyntaxEditingStyle( globalScriptTextArea.getSyntaxEditingStyle() );
    } else {
      queryScriptTextArea.setSyntaxEditingStyle( mapLanguageToSyntaxHighlighting( queryScriptLanguage ) );
    }

    getConfirmAction().setEnabled( hasQueries && isFileSelected );

    queryScriptTextArea.setEnabled( querySelected );
    queryLanguageField.setEnabled( querySelected );
    queryTemplateAction.update();
    if ( querySelected == false ) {
      queryTemplateAction.setEnabled( false );
    }

    globalTemplateAction.update();
  }

  private String mapLanguageToSyntaxHighlighting( final ScriptEngineFactory script ) {
    if ( script == null ) {
      return SyntaxConstants.SYNTAX_STYLE_NONE;
    }

    final String language = script.getLanguageName();
    if ( "ECMAScript".equalsIgnoreCase( language )
       || "js".equalsIgnoreCase( language )
       || "rhino".equalsIgnoreCase( language )
       || "javascript".equalsIgnoreCase( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
    }
    if ( "groovy".equalsIgnoreCase( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_GROOVY;
    }
    return SyntaxConstants.SYNTAX_STYLE_NONE;
  }

  protected void updateQueries() {
    try {
      final DocumentBuilderFactory factory = XMLParserFactoryProducer.createSecureDocBuilderFactory();
      final DocumentBuilder documentBuilder = factory.newDocumentBuilder();

      final DataSetQuery[] objects = queries.values().toArray( new DataSetQuery[ queries.size() ] );
      for ( int i = 0; i < objects.length; i++ ) {
        final DataSetQuery object = objects[ i ];
        final String text = object.getQuery();
        if ( StringUtils.isEmpty( text, true ) ) {
          continue;
        }

        try {
          final Document doc = documentBuilder.parse( new InputSource( new StringReader( text ) ) );
          final NodeList list = doc.getDocumentElement().getElementsByTagName( "domain_id" );
          if ( list.getLength() == 0 ) {
            continue;
          }
          list.item( 0 ).setTextContent( domainIdTextField.getText() );

          final TransformerFactory tfactory = TransformerFactory.newInstance();
          final StringWriter stringWriter = new StringWriter();
          final StreamResult result = new StreamResult();
          result.setWriter( stringWriter );
          tfactory.newTransformer().transform( new DOMSource( doc ), result );
          object.setQuery( stringWriter.getBuffer().toString() );

        } catch ( Exception e ) {
          context.error( e );
        }
      }
    } catch ( Exception e ) {
      context.error( e );
    }

    final Object o = queryNameList.getSelectedValue();
    if ( o != null ) {
      final DataSetQuery q = (DataSetQuery) o;
      queryTextArea.setText( q.getQuery() );
    }
  }
}
