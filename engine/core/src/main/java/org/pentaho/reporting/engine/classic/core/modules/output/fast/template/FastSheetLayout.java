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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.template;

import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SlimSheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;

public class FastSheetLayout implements SlimSheetLayout {
  private SheetLayout sheetLayout;

  public FastSheetLayout( final SheetLayout sheetLayout ) {
    this.sheetLayout = sheetLayout.clone();
    this.sheetLayout.clearVerticalInfo();
  }

  public void reinit( final long rowHeightOffset, final long[] cellHeights ) {
    this.sheetLayout.ensureYMapping( 0, false );
    long h = rowHeightOffset;
    for ( final long height : cellHeights ) {
      h += height;
      this.sheetLayout.ensureYMapping( h, false );
    }
  }

  public long getCellWidth( final int col ) {
    return sheetLayout.getCellWidth( col );
  }

  public long getRowHeight( final int row ) {
    return sheetLayout.getRowHeight( row );
  }

  public long getXPosition( final int col ) {
    return sheetLayout.getXPosition( col );
  }

  public long getYPosition( final int row ) {
    return sheetLayout.getYPosition( row );
  }

  public TableRectangle getTableBounds( final StrictBounds cb, final TableRectangle rectangle ) {
    return sheetLayout.getTableBounds( cb, rectangle );
  }

  public int getColumnCount() {
    return sheetLayout.getColumnCount();
  }

  public long getCellWidth( final int startCell, final int endCell ) {
    return sheetLayout.getCellWidth( startCell, endCell );
  }

  public long getMaxWidth() {
    return sheetLayout.getMaxWidth();
  }

  public StrictBounds getBounds( TableRectangle rectangle ) {
    final long x = getXPosition( rectangle.getX1() );
    final long y = getYPosition( rectangle.getY1() );
    final long width = getCellWidth( rectangle.getX1(), rectangle.getX2() );
    final long height = sheetLayout.getRowHeight( rectangle.getY1(), rectangle.getY2() );
    return new StrictBounds( x, y, width, height );
  }
}
