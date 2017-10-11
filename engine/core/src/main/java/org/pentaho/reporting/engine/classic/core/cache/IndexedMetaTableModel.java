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

package org.pentaho.reporting.engine.classic.core.cache;

import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

public class IndexedMetaTableModel extends IndexedTableModel implements MetaTableModel {
  private MetaTableModel backend;

  public IndexedMetaTableModel( final MetaTableModel backend ) {
    super( backend );
    this.backend = backend;
  }

  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    return backend.getCellDataAttributes( row, indexToColumn( column ) );
  }

  public boolean isCellDataAttributesSupported() {
    return backend.isCellDataAttributesSupported();
  }

  public DataAttributes getColumnAttributes( final int column ) {
    if ( column < backend.getColumnCount() ) {
      return new ColumnIndexDataAttributes( backend.getColumnAttributes( indexToColumn( column ) ), Boolean.FALSE,
          getColumnName( column ), getColumnClass( column ), getColumnName( column ) );
    } else {
      return new ColumnIndexDataAttributes( backend.getColumnAttributes( indexToColumn( column ) ), Boolean.TRUE,
          getColumnName( column ), getColumnClass( column ), getColumnName( column - backend.getColumnCount() ) );
    }
  }

  public DataAttributes getTableAttributes() {
    return backend.getTableAttributes();
  }
}
