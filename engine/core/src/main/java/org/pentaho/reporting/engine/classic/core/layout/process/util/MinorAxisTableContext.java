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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnModel;

public class MinorAxisTableContext {
  private TableRenderBox table;
  private MinorAxisTableContext context;
  private boolean secondPassNeeded;
  private long cellPosition;

  public MinorAxisTableContext( final TableRenderBox table, final MinorAxisTableContext context ) {
    this.table = table;
    this.context = context;
    this.secondPassNeeded = true;
  }

  public TableRenderBox getTable() {
    return table;
  }

  public TableColumnModel getColumnModel() {
    return table.getColumnModel();
  }

  public void setStructureValidated( final boolean structureValidated ) {
    table.setStructureValidated( structureValidated );
  }

  public long getCellPosition() {
    return cellPosition;
  }

  public void setCellPosition( final long cellPosition ) {
    this.cellPosition = cellPosition;
  }

  public boolean isStructureValidated() {
    return table.isStructureValidated();
  }

  public boolean isSecondPassNeeded() {
    return secondPassNeeded;
  }

  public void setSecondPassNeeded( final boolean secondPassNeeded ) {
    this.secondPassNeeded = secondPassNeeded;
  }

  public MinorAxisTableContext pop() {
    return context;
  }

  public void startRow() {
    cellPosition = 0;
  }
}
