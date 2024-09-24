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

package org.pentaho.reporting.ui.datasources.xpath;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.extensions.datasources.xpath.XPathDataFactory;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.VerticalLayout;
import org.pentaho.reporting.libraries.designtime.swing.background.DataPreviewDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

/**
 * @author David Kincade
 */
public class XPathDataSourceEditor extends CommonDialog {
  private class BrowseButtonAction extends AbstractAction {
    private BrowseButtonAction() {
      putValue( Action.NAME, Messages.getString( "XPathDataSourceEditor.Browse.Name" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      final File initiallySelectedFile;
      final File reportContextFile = DesignTimeUtil.getContextAsFile( designTimeContext.getReport() );
      final String fileName = filenameField.getText();
      if ( StringUtils.isEmpty( fileName, true ) == false ) {
        if ( reportContextFile == null ) {
          initiallySelectedFile = new File( fileName );
        } else {
          initiallySelectedFile = new File( reportContextFile.getParentFile(), fileName );
        }
      } else {
        initiallySelectedFile = new File( preferences.get( "XQueryFile", "" ) );
      }

      final JFileChooser fileChooser = new JFileChooser();
      fileChooser.setSelectedFile( initiallySelectedFile );
      final int success = fileChooser.showOpenDialog( XPathDataSourceEditor.this );
      if ( success != JFileChooser.APPROVE_OPTION ) {
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
      preferences.put( "XQueryFile", fileChooser.getSelectedFile().toString() );
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
        if ( !"".equals( queryName ) ) {
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
          legacyQuery.setSelected( query.isLegacyQuery() );
          updateComponents();
        } else {
          queryNameTextField.setText( "" );
          queryTextArea.setText( "" );
          legacyQuery.setSelected( false );
          updateComponents();
        }
      }
    }
  }

  private class QueryAddAction extends AbstractAction {
    private QueryAddAction() {
      final URL resource = XPathDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/xpath/resources/Add.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, "XPathDataSourceEditor.AddQuery.Name" );
      }
      putValue( Action.SHORT_DESCRIPTION, "XPathDataSourceEditor.AddQuery.Description" );
    }

    public void actionPerformed( final ActionEvent e ) {
      // Find a unique query name
      String queryName = Messages.getString( "XPathDataSourceEditor.Query" );
      for ( int i = 1; i < 1000; ++i ) {
        final String newQueryName = Messages.getString( "XPathDataSourceEditor.Query" ) + " " + i;
        if ( !queries.containsKey( newQueryName ) ) {
          queryName = newQueryName;
          break;
        }
      }

      final DataSetQuery newQuery = new DataSetQuery( queryName, "", false );
      queries.put( newQuery.getQueryName(), newQuery );

      inModifyingQueryNameList = true;
      updateQueryList();
      queryNameList.setSelectedValue( newQuery, true );
      inModifyingQueryNameList = false;
      updateComponents();
    }
  }

  private class QueryRemoveActionListener extends AbstractAction implements ListSelectionListener {
    private QueryRemoveActionListener() {
      final URL resource = XPathDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/xpath/resources/Remove.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, "XPathDataSourceEditor.RemoveQuery.Name" );
      }
      putValue( Action.SHORT_DESCRIPTION, "XPathDataSourceEditor.RemoveQuery.Description" );
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
      final DataSetQuery query = (DataSetQuery) queryNameList.getSelectedValue();
      if ( query != null ) {
        query.setQuery( queryTextArea.getText() );
      }
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

  private class PreviewAction extends AbstractAction {

    private PreviewAction() {
      putValue( Action.NAME, Messages.getString( "XPathDataSourceEditor.Preview.Name" ) );
    }

