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

package org.pentaho.reporting.designer.core.editor.crosstab;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDataChangeListener;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabBuilder;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDetail;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDimension;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.bulk.BulkDataProvider;
import org.pentaho.reporting.libraries.designtime.swing.bulk.DefaultBulkListModel;
import org.pentaho.reporting.libraries.designtime.swing.bulk.RemoveBulkAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkDownAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkUpAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class CreateCrosstabDialog extends CommonDialog implements ReportDataChangeListener {
  private class AddListSelectionAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;
    private DefaultListModel data;
    private ListModel fields;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddListSelectionAction( final JList availableFields,
                                    final DefaultBulkListModel data ) {


      this.selectionModel = availableFields.getSelectionModel();
      this.fields = availableFields.getModel();
      this.data = data;

      putValue( Action.SMALL_ICON, IconLoader.getInstance().getFowardArrowIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "CreateCrosstabDialog.AddColumn" ) );
      selectionModel.addListSelectionListener( this );
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      for ( int i = 0; i < fields.getSize(); i++ ) {
        if ( selectionModel.isSelectedIndex( i ) ) {
          data.addElement( fields.getElementAt( i ) );
        }
      }
    }
  }

  private class AddDimensionAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;
    private CrosstabDimensionTableModel data;
    private ListModel fields;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddDimensionAction( final JList availableFields,
                                final CrosstabDimensionTableModel data ) {
      this.selectionModel = availableFields.getSelectionModel();
      this.fields = availableFields.getModel();
      this.data = data;

      putValue( Action.SMALL_ICON, IconLoader.getInstance().getFowardArrowIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "CreateCrosstabDialog.AddColumn" ) );
      selectionModel.addListSelectionListener( this );
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      for ( int i = 0; i < fields.getSize(); i++ ) {
        if ( selectionModel.isSelectedIndex( i ) ) {
          final String item = (String) fields.getElementAt( i );
          data.add( new CrosstabDimension( item ) );
        }
      }
    }
  }

  private class AddDetailsAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;
    private CrosstabDetailTableModel data;
    private ListModel fields;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddDetailsAction( final JList availableFields,
                              final CrosstabDetailTableModel data ) {
      this.selectionModel = availableFields.getSelectionModel();
      this.fields = availableFields.getModel();
      this.data = data;

      putValue( Action.SMALL_ICON, IconLoader.getInstance().getFowardArrowIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "CreateCrosstabDialog.AddColumn" ) );
      selectionModel.addListSelectionListener( this );
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      for ( int i = 0; i < fields.getSize(); i++ ) {
        if ( selectionModel.isSelectedIndex( i ) ) {
          final String item = (String) fields.getElementAt( i );
          data.add( new CrosstabDetail( item ) );
        }
      }
    }
  }

  private class MonitorMandatoryDimensionsHandler implements TableModelListener {
    public void tableChanged( final TableModelEvent e ) {
      validateInputs( false );
    }
  }

  private DraggableJList otherFields;
  private DraggableCrosstabDimensionTable rowFields;
  private DraggableCrosstabDimensionTable columnFields;
  private JTable detailFields;
  private FieldListCellRenderer fieldListCellRenderer;
  private DraggableJList availableFields;
  private DefaultBulkListModel availableFieldsModel;
  private DefaultBulkListModel otherFieldsModel;
  private CrosstabDimensionTableModel rowsFieldsModel;
  private CrosstabDimensionTableModel columnsFieldsModel;
  private CrosstabDetailTableModel detailFieldsModel;
  private CrosstabOptionsPane optionsPane;

  public CreateCrosstabDialog() {
    init();
  }

  public CreateCrosstabDialog( final Frame owner ) throws HeadlessException {
    super( owner );
    init();
  }

  public CreateCrosstabDialog( final Dialog owner ) throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "CreateCrosstabDialog.Title" ) );
    setModal( true );

    optionsPane = new CrosstabOptionsPane();

    fieldListCellRenderer = new FieldListCellRenderer();
    availableFieldsModel = new DefaultBulkListModel();

    availableFields = new DraggableJList( availableFieldsModel );
    availableFields.setTransferHandler( new CrosstabDialogTransferHandler( availableFields, true ) );
    availableFields.setCellRenderer( fieldListCellRenderer );

    otherFieldsModel = new DefaultBulkListModel();
    otherFields = new DraggableJList( otherFieldsModel );
    otherFields.setVisibleRowCount( 3 );
    otherFields.setCellRenderer( fieldListCellRenderer );

    rowsFieldsModel = new CrosstabDimensionTableModel();
    rowsFieldsModel.addTableModelListener( new MonitorMandatoryDimensionsHandler() );
    rowFields = new DraggableCrosstabDimensionTable( rowsFieldsModel );

    columnsFieldsModel = new CrosstabDimensionTableModel();
    columnsFieldsModel.addTableModelListener( new MonitorMandatoryDimensionsHandler() );
    columnFields = new DraggableCrosstabDimensionTable( columnsFieldsModel );

    detailFieldsModel = new CrosstabDetailTableModel();
    detailFields = new DraggableCrosstabDetailTable( detailFieldsModel );

    super.init();
  }

  protected void performInitialResize() {
    super.performInitialResize();
    if ( getHeight() > 800 ) {
      setBounds( getX(), getY(), getWidth(), 800 );
    }
    LibSwingUtil.centerDialogInParent( this );
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.CreateCrosstab"; // NON-NLS
  }

  protected Component createContentPane() {
    final JTabbedPane pane = new JTabbedPane();
    pane.addTab( Messages.getString( "CreateCrosstabDialog.Fields" ), createSelectionPane() );
    pane.addTab( Messages.getString( "CreateCrosstabDialog.Options" ), optionsPane );

    final JPanel contentPane = new JPanel();
    contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( createTitlePanel(), BorderLayout.NORTH );
    contentPane.add( pane, BorderLayout.CENTER );
    return contentPane;
  }

  private JPanel createTitlePanel() {
    final JPanel titlePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );
    titlePanel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 5, 0 ) );
    titlePanel.add( new JLabel( Messages.getString( "CreateCrosstabDialog.TitleLabel" ) ) );
    return titlePanel;
  }

  private JComponent createSelectionPane() {
    final JPanel sidePane = new JPanel();
    sidePane.setLayout( new BorderLayout() );

    final JLabel tablesColumnsLabel = new JLabel( Messages.getString( "CreateCrosstabDialog.AvailableFields" ) );
    sidePane.add( tablesColumnsLabel, BorderLayout.NORTH );
    final JScrollPane comp = new JScrollPane
      ( availableFields, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    sidePane.add( comp, BorderLayout.CENTER );

    final JPanel tablesPane = new JPanel();
    tablesPane.setLayout( new GridBagLayout() );
    addList( tablesPane, 0, otherFields, "CreateCrosstabDialog.OtherFields" );
    addTable( tablesPane, 1, rowFields, "CreateCrosstabDialog.RowFields",
      new AddDimensionAction( availableFields, rowsFieldsModel ) );
    addTable( tablesPane, 2, columnFields, "CreateCrosstabDialog.ColumnsFields",
      new AddDimensionAction( availableFields, columnsFieldsModel ) );
    addTable( tablesPane, 3, detailFields, "CreateCrosstabDialog.Details",
      new AddDetailsAction( availableFields, detailFieldsModel ) );

    final JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, sidePane, tablesPane );
    splitPane.setBorder( null );
    return splitPane;
  }

  private void addTable( final JComponent tablesPane, final int index,
                         final JTable list, final String labelText, final Action addAction ) {
    final BulkDataProvider bulkListModel = (BulkDataProvider) list.getModel();
    final JButton otherAdd = new BorderlessButton( addAction );
    final JLabel otherLabel = new JLabel( Messages.getString( labelText ) );

    final ListSelectionModel otherSelectionModel = list.getSelectionModel();
    final JButton otherSortUp = new BorderlessButton( new SortBulkUpAction( bulkListModel, otherSelectionModel ) );
    final JButton otherSortDown = new BorderlessButton( new SortBulkDownAction( bulkListModel, otherSelectionModel ) );
    final JButton otherRemove = new BorderlessButton( new RemoveBulkAction( bulkListModel, otherSelectionModel ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = index * 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( otherLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = index * 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( otherSortUp, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = index * 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( otherSortDown, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = index * 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( otherRemove, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1 + index * 2;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = 4;
    gbc.insets = new Insets( 0, 5, 5, 0 );
    tablesPane.add( new JScrollPane
      ( list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1 + index * 2;
    gbc.insets = new Insets( 5, 5, 5, 0 );
    tablesPane.add( otherAdd, gbc );
  }

  private void addList( final JComponent tablesPane, final int index, final JList list, final String labelText ) {
    final DefaultBulkListModel bulkListModel = (DefaultBulkListModel) list.getModel();
    final JButton otherAdd = new BorderlessButton( new AddListSelectionAction( availableFields, bulkListModel ) );
    final JLabel otherLabel = new JLabel( Messages.getString( labelText ) );

    final ListSelectionModel otherSelectionModel = list.getSelectionModel();
    final JButton otherSortUp = new BorderlessButton( new SortBulkUpAction( bulkListModel, otherSelectionModel ) );
    final JButton otherSortDown = new BorderlessButton( new SortBulkDownAction( bulkListModel, otherSelectionModel ) );
    final JButton otherRemove = new BorderlessButton( new RemoveBulkAction( bulkListModel, otherSelectionModel ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = index * 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( otherLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = index * 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( otherSortUp, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = index * 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( otherSortDown, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = index * 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( otherRemove, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1 + index * 2;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = 4;
    gbc.insets = new Insets( 0, 5, 5, 0 );
    tablesPane.add( new JScrollPane
      ( list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1 + index * 2;
    gbc.insets = new Insets( 5, 5, 5, 0 );
    tablesPane.add( otherAdd, gbc );
  }

  protected DefaultBulkListModel getAvailableFieldsModel() {
    return availableFieldsModel;
  }

  public void dataModelChanged( final ReportDocumentContext context ) {
    final ContextAwareDataSchemaModel dataSchemaModel = context.getReportDataSchemaModel();
    final String[] columnNames = filterDatabaseColumn( dataSchemaModel );

    final DefaultBulkListModel availableFieldsModel = getAvailableFieldsModel();
    availableFieldsModel.setBulkData( columnNames );

    this.fieldListCellRenderer.setModel( context.getReportDataSchemaModel() );
  }

  private String[] filterDatabaseColumn( final ContextAwareDataSchemaModel dataSchemaModel ) {
    final ArrayList<String> fields = new ArrayList<String>();
    final String[] columnNames = dataSchemaModel.getColumnNames();
    final DataAttributeContext dac = dataSchemaModel.getDataAttributeContext();
    for ( final String columnName : columnNames ) {
      final DataAttributes attributes = dataSchemaModel.getDataSchema().getAttributes( columnName );
      final Object sourceAttribute = attributes.getMetaAttribute
        ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE, String.class, dac );
      if ( MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT.equals( sourceAttribute ) ||
        MetaAttributeNames.Core.SOURCE_VALUE_PARAMETER.equals( sourceAttribute ) ) {
        continue;
      }

      fields.add( columnName );

    }
    return fields.toArray( new String[ fields.size() ] );
  }

  public CrosstabGroup createCrosstab( final ReportDesignerContext designerContext,
                                       final CrosstabGroup editedGroup ) {
    if ( designerContext == null ) {
      throw new NullPointerException();
    }
    final ReportDocumentContext reportRenderContext = designerContext.getActiveContext();
    if ( reportRenderContext == null ) {
      throw new IllegalArgumentException();
    }

    this.optionsPane.setReportDesignerContext( designerContext );

    try {
      final CrosstabBuilder originBuilder;
      if ( editedGroup != null ) {
        originBuilder =
          CrosstabEditSupport.populateBuilder( editedGroup, reportRenderContext.getReportDataSchemaModel() );
        populateDialogFromBuilder( originBuilder );
        optionsPane.setValuesFromGroup( editedGroup );
      } else {
        originBuilder = new CrosstabBuilder( reportRenderContext.getReportDataSchemaModel() );
      }

      reportRenderContext.addReportDataChangeListener( this );
      dataModelChanged( reportRenderContext );
      validateInputs( false );

      if ( performEdit() == false ) {
        return null;
      }

      if ( columnsFieldsModel.size() < 1 ) {
        return null;
      }
      if ( rowsFieldsModel.size() < 1 ) {
        return null;
      }

      final CrosstabBuilder builder = originBuilder.clearDimensions();
      configureBuilderFromOptions( builder );

      for ( int i = 0; i < detailFieldsModel.size(); i += 1 ) {
        final CrosstabDetail crosstabDetail = detailFieldsModel.get( i );
        builder.addDetails( crosstabDetail.getField(), crosstabDetail.getAggregation() );
      }

      for ( int col = 0; col < columnsFieldsModel.size(); col += 1 ) {
        final CrosstabDimension column = columnsFieldsModel.get( col );
        builder.addColumnDimension( column );
      }

      for ( int row = 0; row < rowsFieldsModel.size(); row += 1 ) {
        final CrosstabDimension rowDimension = rowsFieldsModel.get( row );
        builder.addRowDimension( rowDimension );
      }

      for ( int other = 0; other < otherFieldsModel.size(); other += 1 ) {
        final String column = (String) otherFieldsModel.get( other );
        builder.addOtherDimension( column );
      }

      final CrosstabGroup crosstabGroup = builder.create();
      optionsPane.setValuesOnGroup( crosstabGroup );
      return crosstabGroup;
    } finally {
      reportRenderContext.removeReportDataChangeListener( this );
      this.fieldListCellRenderer.setModel( null );
      this.optionsPane.setReportDesignerContext( null );
    }
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    boolean retval = true;
    if ( columnsFieldsModel.size() < 1 ) {
      retval = false;
    }
    if ( rowsFieldsModel.size() < 1 ) {
      retval = false;
    }

    getConfirmAction().setEnabled( retval );
    return retval;
  }


  private void configureBuilderFromOptions( final CrosstabBuilder builder ) {
    builder.setGroupNamePrefix( "Group " ); // NON-NLS
    optionsPane.configureCrosstabBuilder( builder );
  }

  private void populateDialogFromBuilder( final CrosstabBuilder builder ) {
    optionsPane.configureFromCrosstabBuilder( builder );
    for ( final String other : builder.getOthers() ) {
      //noinspection unchecked
      otherFieldsModel.addElement( other );
    }
    for ( final CrosstabDimension d : builder.getRows() ) {
      rowsFieldsModel.add( d );
    }
    for ( final CrosstabDimension d : builder.getColumns() ) {
      columnsFieldsModel.add( d );
    }
    for ( final CrosstabDetail detail : builder.getDetails() ) {
      detailFieldsModel.add( detail );
    }
  }
}
