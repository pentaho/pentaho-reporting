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

public class PreferredRowSizeCache extends AbstractRowSizeCache {
  public PreferredRowSizeCache() {
  }

  public long[] get( final int limit, final BulkArrayList<TableRowImpl> rows ) {
    final long[] validatedSizes = get( rows );
    rows.foreach( new BulkArrayList.Func<TableRowImpl>() {
      public void process( final TableRowImpl value, final int index ) {
        validatedSizes[index] = value.getPreferredSize();
      }
    }, getFillState(), limit );
    return validatedSizes;
  }

  public void apply( final long[] trailingSizes, final int start, final int end, BulkArrayList<TableRowImpl> rows ) {
    rows.foreach( new BulkArrayList.Func<TableRowImpl>() {
      public void process( final TableRowImpl row, final int i ) {
        final long validateSize = trailingSizes[i];
        row.setPreferredSize( validateSize );
      }
    }, start, end );

    setFillState( end );
  }

}
