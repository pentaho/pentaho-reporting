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


package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.pentaho.reporting.engine.classic.core.util.BulkArrayList;

public class AbstractRowSizeCache {
  private long[] validatedSizes;
  private int validateSizesFillState;

  public AbstractRowSizeCache() {
  }

  private int computeMaxArraySize( long[] array, int rowCount ) {
    if ( array == null ) {
      return rowCount;
    }
    return Math.max( rowCount, array.length + 2000 );
  }

  protected long[] get( BulkArrayList<TableRowImpl> rows ) {
    int rowCount = rows.size();
    if ( this.validatedSizes == null || this.validatedSizes.length < rowCount ) {
      int growth = computeMaxArraySize( this.validatedSizes, rowCount );
      long[] newValidatedSizes = new long[growth];
      if ( this.validatedSizes != null ) {
        System.arraycopy( this.validatedSizes, 0, newValidatedSizes, 0, this.validatedSizes.length );
      }
      this.validatedSizes = newValidatedSizes;
    }

    return validatedSizes;
  }

  public void setFillState( final int validateSizesFillState ) {
    this.validateSizesFillState = validateSizesFillState;
  }

  public int getFillState() {
    return validateSizesFillState;
  }
}
