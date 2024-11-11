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
