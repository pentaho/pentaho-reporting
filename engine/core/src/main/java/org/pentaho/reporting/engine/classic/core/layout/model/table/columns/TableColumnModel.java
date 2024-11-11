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


package org.pentaho.reporting.engine.classic.core.layout.model.table.columns;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;

/**
 * Creation-Date: 21.07.2006, 19:20:44
 *
 * @author Thomas Morgner
 */
public interface TableColumnModel extends Cloneable {
  public void addColumnGroup( TableColumnGroup column );

  public void addAutoColumn();

  public boolean isIncrementalModeSupported();

  // public int getColumnGroupCount();

  public int getColumnCount();

  public void validateSizes( TableRenderBox tableRenderBox );

  // public long getPreferredSize();

  // public long getMinimumChunkSize();

  // public TableColumnGroup getColumnGroup(int i);

  // public TableColumn getColumn(int i);

  public long getBorderSpacing();

  // public TableColumnGroup getGroupForIndex(final int i);

  public Object clone() throws CloneNotSupportedException;

  public void clear();

  public long getCellPosition( int columnIndex );

  public void updateCellSize( int columnIndex, int colSpan, long cachedWidth );

  public long getCachedSize();

  RenderLength getDefinedWidth( int columnIndex );

  long getEffectiveColumnSize( int columnIndex );
}
