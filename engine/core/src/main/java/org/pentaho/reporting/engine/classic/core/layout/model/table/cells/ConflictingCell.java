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
