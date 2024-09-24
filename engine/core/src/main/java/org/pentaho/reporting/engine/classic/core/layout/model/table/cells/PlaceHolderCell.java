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
 * Creation-Date: 10.09.2006, 17:27:54
 *
 * @author Thomas Morgner
 */
public class PlaceHolderCell extends TableCell {
  private DataCell sourceCell;

  public PlaceHolderCell( final DataCell sourceCell, final int rowSpan, final int colSpan ) {
    super( rowSpan, colSpan );
    if ( sourceCell == null ) {
      throw new NullPointerException();
    }
    this.sourceCell = sourceCell;
  }

  public DataCell getSourceCell() {
    return sourceCell;
  }

  public String toString() {
    return "PlaceHolderCell{" + "rowSpan=" + getRowSpan() + ", colSpan=" + getColSpan() + ", sourceCell=" + sourceCell
        + '}';
  }
}
