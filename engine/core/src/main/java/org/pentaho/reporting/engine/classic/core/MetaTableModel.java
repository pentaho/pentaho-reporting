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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

import javax.swing.table.TableModel;

/**
 * A extension of the classic table-model. If the tablemodel returned by the data-factory implements this interface, the
 * report engine can make use of the extended meta-data provided for each column.
 * <p/>
 * The meta-data given here is presentation level meta-data, so it describes how data from the data-set should be
 * formatted or styled.
 *
 * @author Thomas Morgner
 */
public interface MetaTableModel extends TableModel {
  /**
   * Returns the meta-attribute as Java-Object. The object type that is expected by the caller is defined in the
   * TableMetaData property set. It is the responsibility of the implementor to map the native meta-data model into a
   * model suitable for reporting.
   * <p/>
   * Be aware that cell-level attributes do not make it into the designtime dataschema, as this dataschema only looks at
   * the structural metadata available and does not contain any data references.
   *
   * @param row
   *          the row of the cell for which the meta-data is queried.
   * @param column
   *          the index of the column for which the meta-data is queried.
   * @return the meta-data object.
   */
  public DataAttributes getCellDataAttributes( final int row, final int column );

  /**
   * Checks, whether cell-data attributes are supported by this tablemodel implementation.
   *
   * @return true, if the model supports cell-level attributes, false otherwise.
   */
  public boolean isCellDataAttributesSupported();

  /**
   * Returns the column-level attributes for the given column.
   *
   * @param column
   *          the column.
   * @return data-attributes, never null.
   */
  public DataAttributes getColumnAttributes( final int column );

  /**
   * Returns table-wide attributes. This usually contain hints about the data-source used to query the data as well as
   * hints on the sort-order of the data.
   *
   * @return the table-attributes, never null.
   */
  public DataAttributes getTableAttributes();
}
