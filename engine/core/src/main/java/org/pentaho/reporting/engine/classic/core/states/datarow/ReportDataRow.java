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


package org.pentaho.reporting.engine.classic.core.states.datarow;

import javax.swing.table.TableModel;

public final class ReportDataRow {
  private String[] names;
  private final TableModel reportData;
  private int cursor;

  public ReportDataRow( final TableModel reportData ) {
    if ( reportData == null ) {
      throw new NullPointerException();
    }
    this.reportData = reportData;
    this.cursor = 0;

    final int columnCount = reportData.getColumnCount();
    this.names = new String[columnCount];

    for ( int i = 0; i < columnCount; i++ ) {
      this.names[i] = reportData.getColumnName( i );
    }
  }

  private ReportDataRow( final TableModel reportData, final ReportDataRow reportDataRow ) {
    if ( reportData == null ) {
      throw new NullPointerException();
    }

    if ( reportDataRow == null ) {
      throw new NullPointerException();
    }

    this.reportData = reportData;
    this.cursor = 0;

    this.cursor = reportDataRow.cursor + 1;
    this.names = reportDataRow.names;
  }

  /**
   * Returns the value of the expression or column in the tablemodel using the given column number as index. For
   * functions and expressions, the <code>getValue()</code> method is called and for columns from the tablemodel the
   * tablemodel method <code>getValueAt(row, column)</code> gets called.
   *
   * @param col
   *          the item index.
   * @return the value.
   * @throws IllegalStateException
   *           if the datarow detected a deadlock.
   */
  public Object get( final int col ) {
    return reportData.getValueAt( cursor, col );
  }

  /**
   * Returns the name of the column, expression or function. For columns from the tablemodel, the tablemodels
   * <code>getColumnName</code> method is called. For functions, expressions and report properties the assigned name is
   * returned.
   *
   * @param col
   *          the item index.
   * @return the name.
   */
  public String getColumnName( final int col ) {
    return names[col];
  }

  /**
   * Returns the number of columns, expressions and functions and marked ReportProperties in the report.
   *
   * @return the item count.
   */
  public int getColumnCount() {
    return names.length;
  }

  /**
   * Advances to the next row and attaches the given master row to the objects contained in that client data row.
   *
   * @return
   */
  public ReportDataRow advance() {
    return new ReportDataRow( reportData, this );
  }

  public boolean isAdvanceable() {
    return cursor < ( reportData.getRowCount() - 1 );
  }

  public boolean isReadable() {
    return cursor >= 0 && cursor < reportData.getRowCount();
  }

  public TableModel getReportData() {
    return reportData;
  }

  public int getCursor() {
    return cursor;
  }
}
