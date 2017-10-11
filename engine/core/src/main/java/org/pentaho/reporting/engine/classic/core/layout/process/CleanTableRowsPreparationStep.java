/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.AutoRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.TableRowModel;
import org.pentaho.reporting.libraries.base.util.GenericObjectTable;

public class CleanTableRowsPreparationStep extends IterateStructuralProcessStep {
  public static class Cell {
    private int rowIndex;
    private int colIndex;
    private int rowSpan;
    private long y;

    public Cell( final int rowIndex, final int colIndex, final int rowSpan, final long y ) {
      this.rowIndex = rowIndex;
      this.colIndex = colIndex;
      this.rowSpan = rowSpan;
      this.y = y;
    }

    public int getRowIndex() {
      return rowIndex;
    }

    public int getColIndex() {
      return colIndex;
    }

    public int getRowSpan() {
      return rowSpan;
    }

    public long getY() {
      return y;
    }
  }

  private GenericObjectTable<Cell> cells;

  private int requiredAdditionalRows;
  private int trueRowCount;
  private TableRowModel rowModel;

  private int autoBoxIndex;
  private int firstRowEncountered;
  private int currentRow;

  public CleanTableRowsPreparationStep() {
  }

  public int process( final TableSectionRenderBox renderBox, final long pageOffset ) {
    this.firstRowEncountered = -1;
    this.rowModel = renderBox.getRowModel();
    this.trueRowCount = 0;

    this.cells = new GenericObjectTable<Cell>();
    // compute the effective row spans for each row and the sizes it spans
    startProcessing( renderBox );

    return computeSafeCut( pageOffset, cells, trueRowCount );
  }

  public int getFirstRowEncountered() {
    return firstRowEncountered;
  }

  protected boolean startAutoBox( final RenderBox box ) {
    autoBoxIndex = -1;
    // treat as transient ..
    return true;
  }

  protected void finishAutoBox( final RenderBox box ) {
    AutoRenderBox autobox = (AutoRenderBox) box;
    autobox.setRowIndex( autoBoxIndex );
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    int row = box.getRowIndex();
    trueRowCount = row + 1;
    if ( autoBoxIndex == -1 ) {
      autoBoxIndex = row;
    }
    currentRow = row;
    seenRow( row );

    return true;
  }

  private void seenRow( int rowNumber ) {
    if ( firstRowEncountered == -1 ) {
      firstRowEncountered = rowNumber;
    }

    int currentRowMaxRowSpan = rowModel.getMaximumRowSpan( rowNumber );
    if ( this.requiredAdditionalRows > 0 ) {
      // if we have spanned rows pending, reduce the span with each new row started, until every row is consumed.
      this.requiredAdditionalRows -= 1;
    }
    this.requiredAdditionalRows += currentRowMaxRowSpan;
    this.requiredAdditionalRows -= 1;
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    int row = currentRow;
    int col = box.getColumnIndex();
    int rowSpan = box.getRowSpan();

    Cell c = new Cell( row, col, rowSpan, box.getY() );
    for ( int r = 0; r < rowSpan; r += 1 ) {
      cells.setObject( r + row, col, c );
    }

    return false;
  }

  public static int
    computeSafeCut( final long pageOffset, final GenericObjectTable<Cell> cells, final int trueRowCount ) {
    int rowForPageOffset = findRowForPageOffset( pageOffset, cells, trueRowCount );
    if ( rowForPageOffset == 0 ) {
      // none of the rows can be cut, the whole table must be preserved.
      return 0;
    }
    if ( rowForPageOffset == trueRowCount ) {
      // all the table content is before the page-offset, so we can remove all table elements.
      return trueRowCount;
    }

    // algorithm: Start on the right hand side of the table at the rowPagePageOffset. Now move
    // the cutting point upwards until you reach a start of a cell. Move towards the left until
    // you hit a spanned cell that is not starting at the current row. Continue moving upwards
    // and left until you reach the first column of the cell. This marks the safe-cut-off point
    // for removing cells.

    int colIdx = cells.getColumnCount() - 1;
    int rowIdx = rowForPageOffset;

    while ( colIdx >= 0 ) {
      Cell c = cells.getObject( rowIdx, colIdx );
      if ( c == null ) {
        // move left when spanned cell area ..
        colIdx -= 1;
        continue;
      }
      if ( c.getRowIndex() == rowIdx ) {
        // move left on start of cell
        colIdx -= 1;
        continue;
      }

      // move upwards on spanned cell.
      rowIdx -= 1;
    }

    return rowIdx;
  }

  private static int findRowForPageOffset( final long pageOffset, final GenericObjectTable<Cell> cells,
      final int trueRowCount ) {
    int selectedRow = 0;
    for ( int row = 0; row < trueRowCount; row += 1 ) {
      long pos = -1;
      for ( int col = 0; col < cells.getColumnCount(); col += 1 ) {
        Cell c = cells.getObject( row, col );
        if ( c == null ) {
          continue;
        }

        if ( c.getRowIndex() == row ) {
          pos = c.getY();
        }
      }

      if ( pos != -1 ) {
        if ( pageOffset < pos ) {
          break;
        } else {
          selectedRow = row;
        }
      }
    }
    return selectedRow;
  }
}
