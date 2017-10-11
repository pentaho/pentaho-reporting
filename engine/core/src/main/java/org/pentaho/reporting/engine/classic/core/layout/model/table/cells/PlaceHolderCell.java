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

package org.pentaho.reporting.engine.classic.core.layout.model.table.cells;

/**
 * Creation-Date: 10.09.2006, 17:27:54
 *
 * @author Thomas Morgner
 */
public class PlaceHolderCell extends TableCell {
  private DataCell sourceCell;

  public PlaceHolderCell( final DataCell sourceCell, final int rowSpan, final int colSpan ) {
    super( rowSpan, colSpan );
    if ( sourceCell == null ) {
      throw new NullPointerException();
    }
    this.sourceCell = sourceCell;
  }

  public DataCell getSourceCell() {
    return sourceCell;
  }

  public String toString() {
    return "PlaceHolderCell{" + "rowSpan=" + getRowSpan() + ", colSpan=" + getColSpan() + ", sourceCell=" + sourceCell
        + '}';
  }
}
