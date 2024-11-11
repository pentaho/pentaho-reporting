/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
