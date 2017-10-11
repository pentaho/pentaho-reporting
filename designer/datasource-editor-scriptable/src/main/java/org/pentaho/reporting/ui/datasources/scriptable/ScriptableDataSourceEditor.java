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

package org.pentaho.reporting.ui.datasources.scriptable;

import org.apache.bsf.BSFManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author David Kincade
 */
public class ScriptableDataSourceEditor extends CommonDialog {
  private class UpdateLanguageHandler implements ActionListener, ListSelectionListener {
    private UpdateLanguageHandler() {
    }

    public void actionPerformed( final ActionEvent e ) {
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

  private static class InternalBSFManager extends BSFManager {
    private InternalBSFManager() {
    }

    public static String[] getRegisteredLanguages() {
      final ArrayList<String> list = new ArrayList<String>();
      final Iterator iterator = registeredEngines.entrySet().iterator();
      while ( iterator.hasNext() ) {
        final Map.Entry entry = (Map.Entry) iterator.next();
        final String lang = (String) entry.getKey();
        final String className = (String) entry.getValue();

        try {
          // this is how BSH will load the class
          Class.forName( className, false, Thread.currentThread().getContextClassLoader() );
          list.add( lang );
        } catch ( Throwable t ) {
          // ignored.
        }

      }
      return list.toArray( new String[ list.size() ] );
    }
  }


  private class QueryRemoveAction extends AbstractAction implements ListSelectionListener {
    private QueryRemoveAction() {
      final URL resource = ScriptableDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/scriptable/resources/Remove.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "ScriptableDataSourceEditor.RemoveQuery.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "ScriptableDataSourceEditor.RemoveQuery.Description" ) );
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

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( queryNameList.isSelectionEmpty() == false );
    }
  }

  private class QueryNameTextFieldDocumentListener implements DocumentListener {
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

  private static class QueryNameListCellRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   final boolean cellHasFocus ) {
      final JLabel listCellRendererComponent =
        (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
      if ( value != null ) {
        final String queryName = ( (DataSetQuery) value ).getQueryName();
        if ( StringUtils.isEmpty( queryName ) == false ) {
          listCellRendererComponent.setText( queryName );
        } else {
          listCellRendererComponent.setText( " " );
        }
      }
      return listCellRendererComponent;
    }
  }

  private class QueryNameListSelectionListener implements ListSelectionListener {
    public void valueChanged( final ListSelectionEvent e ) {
      if ( !inQueryNameUpdate ) {
        final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
        if ( query != null ) {
          queryNameTextField.setText( query.getQueryName() );
          queryTextArea.setText( query.getQuery() );
          updateComponents();
        } else {
          queryNameTextField.setText( "" );
          queryTextArea.setText( "" );
          updateComponents();
        }
      }
    }
  }

  private class QueryAddAction extends AbstractAction {
    private QueryAddAction() {
      final URL resource = ScriptableDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/scriptable/resources/Add.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "ScriptableDataSourceEditor.AddQuery.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "ScriptableDataSourceEditor.AddQuery.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      // Find a unique query name
      String queryName = Messages.getString( "ScriptableDataSourceEditor.Query" );
      for ( int i = 1; i < 1000; ++i ) {
        final String newQueryName = Messages.getString( "ScriptableDataSourceEditor.Query" ) + ' ' + i;
        if ( !queries.containsKey( newQueryName ) ) {
          queryName = newQueryName;
          break;
        }
      }

      final DataSetQuery newQuery = new DataSetQuery( queryName, "" );
      queries.put( newQuery.getQueryName(), newQuery );

      inModifyingQueryNameList = true;
      updateQueryList();
      queryNameList.setSelectedValue( newQuery, true );
      inModifyingQueryNameList = false;
      updateComponents();
    }
  }

  private class QueryDocumentListener implements DocumentListener {
    private QueryDocumentListener() {
    }

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
      final DataSetQuery currentQuery = (DataSetQuery) queryNameList.getSelectedValue();
      if ( currentQuery == null ) {
        return;
      }

      currentQuery.setQuery( queryTextArea.getText() );
    }
  }

