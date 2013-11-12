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
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.pentaho.reporting.engine.classic.core.util.BulkArrayList;

public class RowSizeCache
{
  private long[] validatedSizes;
  private int validateSizesFillState;

  public RowSizeCache()
  {
  }

  private int computeMaxArraySize(long[] array, int rowCount)
  {
    if (array == null)
    {
      return rowCount;
    }
    return Math.max(rowCount, array.length + 2000);
  }

  public long[] get(int limit, BulkArrayList<TableRowImpl> rows)
  {
    int rowCount = rows.size();
    if (this.validatedSizes == null || this.validatedSizes.length < rowCount)
    {
      int growth = computeMaxArraySize(this.validatedSizes, rowCount);
      long[] newValidatedSizes = new long[growth];
      if (this.validatedSizes != null)
      {
        System.arraycopy(this.validatedSizes, 0, newValidatedSizes, 0, this.validatedSizes.length);
      }
      this.validatedSizes = newValidatedSizes;
    }

    rows.foreach(new BulkArrayList.Func<TableRowImpl>()
    {
      public void process(final TableRowImpl value, final int index)
      {
        validatedSizes[index] = value.getValidateSize();
      }
    }, this.validateSizesFillState, limit);
    return validatedSizes;
  }

  public void apply(final long[] trailingSizes,
                    final int start,
                    final int end,
                    BulkArrayList<TableRowImpl> rows)
  {
    rows.foreach(new BulkArrayList.Func<TableRowImpl>()
    {
      public void process(final TableRowImpl row, final int i)
      {
        final long validateSize = trailingSizes[i] + row.getValidatedLeadingSize();
        row.setValidateSize(Math.max(row.getPreferredSize(), validateSize));
      }
    }, start, end);
    validateSizesFillState = end;
  }

}
