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
