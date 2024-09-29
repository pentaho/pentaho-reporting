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

public abstract class AbstractRowModel implements TableRowModel {
  private BulkArrayList<TableRowImpl> rows;
  private ValidatedRowSizeCache validatedSizesCache;
  private PreferredRowSizeCache preferredSizesCache;

  public AbstractRowModel() {
    this.rows = new BulkArrayList<TableRowImpl>( 2000 );
    this.validatedSizesCache = new ValidatedRowSizeCache();
    this.preferredSizesCache = new PreferredRowSizeCache();
  }

  public void addRow() {
    rows.add( new TableRowImpl() );
  }

  public int getRowCount() {
    return rows.size();
  }

  public TableRow getRow( int i ) {
    return rows.get( i );
  }

  protected TableRowImpl internalGetRow( final int i ) {
    return rows.get( i );
  }

  protected TableRowImpl[] getRows() {
    return rows.toArray( new TableRowImpl[rows.size()] );
  }

  public void prune( final int rows ) {
    if ( rows <= 1 ) {
      return;
    }

    long validatedSize = 0;
    long preferredSize = 0;
    int split = 0;
    int runningMaxRowSpan = 0;
    for ( int r = 0; r < rows; r += 1 ) {
      runningMaxRowSpan -= 1;
      final TableRow row = internalGetRow( r );
      validatedSize += row.getValidateSize();
      preferredSize += row.getPreferredSize();

      runningMaxRowSpan = Math.max( runningMaxRowSpan, row.getMaximumRowSpan() );
      if ( runningMaxRowSpan == 1 ) {
        split = r;
      }
    }

    if ( split == 0 ) {
      return;
    }

    final TableRowImpl newRow = new TableRowImpl();
    newRow.updateDefinedSize( 1, preferredSize );
    newRow.updateValidatedSize( 1, 0, validatedSize );
    newRow.setPreferredSize( preferredSize );
    newRow.setValidateSize( validatedSize );

    this.rows.set( 0, newRow );
    this.rows.removeRange( 1, rows - 1 );
  }

  public long getRowSpacing() {
    return 0;
  }

  public void updateDefinedSize( final int rowNumber, final int rowSpan, final long preferredSize ) {
    internalGetRow( rowNumber ).updateDefinedSize( rowSpan, preferredSize );
  }

  public void updateValidatedSize( final int rowNumber, final int rowSpan, final long leading, final long height ) {
    internalGetRow( rowNumber ).updateValidatedSize( rowSpan, leading, height );
  }

  public long getValidatedRowSize( final int rowNumber ) {
    return internalGetRow( rowNumber ).getValidateSize();
  }

  public long getPreferredRowSize( final int rowNumber ) {
    return internalGetRow( rowNumber ).getPreferredSize();
  }

  public int getMaximumRowSpan( final int rowNumber ) {
    return internalGetRow( rowNumber ).getMaximumRowSpan();
  }

  protected long[] getPreferredSizes( int limit ) {
    return preferredSizesCache.get( limit, rows );
  }

  protected void applyPreferredSizes( long[] preferredSizes, final int start, final int end ) {
    preferredSizesCache.apply( preferredSizes, start, end, rows );
  }

  protected long[] getValidateSizes( final int limit ) {
    return validatedSizesCache.get( limit, rows );
  }

  protected void applyValidateSizes( final long[] trailingSizes, final int start, final int end ) {
    validatedSizesCache.apply( trailingSizes, start, end, rows );
  }

}
