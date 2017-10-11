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

package org.pentaho.reporting.ui.datasources.table;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetQuery;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.NamedQueryModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QueryAddAction;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QueryNameListCellRenderer;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QueryNameTextFieldDocumentListener;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QueryRemoveAction;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.QuerySelectedHandler;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

/**
 * @author Ezequiel Cuellar
 */
public class TableDataSourceEditor extends CommonDialog {
  private class ImportAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ImportAction() {
      //putValue(Action.NAME, "Import");
      setEnabled( false );
      final URL resource = TableDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/table/resources/Spreadsheet.png" ); // NON-NLS
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      }
      putValue( Action.NAME, Messages.getString( "TableDataSourceEditor.ImportSpreadsheet.Name" ) );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "TableDataSourceEditor.ImportSpreadsheet.Description" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final FileFilter[] fileFilters =
        { new FilesystemFilter( new String[] { ".xls", ".xlsx" }, // NON-NLS
          Messages.getString( "TableDataSourceEditor.ExcelFileDescription" ), true ) };
      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( "xls" );

      fileChooser.setFilters( fileFilters );
      if ( fileChooser.showDialog( TableDataSourceEditor.this, JFileChooser.OPEN_DIALOG ) == false ) {
        return;
      }

      final File file = fileChooser.getSelectedFile();
      final ImportFromFileTask importFromFileTask =
        new ImportFromFileTask( file, useFirstRowAsHeader.isSelected(), TableDataSourceEditor.this );
      final Thread workerThread = new Thread( importFromFileTask );
      workerThread.setName( "PRD-import-table-data-task" ); // NON-NLS
      BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( workerThread, importFromFileTask,
        TableDataSourceEditor.this, Messages.getString( "TableDataSourceEditor.ImportSpeadsheet.TaskName" ) );
    }
  }

  private class QueryNameHandler extends QueryNameTextFieldDocumentListener<TableModel> {
    private QueryNameHandler( final NamedQueryModel<TableModel> dialogModel ) {
      super( dialogModel );
    }

    protected void setEditorQuery( final DataSetQuery<TableModel> dataSetQuery ) {
      if ( dataSetQuery == null ) {
        queryNameTextField.setText( null );
        table.setTableEditorModel( null );
        return;
      }

      queryNameTextField.setText( dataSetQuery.getQueryName() );
      table.setTableEditorModel( dataSetQuery.getQuery() );
    }
  }

  private class TableUpdateHandler implements ChangeListener {
    private TableUpdateHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      final DataSetQuery<TableModel> selectedQuery = queries.getQueries().getSelectedQuery();
      if ( selectedQuery != null ) {
        selectedQuery.setQuery( table.getTableEditorModel() );
      }
    }
  }

  private class TableQueryModel extends NamedQueryModel<TableModel> {
    private TableQueryModel() {
    }

    protected TableModel createDefaultObject() {
      final TypedTableModel defaultTableModel = new TypedTableModel();
      defaultTableModel.addColumn( Messages.getString( "TableDataSourceEditor.IDColumn" ), String.class );
      defaultTableModel.addColumn( Messages.getString( "TableDataSourceEditor.ValueColumn" ), String.class );
      defaultTableModel.addRow();
      return defaultTableModel;
    }

    public void setSelectedDataSetQuery( final DataSetQuery<TableModel> tableModelDataSetQuery ) {
      table.stopEditing();
      super.setSelectedDataSetQuery( tableModelDataSetQuery );
    }

    protected void setQuerySelected( final boolean querySelected ) {
      super.setQuerySelected( querySelected );
      updateComponents();
    }
  }

  private JTextField queryNameTextField;
  private TableEditorPanel table;
  private JList queryNameList;

  private TableQueryModel queries;

  private JCheckBox useFirstRowAsHeader;
  private ImportAction importAction;
  private DesignTimeContext designTimeContext;


  public TableDataSourceEditor( final Dialog aOwner ) {
    super( aOwner );
    init();
  }

  public TableDataSourceEditor( final Frame aOwner ) {
    super( aOwner );
    init();
  }

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner.  A shared, hidden
   * frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public TableDataSourceEditor() {
    init();
  }

  protected void init() {
    queries = new TableQueryModel();

    final QueryNameHandler queryNameHandler = new QueryNameHandler( queries );

    queryNameList = new JList( queries.getQueries() );
    queryNameList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    queryNameList.setVisibleRowCount( 5 );
    queryNameList.setCellRenderer( new QueryNameListCellRenderer() );

    queryNameTextField = new JTextField( null, 0 );
    queryNameTextField.setColumns( 35 );
    queryNameTextField.getDocument().addDocumentListener( queryNameHandler );
    queryNameTextField.setEnabled( false );

    QuerySelectedHandler querySelectedHandler = new QuerySelectedHandler( queries, queryNameList );

    table = new TableEditorPanel();
    table.addChangeListener( new TableUpdateHandler() );

    importAction = new ImportAction();

    useFirstRowAsHeader = new JCheckBox( Messages.getString( "TableDataSourceEditor.UseFirstRowAsHeader" ) );
    useFirstRowAsHeader.setEnabled( false );
    useFirstRowAsHeader.setSelected( true );

    setTitle( Messages.getString( "TableDataSourceEditor.Title" ) );

    super.init();
  }

  protected String getDialogId() {
    return "TableDataSourceEditor";
  }

  protected Component createContentPane() {

    final JPanel namePanel = new JPanel( new BorderLayout() );
    namePanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 0, 0 ) );
    namePanel.add( BorderLayout.NORTH, new JLabel( Messages.getString( "TableDataSourceEditor.QueryName" ) ) );
    namePanel.add( BorderLayout.CENTER, queryNameTextField );


    final JPanel leftButtonsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );
    leftButtonsPanel.add( useFirstRowAsHeader );
    leftButtonsPanel.add( new JButton( importAction ) );

    final JPanel buttonsPanel = new JPanel( new BorderLayout() );
    buttonsPanel.setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, Color.LIGHT_GRAY ) );
    buttonsPanel.add( leftButtonsPanel, BorderLayout.WEST );

    final JPanel queryConfigPane = new JPanel( new BorderLayout() );
    queryConfigPane.add( createQuerySelectionPanel(), BorderLayout.NORTH );
    queryConfigPane.add( namePanel, BorderLayout.CENTER );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( queryConfigPane, BorderLayout.NORTH );
    contentPane.add( table, BorderLayout.CENTER );
    contentPane.add( buttonsPanel, BorderLayout.SOUTH );
    return contentPane;
  }

  private JPanel createQuerySelectionPanel() {
    final QueryRemoveAction removeQueryAction = new QueryRemoveAction( queries );

    final JPanel queryListButtonsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    queryListButtonsPanel.add( new BorderlessButton( new QueryAddAction( queries ) ) );
    queryListButtonsPanel.add( new BorderlessButton( removeQueryAction ) );


    final JPanel queryListDetailsPanel = new JPanel( new BorderLayout() );
    queryListDetailsPanel
      .add( new JLabel( Messages.getString( "TableDataSourceEditor.QueryDetailsLabel" ) ), BorderLayout.WEST );
    queryListDetailsPanel.add( queryListButtonsPanel, BorderLayout.EAST );

    // Create the query list panel
    final JPanel queryListPanel = new JPanel( new BorderLayout() );
    queryListPanel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) );
    queryListPanel.add( BorderLayout.NORTH, queryListDetailsPanel );
    queryListPanel.add( BorderLayout.CENTER, new JScrollPane( queryNameList ) );
    return queryListPanel;
  }

  public TableDataFactory performConfiguration( final DesignTimeContext designTimeContext,
                                                final TableDataFactory dataFactory,
                                                final String selectedQuery ) {
    if ( designTimeContext == null ) {
      throw new NullPointerException();
    }

    this.designTimeContext = designTimeContext;
    this.table.applyLocaleSettings( designTimeContext.getLocaleSettings() );

    if ( dataFactory != null ) {
      final String[] queryNames = dataFactory.getQueryNames();
      for ( int i = 0; i < queryNames.length; i++ ) {
        final String queryName = queryNames[ i ];
        final TableModel query = dataFactory.getTable( queryName );
        queries.addQuery( queryName, query );
      }
    }

    queries.setSelectedQuery( selectedQuery );
    if ( performEdit() == false ) {
      return null;
    }

    table.stopEditing();

    final TableDataFactory retval = new TableDataFactory();
    for ( final DataSetQuery<TableModel> query : this.queries.getQueries() ) {
      retval.addTable( query.getQueryName(), query.getQuery() );
    }
    return retval;
  }

  protected void updateComponents() {
    final boolean querySelected = queryNameList.getSelectedIndex() != -1;

    queryNameTextField.setEnabled( querySelected );
    table.setEnabled( querySelected );
    importAction.setEnabled( querySelected );
    useFirstRowAsHeader.setEnabled( querySelected );
  }

  public void importComplete( final TypedTableModel tableModel ) {
    table.setTableEditorModel( tableModel );
  }

  public void importFailed( final Exception e ) {
    designTimeContext.error( e );
  }

  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    TableDataSourceEditor ed = new TableDataSourceEditor();
    ed.performConfiguration( new DefaultDesignTimeContext( new MasterReport() ), null, null );
  }
}
