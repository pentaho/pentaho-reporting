/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.ImmutableDataAttributes;

/**
 * An immutable version of the table-metadata class. This class allows more efficient reuse of shared objects
 * as all contents are guaranteed to be immutable.
 */
public class ImmutableTableMetaData implements TableMetaData {
  private ImmutableDataAttributes tableAttributes;
  private ImmutableDataAttributes[] columnAttributes;

  public ImmutableTableMetaData( final ImmutableDataAttributes tableAttributes,
                                 final ImmutableDataAttributes... columnAttributes ) {
    this.tableAttributes = tableAttributes;
    this.columnAttributes = columnAttributes.clone();
  }

  public DataAttributes getCellDataAttribute( final int row,
                                              final int column ) {
    return EmptyDataAttributes.INSTANCE;
  }

  public boolean isCellDataAttributesSupported() {
    return false;
  }

  public DataAttributes getColumnAttribute( final int column ) {
    return columnAttributes[column];
  }

  public DataAttributes getTableAttribute() {
    return tableAttributes;
  }
}
