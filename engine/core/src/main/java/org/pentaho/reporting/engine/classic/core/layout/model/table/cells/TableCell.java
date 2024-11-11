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


package org.pentaho.reporting.engine.classic.core.layout.model.table.cells;

/**
 * Creation-Date: 10.09.2006, 17:28:05
 *
 * @author Thomas Morgner
 */
public abstract class TableCell {
  private int rowSpan;
  private int colSpan;

  public TableCell( final int rowSpan, final int colSpan ) {
    if ( rowSpan < 1 ) {
      throw new IllegalArgumentException();
    }
    if ( colSpan < 1 ) {
      throw new IllegalArgumentException();
    }

    this.rowSpan = rowSpan;
    this.colSpan = colSpan;
  }

  public int getRowSpan() {
    return rowSpan;
  }

  public int getColSpan() {
    return colSpan;
  }

  public String toString() {
    return "TableCell{" + "rowSpan=" + rowSpan + ", colSpan=" + colSpan + '}';
  }
}
