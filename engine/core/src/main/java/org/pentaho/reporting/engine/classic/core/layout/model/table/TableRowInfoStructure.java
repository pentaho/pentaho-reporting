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
