/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;

public interface SlimSheetLayout {
  long getCellWidth( int col );

  long getRowHeight( int row );

  long getXPosition( int col );

  long getYPosition( int row );

  TableRectangle getTableBounds( StrictBounds cb, TableRectangle rectangle );

  int getColumnCount();

  long getCellWidth( int startCell, int endCell );

  long getMaxWidth();
}
