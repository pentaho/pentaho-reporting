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
