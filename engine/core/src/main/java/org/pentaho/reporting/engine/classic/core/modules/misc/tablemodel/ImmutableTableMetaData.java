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
