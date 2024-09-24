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
