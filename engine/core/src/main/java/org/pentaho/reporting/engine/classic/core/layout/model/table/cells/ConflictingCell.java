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

import java.util.ArrayList;

/**
 * A storage item for conflicting cells. Conflicts can only happen between two placeholder cells. The first cell is
 * represented by the conflictingCell instance itself, all additional cells are stored in a list of placeholder cells.
 * <p/>
 * This information can be used to resolve the conflict by inserting extra rows. For now, we simply log the whole stuff
 * and blame the user if things go wrong.
 *
 * @author Thomas Morgner
 */
public class ConflictingCell extends PlaceHolderCell {
  private ArrayList additionalCells;

  public ConflictingCell( final DataCell sourceCell, final int rowSpan, final int colSpan ) {
    super( sourceCell, rowSpan, colSpan );
    additionalCells = new ArrayList();
  }

  public void addConflictingCell( final PlaceHolderCell cell ) {
    additionalCells.add( cell );
  }

  public int getConflictingCellCount() {
    return additionalCells.size();
  }

  public PlaceHolderCell getConflictingCell( final int pos ) {
    return (PlaceHolderCell) additionalCells.get( pos );
  }

  public String toString() {
    return "ConflictingCell{" + "rowSpan=" + getRowSpan() + ", colSpan=" + getColSpan() + ", sourceCell="
        + getSourceCell() + ", additionalCells=" + additionalCells + '}';
  }
}
