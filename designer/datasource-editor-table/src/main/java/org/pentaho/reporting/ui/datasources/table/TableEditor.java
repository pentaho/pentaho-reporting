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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.designtime.swing.FormattingTableCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellRenderer;
import org.pentaho.reporting.libraries.designtime.swing.date.DateCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.date.TimeCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TableEditor extends JTable {
  private static final Log logger = LogFactory.getLog( TableEditor.class );

  private TableEditorModel tableModel;
  private TableColumn selectedColumn;
  private EditableHeader tableHeader;

  public TableEditor() {
    tableModel = new TableEditorModel();
    tableHeader = new EditableHeader( getColumnModel(), tableModel );

    setTableHeader( tableHeader );
    setModel( tableModel );
    setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );

    final SimpleDateFormat isoDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
    setDefaultRenderer( Date.class, new FormattingTableCellRenderer( isoDateFormat ) );
    setDefaultRenderer( java.sql.Date.class, new FormattingTableCellRenderer( new SimpleDateFormat( "yyyy-MM-dd" ) ) );
    setDefaultRenderer( Time.class, new FormattingTableCellRenderer( new SimpleDateFormat( "HH:mm:ss.SSS" ) ) );
    setDefaultRenderer( Timestamp.class, new FormattingTableCellRenderer( isoDateFormat ) );
    setDefaultRenderer( String.class, new GenericCellRenderer() );
    setDefaultRenderer( Object.class, new GenericCellRenderer() );

    setDefaultEditor( Number.class, new GenericCellEditor( BigDecimal.class ) );
    setDefaultEditor( Integer.class, new GenericCellEditor( Integer.class ) );
    setDefaultEditor( Float.class, new GenericCellEditor( Float.class ) );
    setDefaultEditor( Double.class, new GenericCellEditor( Double.class ) );
    setDefaultEditor( Short.class, new GenericCellEditor( Short.class ) );
    setDefaultEditor( Byte.class, new GenericCellEditor( Byte.class ) );
    setDefaultEditor( Long.class, new GenericCellEditor( Long.class ) );
    setDefaultEditor( BigInteger.class, new GenericCellEditor( BigInteger.class ) );
    setDefaultEditor( BigDecimal.class, new GenericCellEditor( BigDecimal.class ) );
    setDefaultEditor( String.class, new GenericCellEditor( String.class, true ) );
    setDefaultEditor( Object.class, new GenericCellEditor( String.class, false ) );
    setDefaultEditor( Date.class, new DateCellEditor( Date.class ) );
    setDefaultEditor( java.sql.Date.class, new DateCellEditor( java.sql.Date.class ) );
    setDefaultEditor( Time.class, new TimeCellEditor( Time.class ) );
    setDefaultEditor( Timestamp.class, new DateCellEditor( Timestamp.class ) );

    updateUI();
  }

  public void applyLocaleSettings( final LocaleSettings localeSettings ) {
    try {
      final SimpleDateFormat isoDateFormat =
        new SimpleDateFormat( localeSettings.getDatetimeFormatPattern(), localeSettings.getLocale() );
      isoDateFormat.setTimeZone( localeSettings.getTimeZone() );
      setDefaultRenderer( Date.class, new FormattingTableCellRenderer( isoDateFormat ) );
      setDefaultRenderer( Timestamp.class, new FormattingTableCellRenderer( isoDateFormat ) );

      final DateCellEditor dateCellEditor = new DateCellEditor( Date.class );
      dateCellEditor.setDateFormat( isoDateFormat );
      setDefaultEditor( Date.class, dateCellEditor );

      final DateCellEditor timestampEditor = new DateCellEditor( Timestamp.class );
      timestampEditor.setDateFormat( isoDateFormat );
      setDefaultEditor( Timestamp.class, timestampEditor );

    } catch ( Exception e ) {
      logger.warn( "Invalid format string found in locale settings", e );
    }
    try {
      final SimpleDateFormat dateFormat =
        new SimpleDateFormat( localeSettings.getDateFormatPattern(), localeSettings.getLocale() );
      dateFormat.setTimeZone( localeSettings.getTimeZone() );
      setDefaultRenderer( java.sql.Date.class, new FormattingTableCellRenderer( dateFormat ) );

      final DateCellEditor dateCellEditor = new DateCellEditor( java.sql.Date.class );
      dateCellEditor.setDateFormat( dateFormat );
      setDefaultEditor( java.sql.Date.class, dateCellEditor );
    } catch ( Exception e ) {
      logger.warn( "Invalid format string found in locale settings", e );
    }
    try {
      final SimpleDateFormat timeFormat =
        new SimpleDateFormat( localeSettings.getTimeFormatPattern(), localeSettings.getLocale() );
      timeFormat.setTimeZone( localeSettings.getTimeZone() );
      setDefaultRenderer( Time.class, new FormattingTableCellRenderer( timeFormat ) );

      final TimeCellEditor timeCellEditor = new TimeCellEditor( Time.class );
      timeCellEditor.setDateFormat( timeFormat );
      setDefaultEditor( Time.class, timeCellEditor );
    } catch ( Exception e ) {
      logger.warn( "Invalid format string found in locale settings", e );
    }

  }

  /**
   * Creates default columns for the table from the data model using the <code>getColumnCount</code> method defined in
   * the <code>TableModel</code> interface.
   * <p/>
   * Clears any existing columns before creating the new columns based on information from the model.
   *
   * @see #getAutoCreateColumnsFromModel
   */
  public void createDefaultColumnsFromModel() {
    final TableModel m = getModel();
    if ( m != null ) {
      // Remove any current columns
      final TableColumnModel cm = getColumnModel();
      while ( cm.getColumnCount() > 0 ) {
        cm.removeColumn( cm.getColumn( 0 ) );
      }

      // Create new columns from the data model info
      for ( int i = 0; i < m.getColumnCount(); i++ ) {
        if ( i == 0 ) {
          final TableColumn column = new TableColumn( i );
          column.setCellRenderer( tableHeader.getDefaultRenderer() );
          addColumn( column );
          continue;
        }

        final EditableHeaderTableColumn newColumn = new EditableHeaderTableColumn( i );
        newColumn.setHeaderEditor( new TypedHeaderCellEditor() );
        addColumn( newColumn );
      }
    }
  }

  public void addColumn( final TableColumn column ) {
    stopEditing();
    if ( column.getHeaderValue() == null ) {
      final int modelColumn = column.getModelIndex();
      final String columnName = getModel().getColumnName( modelColumn );
      if ( modelColumn == 0 ) {
        column.setResizable( false );
        column.setHeaderValue( columnName );
        column.setPreferredWidth( 30 );
        column.setMaxWidth( 30 );
        column.setMinWidth( 30 );
      } else {
        final Class columnType = getModel().getColumnClass( modelColumn );
        column.setHeaderValue( new TypedHeaderInformation( columnType, columnName ) );
      }
    }
    getColumnModel().addColumn( column );

  }


  public void addColumn( final String aHeaderName ) {
    stopEditing();
    tableHeader.removeEditor();
    tableModel.addColumn( aHeaderName, String.class );

  }

  public void addRow() {
    stopEditing();
    final int row = getSelectedRow();
    if ( row == -1 ) {
      tableModel.addRow();
      setRowSelectionInterval( getRowCount() - 1, getRowCount() - 1 );
    } else {
      tableModel.addRow( row + 1 );
      setRowSelectionInterval( row + 1, row + 1 );
    }
  }

  public void removeRow() {
    stopEditing();
    final int[] rows = getSelectedRows();
    for ( int i = rows.length - 1; i >= 0; i -= 1 ) {
      final int row = rows[ i ];
      tableModel.removeRow( row );
    }
    tableModel.fireTableDataChanged();
  }

  public void removeColumn() {
    stopEditing();
    tableHeader.removeEditor();
    final int modelIndex = selectedColumn.getModelIndex();
    if ( modelIndex == 0 ) {
      return;
    }
    tableModel.removeColumn( modelIndex - 1 );
  }

  public void setSelectedColumn( final TableColumn aSelectedColumn ) {
    selectedColumn = aSelectedColumn;
  }

  public void setTableEditorModel( final TableModel model ) {
    tableModel.copyInto( model );
  }

  public TableModel getTableEditorModel() {
    return tableModel.createModel();
  }

  public void stopEditing() {
    final TableCellEditor cellEditor = getCellEditor();
    if ( cellEditor != null ) {
      cellEditor.stopCellEditing();
    }
  }
}
