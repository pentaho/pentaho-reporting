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

package org.pentaho.reporting.engine.classic.core.layout.model.table.cells;

/**
 * A data-cell holds a reference to a TableCellRenderBox. It contains data and is usually found in the upper left corner
 * of an cell.
 *
 * @author Thomas Morgner
 */
public class DataCell extends TableCell {
  // The instance-id of the cell ..
  private Object cellRenderBox;

  public DataCell( final int rowSpan, final int colSpan, final Object cellRenderBox ) {
    super( rowSpan, colSpan );

    if ( cellRenderBox == null ) {
      throw new NullPointerException();
    }

    this.cellRenderBox = cellRenderBox;
  }

  public Object getCellRenderBox() {
    return cellRenderBox;
  }

  public String toString() {
    return "DataCell{" + "rowSpan=" + getRowSpan() + ", colSpan=" + getColSpan() + ", cellRenderBox=" + cellRenderBox
        + '}';
  }
}