    public void actionPerformed( final ActionEvent aEvt ) {
      try {
        final String query = queryNameTextField.getText();
        final DataPreviewDialog previewDialog = new DataPreviewDialog( XPathDataSourceEditor.this );
        final XPathDataFactory dataFactory = produceFactory();
        DataFactoryEditorSupport.configureDataFactoryForPreview( dataFactory, designTimeContext );

        final XPathPreviewWorker worker = new XPathPreviewWorker( dataFactory, query );
        previewDialog.showData( worker );

        final ReportDataFactoryException factoryException = worker.getException();
        if ( factoryException != null ) {
          ExceptionDialog.showExceptionDialog( XPathDataSourceEditor.this,
            Messages.getString( "XPathDataSourceEditor.PreviewError.Title" ),
            Messages.getString( "XPathDataSourceEditor.PreviewError.Message" ), factoryException );
        }

      } catch ( Exception e ) {
        ExceptionDialog.showExceptionDialog( XPathDataSourceEditor.this,
          Messages.getString( "XPathDataSourceEditor.PreviewError.Title" ),
          Messages.getString( "XPathDataSourceEditor.PreviewError.Message" ), e );
      }
    }
  }

  private class LegacyCheckboxUpdateHandler implements ChangeListener {
    private LegacyCheckboxUpdateHandler() {
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged( final ChangeEvent e ) {
      final DataSetQuery currentQuery = (DataSetQuery) queryNameList.getSelectedValue();
      if ( currentQuery == null ) {
        return;
      }

      currentQuery.setLegacyQuery( legacyQuery.isSelected() );
    }
  }

  private JList queryNameList;
  private JTextField queryNameTextField;
  private JTextField filenameField;
  private JTextArea queryTextArea;
  private Map<String, DataSetQuery> queries;
  private boolean inQueryNameUpdate;
  private boolean inModifyingQueryNameList;
  private Preferences preferences;
  private PreviewAction previewAction;
  private DesignTimeContext designTimeContext;
  private JCheckBox legacyQuery;
  private QueryRemoveActionListener removeQueryAction;

  public XPathDataSourceEditor( final DesignTimeContext designTimeContext ) {
    init( designTimeContext );
  }

  public XPathDataSourceEditor( final DesignTimeContext designTimeContext, final Dialog owner ) {
    super( owner );
    init( designTimeContext );
  }

  public XPathDataSourceEditor( final DesignTimeContext designTimeContext, final Frame owner ) {
    super( owner );
    init( designTimeContext );
  }

  private void init( final DesignTimeContext designTimeContext ) {
    if ( designTimeContext == null ) {
      throw new NullPointerException();
    }
    this.designTimeContext = designTimeContext;
    preferences = Preferences.userNodeForPackage( XPathDataSourceEditor.class );

    previewAction = new PreviewAction();

    setTitle( Messages.getString( "XPathDataSourceEditor.Title" ) );
    setModal( true );
    setLayout( new BorderLayout() );


    filenameField = new JTextField( null, 0 );
    filenameField.setColumns( 30 );
    filenameField.getDocument().addDocumentListener( new FilenameDocumentListener() );

    removeQueryAction = new QueryRemoveActionListener();

    queryNameList = new JList();
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.addListSelectionListener( new QueryNameListSelectionListener() );
    queryNameList.setCellRenderer( new QueryNameListCellRenderer() );
    queryNameList.addListSelectionListener( removeQueryAction );

    queryNameTextField = new JTextField( null, 0 );
    queryNameTextField.setColumns( 35 );
    queryNameTextField.getDocument().addDocumentListener( new QueryNameTextFieldDocumentListener() );

    legacyQuery = new JCheckBox( Messages.getString( "XPathDataSourceEditor.LegacyQuery" ) );
    legacyQuery.addChangeListener( new LegacyCheckboxUpdateHandler() );

    queryTextArea = new JTextArea( (String) null );
    queryTextArea.setWrapStyleWord( true );
    queryTextArea.setLineWrap( true );
    queryTextArea.setRows( 5 );
    queryTextArea.getDocument().addDocumentListener( new QueryDocumentListener() );

    super.init();
  }

  protected String getDialogId() {
    return "XPathDataSourceEditor";
  }

