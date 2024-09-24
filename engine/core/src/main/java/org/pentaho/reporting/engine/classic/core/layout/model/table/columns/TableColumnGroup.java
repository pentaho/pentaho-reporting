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

package org.pentaho.reporting.engine.classic.core.layout.model.table.columns;

import org.pentaho.reporting.engine.classic.core.layout.model.Border;

import java.util.ArrayList;

/**
 * A table column group contains one or more table columns. The table column group is a normalized element.
 * <p/>
 * A column group may defined a shared background for all columns. The column group may define a minimum width. If the
 * contained cells do not use all of that granted width, they get some extra padding.
 * <p/>
 * As Mozilla does not take the width of a colgroup into account, we will neither.
 *
 * @author Thomas Morgner
 */
public class TableColumnGroup {
  private ArrayList<TableColumn> tableColumns;
  private Border border;
  private boolean freeze;
  private int colSpan;

  public TableColumnGroup( final Border border ) {
    this.border = border;
    this.tableColumns = new ArrayList<TableColumn>();
  }

  public TableColumnGroup() {
    this( Border.EMPTY_BORDER );
  }

  public int getColSpan() {
    return colSpan;
  }

  public void setColSpan( final int colSpan ) {
    this.colSpan = colSpan;
  }

  public void freeze() {
    freeze = true;
  }

  public void addColumn( final TableColumn column ) {
    if ( freeze ) {
      throw new IllegalStateException();
    }
    this.tableColumns.add( column );
  }

  public Border getBorder() {
    return border;
  }

  public int getColumnCount() {
    return tableColumns.size();
  }

  public TableColumn getColumn( final int pos ) {
    return tableColumns.get( pos );
  }
}
