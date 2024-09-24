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

package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * The table row model is responsible for calculating cell heights and for distributing extra space for cells and rows.
 * It also deals with gathering evidence for cell sizes of row-spanned cells.
 *
 * @author Thomas Morgner
 */
public interface TableRowModel {
  public void addRow();

  public int getRowCount();

  public void validatePreferredSizes();

  public void validateActualSizes();

  public void initialize( TableRenderBox table );

  public void prune( int rows );

  public void clear();

  void updateDefinedSize( int rowNumber, int rowSpan, long preferredSize );

  void updateValidatedSize( int rowNumber, int rowSpan, long leading, long height );

  long getValidatedRowSize( int rowNumber );

  long getPreferredRowSize( int rowNumber );

  public int getMaximumRowSpan( int rowNumber );

  void setDebugInformation( ElementType elementType, InstanceID instanceID );
}