  private class PreviewAction extends AbstractAction {
    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "ScriptableDataSourceEditor.Preview.Name" ) );
    }

    public void actionPerformed( final ActionEvent aEvt ) {
      try {
        final ScriptableDataFactory dataFactory = produceFactory();
        DataFactoryEditorSupport.configureDataFactoryForPreview( dataFactory, designTimeContext );
        final DataPreviewDialog previewDialog = new DataPreviewDialog( ScriptableDataSourceEditor.this );

        final ScriptablePreviewWorker worker = new ScriptablePreviewWorker( dataFactory, queryNameTextField.getText() );
        previewDialog.showData( worker );

        final ReportDataFactoryException factoryException = worker.getException();
        if ( factoryException != null ) {
          ExceptionDialog.showExceptionDialog( ScriptableDataSourceEditor.this,
            Messages.getString( "ScriptableDataSourceEditor.PreviewError.Title" ),
            Messages.getString( "ScriptableDataSourceEditor.PreviewError.Message" ), factoryException );
        }
      } catch ( Exception e ) {
        ExceptionDialog.showExceptionDialog( ScriptableDataSourceEditor.this,
          Messages.getString( "ScriptableDataSourceEditor.PreviewError.Title" ),
          Messages.getString( "ScriptableDataSourceEditor.PreviewError.Message" ), e );
      }
    }
  }

  private static class ScriptablePreviewWorker implements PreviewWorker {
    private ScriptableDataFactory dataFactory;
    private TableModel resultTableModel;
    private ReportDataFactoryException exception;
    private String query;

    private ScriptablePreviewWorker( final ScriptableDataFactory dataFactory,
                                     final String query ) {
      if ( dataFactory == null ) {
        throw new NullPointerException();
      }
      this.query = query;
      this.dataFactory = dataFactory;
    }

    public ReportDataFactoryException getException() {
      return exception;
    }

    public TableModel getResultTableModel() {
      return resultTableModel;
    }

    public void close() {
    }

    /**
     * Requests that the thread stop processing as soon as possible.
     */
    public void cancelProcessing( final CancelEvent event ) {
      dataFactory.cancelRunningQuery();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      try {
        resultTableModel = dataFactory.queryData( query, new ReportParameterValues() );
      } catch ( ReportDataFactoryException e ) {
        exception = e;
      } finally {
        dataFactory.close();
      }
    }
  }

  private JList queryNameList;
  private JTextField queryNameTextField;
  private JList languageField;
  private RSyntaxTextArea queryTextArea;
  private RSyntaxTextArea initScriptTextArea;
  private RSyntaxTextArea shutdownScriptTextArea;
  private Map<String, DataSetQuery> queries;
  private boolean inQueryNameUpdate;
  private boolean inModifyingQueryNameList;
  private PreviewAction previewAction;
  private DesignTimeContext designTimeContext;

  public ScriptableDataSourceEditor( final DesignTimeContext designTimeContext ) {
    init( designTimeContext );
  }

  public ScriptableDataSourceEditor( final DesignTimeContext designTimeContext, final Dialog owner ) {
    super( owner );
    init( designTimeContext );
  }

  public ScriptableDataSourceEditor( final DesignTimeContext designTimeContext, final Frame owner ) {
    super( owner );
    init( designTimeContext );
  }

  private void init( final DesignTimeContext designTimeContext ) {
    if ( designTimeContext == null ) {
      throw new NullPointerException();
    }
    this.designTimeContext = designTimeContext;

    setTitle( Messages.getString( "ScriptableDataSourceEditor.Title" ) );
    setModal( true );

    previewAction = new PreviewAction();

    queryNameTextField = new JTextField( null, 0 );
    queryNameTextField.setColumns( 35 );
    queryNameTextField.getDocument().addDocumentListener( new QueryNameTextFieldDocumentListener() );

    queryTextArea = new RSyntaxTextArea();
    queryTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );
    queryTextArea.getDocument().addDocumentListener( new QueryDocumentListener() );

    initScriptTextArea = new RSyntaxTextArea();
    initScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );

    shutdownScriptTextArea = new RSyntaxTextArea();
    shutdownScriptTextArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_NONE );

    languageField = new JList( new DefaultComboBoxModel( InternalBSFManager.getRegisteredLanguages() ) );
    languageField.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    languageField.getSelectionModel().addListSelectionListener( new UpdateLanguageHandler() );

    queryNameList = new JList();
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.addListSelectionListener( new QueryNameListSelectionListener() );
    queryNameList.setCellRenderer( new QueryNameListCellRenderer() );

    final QueryRemoveAction removeQueryAction = new QueryRemoveAction();
    queryNameList.addListSelectionListener( removeQueryAction );


    super.init();
  }

  protected String getDialogId() {
    return "ScriptableDataSourceEditor";
  }

  protected Component createContentPane() {
    final JPanel initScriptContentHolder = new JPanel( new BorderLayout() );
    initScriptContentHolder
      .add( BorderLayout.NORTH, new JLabel( Messages.getString( "ScriptableDataSourceEditor.InitScript" ) ) );
    initScriptContentHolder.add( BorderLayout.CENTER, new RTextScrollPane( 500, 600, initScriptTextArea, true ) );

    final JPanel shutdownScriptContentHolder = new JPanel( new BorderLayout() );
    shutdownScriptContentHolder
      .add( BorderLayout.NORTH, new JLabel( Messages.getString( "ScriptableDataSourceEditor.ShutdownScript" ) ) );
    shutdownScriptContentHolder
      .add( BorderLayout.CENTER, new RTextScrollPane( 500, 600, shutdownScriptTextArea, true ) );

    final JPanel queryDetailsNamePanel = new JPanel( new BorderLayout() );
    queryDetailsNamePanel
      .add( new JLabel( Messages.getString( "ScriptableDataSourceEditor.QueryName" ) ), BorderLayout.NORTH );
    queryDetailsNamePanel.add( queryNameTextField, BorderLayout.CENTER );

    final JPanel queryContentHolder = new JPanel( new BorderLayout() );
    queryContentHolder
      .add( BorderLayout.NORTH, new JLabel( Messages.getString( "ScriptableDataSourceEditor.QueryLabel" ) ) );
    queryContentHolder.add( BorderLayout.CENTER, new RTextScrollPane( 500, 300, queryTextArea, true ) );

    // Create the query details panel
    final JPanel queryDetailsPanel = new JPanel( new BorderLayout() );
    queryDetailsPanel.setBorder( new EmptyBorder( 0, 8, 8, 8 ) );
    queryDetailsPanel.add( BorderLayout.NORTH, queryDetailsNamePanel );
    queryDetailsPanel.add( BorderLayout.CENTER, queryContentHolder );

    final JPanel previewButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    previewButtonPanel.add( new JButton( previewAction ) );

    final JPanel queryContentPanel = new JPanel( new BorderLayout() );
    queryContentPanel.add( BorderLayout.NORTH, createQueryListPanel() );
    queryContentPanel.add( BorderLayout.CENTER, queryDetailsPanel );

    final JTabbedPane scriptsTabPane = new JTabbedPane();
    scriptsTabPane.addTab( Messages.getString( "ScriptableDataSourceEditor.QueryTab" ), queryContentPanel );
    scriptsTabPane.addTab( Messages.getString( "ScriptableDataSourceEditor.InitScriptTab" ), initScriptContentHolder );
    scriptsTabPane
      .addTab( Messages.getString( "ScriptableDataSourceEditor.ShutdownScriptTab" ), shutdownScriptContentHolder );

    final JLabel languageLabel = new JLabel( Messages.getString( "ScriptableDataSourceEditor.Language" ) );
    languageLabel.setBorder( new EmptyBorder( 0, 0, 3, 0 ) );

    final JPanel languagesPanel = new JPanel( new BorderLayout() );
    languagesPanel.setBorder( new EmptyBorder( 8, 8, 8, 0 ) );
    languagesPanel.add( BorderLayout.NORTH, languageLabel );
    languagesPanel.add( BorderLayout.CENTER, new JScrollPane( languageField ) );

    final JPanel contentPanel = new JPanel( new BorderLayout() );
    contentPanel.add( BorderLayout.WEST, languagesPanel );
    contentPanel.add( BorderLayout.CENTER, scriptsTabPane );
    contentPanel.add( BorderLayout.SOUTH, previewButtonPanel );
    return contentPanel;
  }

  private JPanel createQueryListPanel() {
    final QueryRemoveAction queryRemoveAction = new QueryRemoveAction();
    queryNameList.addListSelectionListener( queryRemoveAction );

    final JPanel theQueryButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    theQueryButtonsPanel.add( new BorderlessButton( new QueryAddAction() ) );
    theQueryButtonsPanel.add( new BorderlessButton( queryRemoveAction ) );

    final JPanel theQueryControlsPanel = new JPanel( new BorderLayout() );
    theQueryControlsPanel
      .add( BorderLayout.WEST, new JLabel( Messages.getString( "ScriptableDataSourceEditor.AvailableQueries" ) ) );
    theQueryControlsPanel.add( BorderLayout.EAST, theQueryButtonsPanel );

    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 8, 0, 8 ) );
    queryListPanel.add( BorderLayout.NORTH, theQueryControlsPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }

  public ScriptableDataFactory performConfiguration( final ScriptableDataFactory dataFactory,
                                                     final String selectedQuery ) {
    // Reset the confirmed / cancel flag

    // Initialize the internal storage
    queries = new TreeMap<String, DataSetQuery>();

    // Load the current configuration
    if ( dataFactory != null ) {
      languageField.setSelectedValue( dataFactory.getLanguage(), true );

      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[ i ];
        final String query = dataFactory.getQuery( queryName );
        queries.put( queryName, new DataSetQuery( queryName, query ) );
      }

      initScriptTextArea.setText( dataFactory.getScript() );
      shutdownScriptTextArea.setText( dataFactory.getShutdownScript() );
    }

    // Prepare the data and the enable the proper buttons
    updateComponents();
    updateQueryList();
    setSelectedQuery( selectedQuery );

    // Enable the dialog

    if ( !performEdit() ) {
      return null;
    }

    return produceFactory();
  }

  private ScriptableDataFactory produceFactory() {
    final ScriptableDataFactory returnDataFactory = new ScriptableDataFactory();
    returnDataFactory.setLanguage( (String) languageField.getSelectedValue() );
    if ( StringUtils.isEmpty( initScriptTextArea.getText() ) ) {
      returnDataFactory.setScript( null );
    } else {
      returnDataFactory.setScript( initScriptTextArea.getText() );
    }

    if ( StringUtils.isEmpty( shutdownScriptTextArea.getText() ) ) {
      returnDataFactory.setShutdownScript( null );
    } else {
      returnDataFactory.setShutdownScript( shutdownScriptTextArea.getText() );
    }

    final DataSetQuery[] queries = this.queries.values().toArray( new DataSetQuery[ this.queries.size() ] );
    for ( int i = 0; i < queries.length; i++ ) {
      final DataSetQuery query = queries[ i ];
      returnDataFactory.setQuery( query.getQueryName(), query.getQuery() );
    }
    return returnDataFactory;
  }

  protected void updateQueryList() {
    queryNameList.removeAll();
    queryNameList.setListData( queries.values().toArray( new DataSetQuery[ queries.size() ] ) );
  }

  private void setSelectedQuery( final String aQuery ) {
    final ListModel theModel = queryNameList.getModel();
    for ( int i = 0; i < theModel.getSize(); i++ ) {
      final DataSetQuery theDataSet = (DataSetQuery) theModel.getElementAt( i );
      if ( theDataSet.getQueryName().equals( aQuery ) ) {
        queryNameList.setSelectedValue( theDataSet, true );
        break;
      }
    }
  }

  protected void updateComponents() {
    final boolean querySelected = queryNameList.getSelectedIndex() != -1;
    final boolean hasQueries = queryNameList.getModel().getSize() > 0;

    queryNameTextField.setEnabled( querySelected );
    queryTextArea.setEnabled( querySelected );

    getConfirmAction().setEnabled( hasQueries && languageField.getSelectedIndex() != -1 );
    queryTextArea.setSyntaxEditingStyle( mapLanguageToSyntaxHighlighting( (String) languageField.getSelectedValue() ) );
    initScriptTextArea
      .setSyntaxEditingStyle( mapLanguageToSyntaxHighlighting( (String) languageField.getSelectedValue() ) );
    shutdownScriptTextArea
      .setSyntaxEditingStyle( mapLanguageToSyntaxHighlighting( (String) languageField.getSelectedValue() ) );
    previewAction.setEnabled( querySelected );
  }

  private String mapLanguageToSyntaxHighlighting( final String language ) {
    if ( "beanshell".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_JAVA;
    }
    if ( "groovy".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_GROOVY;
    }
    if ( "javascript".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
    }
    if ( "jython".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_PYTHON;
    }
    if ( "xslt".equals( language ) ) {
      return SyntaxConstants.SYNTAX_STYLE_XML;
    }

    return SyntaxConstants.SYNTAX_STYLE_NONE;
  }
}
