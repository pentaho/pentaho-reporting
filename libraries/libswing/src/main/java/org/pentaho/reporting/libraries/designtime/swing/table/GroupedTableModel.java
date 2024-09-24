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
package org.pentaho.reporting.libraries.designtime.swing.table;

import javax.swing.event.TableModelEvent;

public class GroupedTableModel extends RowMapperTableModel {
  private GroupingModel parent;

  public GroupedTableModel( final GroupingModel parent ) {
    super( parent );
    this.parent = parent;
    recomputeRowCount();
  }

  public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
    final int index = mapToModel( rowIndex );
    if ( parent.isHeaderRow( index ) ) {
      return true;
    }

    return parent.isCellEditable( index, columnIndex );
  }

  public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
    final int index = mapToModel( rowIndex );
    parent.setValueAt( aValue, index, columnIndex );
    if ( parent.isHeaderRow( index ) ) {
      recomputeRowCount();
      fireTableModelEvent( new TableModelEvent( this ) );
    }
  }

  protected boolean isFiltered( final int row ) {
    final GroupingHeader groupHeader = parent.getGroupHeader( row );
    return groupHeader != null && groupHeader.isCollapsed() && parent.isHeaderRow( row ) == false;
  }
}
