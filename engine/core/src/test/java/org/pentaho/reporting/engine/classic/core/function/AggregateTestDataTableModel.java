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

package org.pentaho.reporting.engine.classic.core.function;

import javax.swing.table.AbstractTableModel;

/**
 * A sample data source for the JFreeReport Demo Application.
 *
 * @author David Gilbert
 */
public class AggregateTestDataTableModel extends AbstractTableModel {

  /**
   * Storage for the data.
   */
  private final Object[][] data;

  /**
   * Default constructor - builds the sample data source using incomplete (and possibly inaccurate) data for countries
   * of the world.
   */
  public AggregateTestDataTableModel() {
    data = new Object[23][4];
    data[0] = new Object[] { "Morocco", "MA", "Africa", new Integer( 29114497 ) };
    data[1] = new Object[] { "South Africa", "ZA", "Africa", new Integer( 40583573 ) };
    data[2] = new Object[] { "China", "CN", "Asia", new Integer( 1254400000 ) };
    data[3] = new Object[] { "Iran", "IR", "Asia", new Integer( 66000000 ) };
    data[4] = new Object[] { "Iraq", "IQ", "Asia", new Integer( 19700000 ) };
    data[5] = new Object[] { "Australia", "AU", "Australia", new Integer( 18751000 ) };
    data[6] = new Object[] { "Austria", "AT", "Europe", new Integer( 8015000 ) };
    data[7] = new Object[] { "Belgium", "BE", "Europe", new Integer( 10213752 ) };
    data[8] = new Object[] { "Estonia", "EE", "Europe", new Integer( 1445580 ) };
    data[9] = new Object[] { "Finland", "FI", "Europe", new Integer( 5171000 ) };
    data[10] = new Object[] { "France", "FR", "Europe", new Integer( 60186184 ) };
    data[11] = new Object[] { "Germany", "DE", "Europe", new Integer( 82037000 ) };
    data[12] = new Object[] { "Hungary", "HU", "Europe", new Integer( 10044000 ) };
    data[13] = new Object[] { "Italy", "IT", "Europe", new Integer( 57612615 ) };
    data[14] = new Object[] { "Norway", "NO", "Europe", new Integer( 4445460 ) };
    data[15] = new Object[] { "Poland", "PL", "Europe", new Integer( 38608929 ) };
    data[16] = new Object[] { "Portugal", "PT", "Europe", new Integer( 9918040 ) };
    data[17] = new Object[] { "Spain", "ES", "Europe", new Integer( 39669394 ) };
    data[18] = new Object[] { "Sweden", "SE", "Europe", new Integer( 8854322 ) };
    data[19] = new Object[] { "Switzerland", "CH", "Europe", new Integer( 7123500 ) };
    data[20] = new Object[] { "Canada", "CA", "North America", new Integer( 30491300 ) };
    data[21] = new Object[] { "United States of America", "US", "North America", new Integer( 273866000 ) };
    data[22] = new Object[] { "Brazil", "BR", "South America", new Integer( 165715400 ) };
  }

  /**
   * Returns the number of rows in the table model.
   *
   * @return the row count.
   */
  public int getRowCount() {
    return data.length;
  }

  /**
   * Returns the number of columns in the table model.
   *
   * @return the column count.
   */
  public int getColumnCount() {
    return 4;
  }

  /**
   * Returns the class of the data in the specified column.
   *
   * @param column
   *          the column (zero-based index).
   * @return the column class.
   */
  public Class getColumnClass( final int column ) {
    if ( column == 3 ) {
      return Integer.class;
    } else {
      return String.class;
    }
  }

  /**
   * Returns the name of the specified column.
   *
   * @param column
   *          the column (zero-based index).
   * @return the column name.
   */
  public String getColumnName( final int column ) {
    if ( column == 0 ) {
      return "Country";
    } else if ( column == 1 ) {
      return "ISO Code";
    } else if ( column == 2 ) {
      return "Continent";
    } else if ( column == 3 ) {
      return "Population";
    } else {
      return null;
    }
  }

  /**
   * Returns the data value at the specified row and column.
   *
   * @param row
   *          the row index (zero based).
   * @param column
   *          the column index (zero based).
   * @return the value.
   */
  public Object getValueAt( final int row, final int column ) {
    return data[row][column];
  }

}
