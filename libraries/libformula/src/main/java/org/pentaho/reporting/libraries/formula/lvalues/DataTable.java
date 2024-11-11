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


package org.pentaho.reporting.libraries.formula.lvalues;

/**
 * A database is a two dimensional collection of data, arranged in a table. Although we do not assume that the whole
 * database is held in memory, we allow random access of the data.
 * <p/>
 * Columns may have names, but there is no enforced requirement for that.
 * <p/>
 * As a database is not just a collection of raw data, this interface returns LValues instead of plain objects. Columns
 * may be computed values using formulas (the exact semantics of adressing database cells in a formula is beyond the
 * scope of this specification and is implementation specific).
 *
 * @author Thomas Morgner
 */
public interface DataTable extends LValue {
  public int getRowCount();

  public int getColumnCount();

  public String getColumnName( int column );

  public LValue getValueAt( int row, int column );
}
