/*
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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.pentaho.reporting.engine.classic.core.util.BulkArrayList;

public abstract class AbstractRowModel implements TableRowModel
{
  private BulkArrayList<TableRow> rows;

  public AbstractRowModel()
  {
    this.rows = new BulkArrayList<TableRow>(1000);
  }

  public void addRow()
  {
    rows.add(new TableRow());
  }

  public int getRowCount()
  {
    return rows.size();
  }

  public TableRow getRow(final int i)
  {
    return rows.get(i);
  }

  public TableRow[] getRows ()
  {
    return rows.toArray(new TableRow[rows.size()]);
  }

  public void prune (final int rows)
  {
    if (rows <= 1)
    {
      return;
    }

    long validatedSize = 0;
    long preferredSize = 0;
    int split = 0;
    int runningMaxRowSpan = 0;
    for (int r = 0; r < rows; r+= 1)
    {
      runningMaxRowSpan -= 1;
      final TableRow row = getRow(r);
      validatedSize += row.getValidateSize();
      preferredSize += row.getPreferredSize();

      runningMaxRowSpan = Math.max (runningMaxRowSpan, row.getMaximumRowSpan());
      if (runningMaxRowSpan == 1)
      {
        split = r;
      }
    }

    if (split == 0)
    {
      return;
    }

    final TableRow newRow = new TableRow();
    newRow.updateDefinedSize(1, preferredSize);
    newRow.updateValidatedSize(1, 0, validatedSize);
    newRow.setPreferredSize(preferredSize);
    newRow.setValidateSize(validatedSize);

    this.rows.set(0, newRow);
    this.rows.removeRange(1, rows - 1);
  }

  public long getRowSpacing()
  {
    return 0;
  }

  public long getValidateSize(final int rowNumber)
  {
    return getRow(rowNumber).getValidateSize();
  }

  public void updateDefinedSize(final int rowNumber, final int rowSpan, final long preferredSize)
  {
    getRow(rowNumber).updateDefinedSize(rowSpan, preferredSize);
  }

  public void updateValidatedSize(final int rowNumber, final int rowSpan, final long leading, final long height)
  {
    getRow(rowNumber).updateValidatedSize(rowSpan, leading, height);
  }

  public long getValidatedRowSize(final int rowNumber)
  {
    return getRow(rowNumber).getValidateSize();
  }

  public long getPreferredRowSize(final int rowNumber)
  {
    return getRow(rowNumber).getPreferredSize();
  }

  public int getMaximumRowSpan(final int rowNumber)
  {
    return getRow(rowNumber).getMaximumRowSpan();
  }
}
