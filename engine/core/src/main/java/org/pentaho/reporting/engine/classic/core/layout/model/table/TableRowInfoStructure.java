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

package org.pentaho.reporting.engine.classic.core.layout.model.table;

import org.pentaho.reporting.engine.classic.core.layout.model.table.cells.RemovedCell;
import org.pentaho.reporting.engine.classic.core.layout.model.table.cells.TableCell;

import java.util.ArrayList;

/**
 * Creation-Date: 10.09.2006, 20:01:18
 *
 * @author Thomas Morgner
 */
public class TableRowInfoStructure implements Cloneable {
  private ArrayList<TableCell> cells;
  private boolean validationDone;
  private int rowNumber;

  public TableRowInfoStructure() {
    cells = new ArrayList<TableCell>();
  }

  public void addCell( final TableCell cell ) {
    if ( cell == null ) {
      throw new NullPointerException();
    }
    cells.add( cell );
  }

  public int getCellCount() {
    return cells.size();
  }

  public TableCell getCellAt( final int col ) {
    return cells.get( col );
  }

  public boolean isValidationDone() {
    return validationDone;
  }

  public void setValidationDone( final boolean validationDone ) {
    this.validationDone = validationDone;
  }

  public int getRowNumber() {
    return rowNumber;
  }

  public void setRowNumber( final int rowNumber ) {
    this.rowNumber = rowNumber;
  }

  public void replaceCell( final int pos, final RemovedCell cell ) {
    if ( cell == null ) {
      throw new NullPointerException();
    }
    this.cells.set( pos, cell );
  }

  public Object clone() throws CloneNotSupportedException {
    final TableRowInfoStructure o = (TableRowInfoStructure) super.clone();
    o.cells = (ArrayList<TableCell>) cells.clone();
    return o;
  }
}