  private JPanel createQueryNameListPanel() {
    final JPanel queryListButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    queryListButtonsPanel.add( new BorderlessButton( new QueryAddAction() ) );
    queryListButtonsPanel.add( new BorderlessButton( removeQueryAction ) );

    final JPanel queryListDetailsPanel = new JPanel( new BorderLayout() );
    queryListDetailsPanel
      .add( new JLabel( Messages.getString( "XPathDataSourceEditor.QueryDetailsLabel" ) ), BorderLayout.WEST );
    queryListDetailsPanel.add( queryListButtonsPanel, BorderLayout.EAST );

    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.add( BorderLayout.NORTH, queryListDetailsPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }

  protected Component createContentPane() {
    final JLabel fileLabel = new JLabel( Messages.getString( "XPathDataSourceEditor.File" ) );

    final JPanel filePanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
    filePanel.add( filenameField );
    filePanel.add( new JButton( new BrowseButtonAction() ) );

    final JLabel queryNameStringLabel =
      new JLabel( Messages.getString( "XPathDataSourceEditor.QueryNameStringLabel" ) );
    queryNameStringLabel.setBorder( BorderFactory.createEmptyBorder( 4, 0, 8, 0 ) );

    final JLabel queryStringLabel = new JLabel( Messages.getString( "XPathDataSourceEditor.QueryStringLabel" ) );
    queryStringLabel.setBorder( BorderFactory.createEmptyBorder( 4, 0, 8, 0 ) );

    final JPanel previewPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    previewPanel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 4 ) );
    previewPanel.add( new JButton( previewAction ) );

    final JPanel contentPane = new JPanel();
    contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    contentPane.setLayout( new VerticalLayout( 5, VerticalLayout.BOTH ) );
    contentPane.add( fileLabel );
    contentPane.add( filePanel );
    contentPane.add( createQueryNameListPanel() );
    contentPane.add( queryNameStringLabel );
    contentPane.add( queryNameTextField );
    contentPane.add( legacyQuery );
    contentPane.add( queryStringLabel );
    contentPane.add( new JScrollPane( queryTextArea ), BorderLayout.CENTER );
    contentPane.add( previewPanel );
    return contentPane;
  }

  public XPathDataFactory performConfiguration( final XPathDataFactory dataFactory,
                                                final String selectedQuery ) {
    // Reset the ok / cancel flag

    // Initialize the internal storage
    queries = new TreeMap<String, DataSetQuery>();

    // Load the current configuration
    if ( dataFactory != null ) {
      filenameField.setText( dataFactory.getXqueryDataFile() );

      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[ i ];
        final XPathDataFactory.QueryDefinition query = dataFactory.getQuery( queryName );
        queries.put( queryName, new DataSetQuery( queryName, query.getXpathExpression(), query.isLegacyQuery() ) );
      }
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

  private XPathDataFactory produceFactory() {
    final XPathDataFactory returnDataFactory = new XPathDataFactory();
    returnDataFactory.setXqueryDataFile( filenameField.getText() );

    final DataSetQuery[] queries = this.queries.values().toArray( new DataSetQuery[ this.queries.size() ] );
    for ( int i = 0; i < queries.length; i++ ) {
      final DataSetQuery query = queries[ i ];
      returnDataFactory.setQuery( query.getQueryName(), query.getQuery(), query.isLegacyQuery() );
    }
    return returnDataFactory;
  }

  protected void setSelectedQuery( final String aQuery ) {
    final ListModel theModel = queryNameList.getModel();
    for ( int i = 0; i < theModel.getSize(); i++ ) {
      final DataSetQuery theDataSet = (DataSetQuery) theModel.getElementAt( i );
      if ( theDataSet.getQueryName().equals( aQuery ) ) {
        queryNameList.setSelectedValue( theDataSet, true );
        break;
      }
    }
  }

  protected void updateQueryList() {
    queryNameList.removeAll();
    queryNameList.setListData( queries.values().toArray( new DataSetQuery[ queries.size() ] ) );
  }

  protected void updateComponents() {
    final boolean querySelected = queryNameList.getSelectedIndex() != -1;
    final boolean hasQueries = queryNameList.getModel().getSize() > 0;

    queryNameTextField.setEnabled( querySelected );
    queryTextArea.setEnabled( querySelected );
    legacyQuery.setEnabled( querySelected );

    getConfirmAction().setEnabled( hasQueries && !StringUtils.isEmpty( filenameField.getText(), true ) );
    previewAction.setEnabled( querySelected );
  }
}
