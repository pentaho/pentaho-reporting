/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
