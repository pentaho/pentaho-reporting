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

package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class SeparateRowModel extends AbstractRowModel {
  private static class ProcessedRowVoter {
    private int rejectedRow;
    private int acceptedRow;

    private ProcessedRowVoter( final int rowCount, final int previouslyProcessedRows ) {
      this.rejectedRow = rowCount;
      this.acceptedRow = previouslyProcessedRows;
    }

    public void reject( int row ) {
      rejectedRow = Math.min( row, rejectedRow );
    }

    public void accept( int row ) {
      acceptedRow = Math.max( row, acceptedRow );
    }

    public int getProcessedRows() {
      return Math.min( rejectedRow, acceptedRow );
    }
  }

  private static final Log logger = LogFactory.getLog( SeparateRowModel.class );
  private int validatedRowCount;
  private int validatedActualSizes;
  private long rowSpacing;

  public SeparateRowModel() {
  }

  public long getRowSpacing() {
    return rowSpacing;
  }

  public void setRowSpacing( final long rowSpacing ) {
    this.rowSpacing = rowSpacing;
  }

  public void initialize( final TableRenderBox table ) {
    rowSpacing = table.getRowSpacing().resolve( 0 );
  }

  private interface RowProcessingDelegate {
    public long run( TableRow r, int rowSpan );
  }

  private ProcessedRowVoter compute( final long[] preferredSizes, final int validatedRowCount,
      RowProcessingDelegate delegate ) {
    int rowCount = getRowCount();
    final ProcessedRowVoter voter = new ProcessedRowVoter( rowCount, validatedRowCount );
    if ( preferredSizes.length == 0 ) {
      return voter;
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug( "Computing: " + ( rowCount - validatedRowCount ) + " rows. " + validatedRowCount );
    }

    int currentRowSpan = 0;
    boolean processNextLevel = true;
    while ( processNextLevel ) {
      currentRowSpan += 1;
      processNextLevel = false;

      for ( int rowIdx = validatedRowCount; rowIdx < rowCount; rowIdx++ ) {
        final TableRow row = getRow( rowIdx );
        final int maximumRowSpan = row.getMaximumRowSpan();
        if ( maximumRowSpan >= currentRowSpan ) {
          final long preferredSize = delegate.run( row, currentRowSpan );
          distribute( preferredSize, preferredSizes, rowIdx, currentRowSpan );
          if ( maximumRowSpan > currentRowSpan ) {
            processNextLevel = true;
          }

          if ( rowIdx + currentRowSpan <= rowCount ) {
            voter.accept( rowIdx + currentRowSpan );
          } else {
            voter.reject( rowIdx );
          }
        }
      }
    }
    return voter;
  }

  public void validatePreferredSizes() {
    final int rowCount = getRowCount();
    if ( this.validatedRowCount == rowCount ) {
      return;
    }

    long[] preferredSizes = getPreferredSizes( this.validatedRowCount );

    // first, find out how much space is already used.
    // For each rowspan ...
    ProcessedRowVoter voter = compute( preferredSizes, this.validatedRowCount, new RowProcessingDelegate() {
      public long run( final TableRow r, final int rowSpan ) {
        return r.getPreferredSize( rowSpan );
      }
    } );

    applyPreferredSizes( preferredSizes, this.validatedRowCount, rowCount );
    this.validatedRowCount = voter.getProcessedRows();
  }

  public void validateActualSizes() {
    validatePreferredSizes();

    final int rowCount = getRowCount();
    if ( this.validatedActualSizes == rowCount ) {
      return;
    }

    final long[] trailingSizes = getValidateSizes( this.validatedActualSizes );
    ProcessedRowVoter voter = compute( trailingSizes, this.validatedActualSizes, new RowProcessingDelegate() {
      public long run( final TableRow r, final int rowSpan ) {
        return r.getValidatedTrailingSize( rowSpan );
      }
    } );

    applyValidateSizes( trailingSizes, this.validatedActualSizes, rowCount );
    this.validatedActualSizes = voter.getProcessedRows();
  }

  private void distribute( final long usedSpace, final long[] allSpaces, final int colIdx, final int colspanX ) {
    final int maxColspan = Math.min( colIdx + colspanX, allSpaces.length ) - colIdx;
    long usedPrev = 0;
    final int maxSize = Math.min( allSpaces.length, colIdx + maxColspan );
    for ( int i = colIdx; i < maxSize; i++ ) {
      usedPrev += allSpaces[i];
    }

    if ( usedSpace <= usedPrev ) {
      // no need to expand the cells.
      return;
    }

    final long distSpace = ( usedSpace - usedPrev );
    final long delta = distSpace / maxColspan;
    for ( int i = 0; i < maxColspan - 1; i++ ) {
      allSpaces[colIdx + i] += delta;
    }
    // any uneven remainder gets added to the last column
    allSpaces[colIdx + maxColspan - 1] += distSpace - ( ( maxColspan - 1 ) * delta );
  }

  public void clear() {
    this.validatedActualSizes = 0;
    this.validatedRowCount = 0;

    final TableRowImpl[] rows = getRows();
    final int rowCount = rows.length;
    for ( int i = 0; i < rowCount; i++ ) {
      final TableRowImpl row = rows[i];
      row.clear();
    }
  }

  public int getValidatedRowCount() {
    return validatedRowCount;
  }

  public int getValidatedActualSizes() {
    return validatedActualSizes;
  }

  public void prune( final int rows ) {
    super.prune( rows );
    if ( rows <= 1 ) {
      return;
    }

    validatedActualSizes -= rows;
    validatedRowCount -= rows;
  }

  public void setDebugInformation( final ElementType elementType, final InstanceID instanceID ) {

  }

  public String toString() {
    TableRowImpl[] rows = getRows();
    StringBuilder b = new StringBuilder();
    for ( TableRowImpl r : rows ) {
      b.append( r );
      b.append( "\n" );
    }

    return "SeparateRowModel{" + "rows=\n" + b + ", rowSpacing=" + rowSpacing + ", validatedActualSizes="
        + validatedActualSizes + ", validatedRowCount=" + validatedRowCount + '}';
  }
}
