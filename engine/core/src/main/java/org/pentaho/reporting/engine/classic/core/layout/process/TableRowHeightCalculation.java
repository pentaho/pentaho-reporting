/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.TableRowModel;

/**
 * Updates the row heights in the model for a table. This is slightly more complex than the row-layout system, as
 * row-spans distribute extra heights to all cells involved.
 *
 * @author Thomas Morgner
 */
public class TableRowHeightCalculation {
  private static class TableInfoStructure {
    private TableRenderBox table;
    private TableInfoStructure parent;
    private TableColumnModel columnModel;
    private TableRowModel rowModel;
    private int rowNumber;
    private long filledHeight;

    public TableInfoStructure( final TableRenderBox table, final TableInfoStructure parent ) {
      this.table = table;
      this.parent = parent;
      this.columnModel = table.getColumnModel();
    }

    public TableInfoStructure pop() {
      return parent;
    }

    public long getFilledHeight() {
      return filledHeight;
    }

    public void setFilledHeight( final long filledHeight ) {
      if ( filledHeight < 0 ) {
        throw new IllegalStateException( "Filled height is negative: " + filledHeight );
      }
      this.filledHeight = filledHeight;
    }

    public TableRenderBox getTable() {
      return table;
    }

    public TableColumnModel getColumnModel() {
      return columnModel;
    }

    public void setRowModel( final TableRowModel rowModel ) {
      this.rowModel = rowModel;
    }

    public void setRowNumber( final int rowNumber ) {
      this.rowNumber = rowNumber;
    }

    public long getPreferredRowSize( final int offset ) {
      return rowModel.getPreferredRowSize( this.rowNumber + offset );
    }

    public long getValidatedRowSize( final int offset ) {
      return rowModel.getValidatedRowSize( this.rowNumber + offset );
    }

    public void updateValidatedSize( final int rowSpan, final int leading, final long cachedHeight ) {
      rowModel.updateValidatedSize( this.rowNumber, rowSpan, leading, cachedHeight );
    }
  }

  private TableInfoStructure currentTable;
  private boolean secondPass;
  private TableRowHeightApplyStep applyStep;

  public TableRowHeightCalculation( final boolean secondPass ) {
    this.applyStep = new TableRowHeightApplyStep();
    this.secondPass = secondPass;
  }

  public void reset() {
    currentTable = null;
  }

  public void startTableBox( final TableRenderBox box ) {
    currentTable = new TableInfoStructure( box, currentTable );
  }

  public void finishTable( final TableRenderBox box ) {
    final long newHeight = currentTable.getFilledHeight();
    box.setCachedHeight( newHeight );

    currentTable = currentTable.pop();
  }

  public void startTableSection( final TableSectionRenderBox sectionBox ) {
    currentTable.setRowModel( sectionBox.getRowModel() );
  }

  public void startTableCell( final TableCellRenderBox box ) {
    if ( secondPass == false ) {
      long size = 0;
      for ( int i = 0; i < box.getRowSpan(); i += 1 ) {
        size += currentTable.getPreferredRowSize( i );
      }

      box.setCachedHeight( size );
    } else {
      long size = 0;
      for ( int i = 0; i < box.getRowSpan(); i += 1 ) {
        size += currentTable.getValidatedRowSize( i );
      }

      box.setCachedHeight( size );
    }
  }

  public void finishTableCell( final TableCellRenderBox cellBox, final long cachedHeight ) {
    if ( secondPass == false ) {
      final int rowSpan = cellBox.getRowSpan();
      currentTable.updateValidatedSize( rowSpan, 0, cachedHeight );
    } else {
      long size = 0;
      for ( int i = 0; i < cellBox.getRowSpan(); i += 1 ) {
        size += currentTable.getValidatedRowSize( i );
      }

      cellBox.setCachedHeight( size );
    }
  }

  public void startTableRow( final TableRowRenderBox box ) {
    currentTable.setRowNumber( box.getRowIndex() );
  }

  public void finishTableSection( final TableSectionRenderBox section ) {
    final TableRowModel rowModel = section.getRowModel();
    if ( section.getRowModelAge() != section.getChangeTracker() ) {
      rowModel.validateActualSizes();
      section.setRowModelAge( section.getChangeTracker() );
    }

    final long usedTableBodyHeight = applyStep.start( section );

    currentTable.setFilledHeight( usedTableBodyHeight + currentTable.getFilledHeight() );
    currentTable.setRowModel( null );
  }
}
