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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;


import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;

public class SeparateRowModel extends AbstractRowModel
{
  private boolean validatedSize;
  private long rowSpacing;

  public SeparateRowModel()
  {
  }

  public void addRow()
  {
    super.addRow();
  }

  public long getRowSpacing()
  {
    return rowSpacing;
  }

  public void initialize(final TableRenderBox table)
  {
    rowSpacing = table.getRowSpacing().resolve(0);
  }

  public void validateSizes()
  {
    if (validatedSize)
    {
      return;
    }

    int maxRowSpan = 0;
    final TableRow[] rows = getRows();
    final int rowCount = rows.length;
    for (int i = 0; i < rowCount; i++)
    {
      final TableRow row = rows[i];
      final int cs = row.getMaximumRowSpan();
      if (cs > maxRowSpan)
      {
        maxRowSpan = cs;
      }
    }

    // first, find out how much space is already used.
    final long[] preferredSizes = new long[rowCount];
    // For each rowspan ...
    for (int rowspan = 1; rowspan <= maxRowSpan; rowspan += 1)
    {
      for (int rowIdx = 0; rowIdx < rowCount; rowIdx++)
      {
        final TableRow row = rows[rowIdx];
        final long preferredSize = row.getPreferredSize(rowspan);

        distribute(preferredSize, preferredSizes, rowIdx, rowspan);
      }
    }

    for (int i = 0; i < rowCount; i++)
    {
      final TableRow row = rows[i];
      row.setPreferredSize(preferredSizes[i]);
    }

    validatedSize = true;
  }


  public void validateActualSizes()
  {
    validateSizes();

    int maxRowSpan = 0;
    final TableRow[] rows = getRows();
    final int rowCount = rows.length;
    for (int i = 0; i < rowCount; i++)
    {
      final TableRow row = rows[i];
      final int cs = row.getMaxValidatedRowSpan();
      if (cs > maxRowSpan)
      {
        maxRowSpan = cs;
      }
    }

    // first, find out how much space is already used.
    // This follows the classical model.

    final long[] trailingSizes = new long[rowCount];
    // For each rowspan ...
    for (int rowspan = 1; rowspan <= maxRowSpan; rowspan += 1)
    {
      for (int rowIdx = 0; rowIdx < trailingSizes.length; rowIdx++)
      {
        final TableRow row = rows[rowIdx];
        final long size = row.getValidatedTrailingSize(rowspan);

        distribute(size, trailingSizes, rowIdx, rowspan);
      }
    }

    for (int i = 0; i < trailingSizes.length; i++)
    {
      final TableRow row = rows[i];
      final long validateSize = trailingSizes[i] + row.getValidatedLeadingSize();
      row.setValidateSize(Math.max(row.getPreferredSize(), validateSize));
    }
  }

  private void distribute(final long usedSpace, final long[] allSpaces,
                          final int colIdx, final int colspanX)
  {
    final int maxColspan = Math.min(colIdx + colspanX, allSpaces.length) - colIdx;
    long usedPrev = 0;
    final int maxSize = Math.min(allSpaces.length, colIdx + maxColspan);
    for (int i = colIdx; i < maxSize; i++)
    {
      usedPrev += allSpaces[i];
    }

    if (usedSpace <= usedPrev)
    {
      // no need to expand the cells.
      return;
    }

    final long distSpace = (usedSpace - usedPrev);
    final long delta = distSpace / maxColspan;
    for (int i = 0; i < maxColspan - 1; i++)
    {
      allSpaces[colIdx + i] += delta;
    }
    // any uneven remainder gets added to the last column
    allSpaces[colIdx + maxColspan - 1] += distSpace - ((maxColspan - 1) * delta);
  }

  public void clear()
  {
    final TableRow[] rows = getRows();
    final int rowCount = rows.length;
    for (int i = 0; i < rowCount; i++)
    {
      final TableRow row = rows[i];
      row.clear();
    }
  }
}
