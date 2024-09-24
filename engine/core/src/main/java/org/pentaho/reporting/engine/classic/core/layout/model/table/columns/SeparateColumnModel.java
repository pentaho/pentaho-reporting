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

package org.pentaho.reporting.engine.classic.core.layout.model.table.columns;

import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;

/**
 * Creation-Date: 18.07.2006, 16:46:11
 *
 * @author Thomas Morgner
 */
public class SeparateColumnModel extends AbstractColumnModel {
  private long validationTrack;
  private long cachedSize;

  public SeparateColumnModel() {
    validationTrack = -1;
  }

  public long getCachedSize() {
    return cachedSize;
  }

  public void validateSizes( final TableRenderBox table ) {
    if ( isValidated() && ( validationTrack == table.getChangeTracker() ) ) {
      return;
    }

    int maxColSpan = 0;
    final TableColumn[] columns = getColumns();
    final int colCount = columns.length;
    for ( int i = 0; i < colCount; i++ ) {
      final TableColumn column = columns[i];
      final int cs = column.getMaxColspan();
      if ( cs > maxColSpan ) {
        maxColSpan = cs;
      }
    }

    if ( colCount == 0 ) {
      validationTrack = table.getChangeTracker();
      return;
    }

    // first, find out how much space is already used.
    final long[] cachedSizes = new long[colCount];

    // For each colspan distribute the content.
    // The 1-column size also gets the preferred size ...
    for ( int colIdx = 0; colIdx < colCount; colIdx++ ) {
      final TableColumn column = columns[colIdx];
      final long cachedSize = column.getCachedSize( 1 );

      cachedSizes[colIdx] = cachedSize;
    }

    for ( int colspan = 2; colspan <= maxColSpan; colspan += 1 ) {
      for ( int colIdx = 0; colIdx < colCount; colIdx++ ) {
        final TableColumn column = columns[colIdx];
        final long cachedSize = column.getCachedSize( colspan );

        distribute( cachedSize, cachedSizes, colIdx, colspan );
      }
    }

    this.cachedSize = 0;
    int variableColumns = 0;
    for ( int i = 0; i < colCount; i++ ) {
      final TableColumn column = columns[i];
      if ( column.isValidated() ) {
        this.cachedSize += column.getEffectiveSize();
        continue;
      }

      variableColumns += 1;
      final long cachedSize = cachedSizes[i];
      this.cachedSize += cachedSize;
      column.setEffectiveSize( cachedSize );
    }

    if ( variableColumns > 0 ) {
      final long tableSize = Math.max( table.getContentAreaX2() - table.getContentAreaX1(), this.cachedSize );
      // the space we are able to distribute .. (This can be negative, if we
      // have to remove space to get to the defined table size!)
      // The space that is available for the content from the cells. The
      // border-spacing eats some space as well (already in the pref-size-value)
      //
      // Todo: This should be distributed by column weight, not evenly
      final long extraSpace = tableSize - cachedSize;
      final long extraSpacePerCol = extraSpace / variableColumns;
      for ( int i = 0; i < colCount; i++ ) {
        final TableColumn column = columns[i];
        if ( column.isValidated() ) {
          continue;
        }

        final long colSize = column.getEffectiveSize() + extraSpacePerCol;
        column.setEffectiveSize( colSize );
        column.setValidated( true );
      }
      this.cachedSize = tableSize;
    }

    validationTrack = table.getChangeTracker();
  }

  private void distribute( final long usedSpace, final long[] allSpaces, final int colIdx, final int colspan ) {
    final int maxColspan = Math.min( colIdx + colspan, allSpaces.length ) - colIdx;
    final int maxSize = Math.min( allSpaces.length, colIdx + maxColspan );

    // compute the space occupied by all columns of the range.
    // That has been computed earlier (for 'colspan - 1') and is zero if
    // there is no colspan at all
    long usedPrev = 0;
    for ( int i = colIdx; i < maxSize; i++ ) {
      usedPrev += allSpaces[i];
    }

    if ( usedSpace <= usedPrev ) {
      // no need to expand the cells, as the requested size for the whole
      // span will be less than the size occupied by all the columns ..
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
}
