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


package org.pentaho.reporting.engine.classic.core.layout.model.table.columns;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;

import java.util.ArrayList;

/**
 * Creation-Date: 21.07.2006, 19:21:43
 *
 * @author Thomas Morgner
 */
public abstract class AbstractColumnModel implements TableColumnModel {
  private static final Log logger = LogFactory.getLog( AbstractColumnModel.class );
  private boolean validated;

  private ArrayList<TableColumnGroup> columnGroups;
  private TableColumn[] columns;

  public AbstractColumnModel() {
    this.columns = null;
    this.columnGroups = new ArrayList<TableColumnGroup>();
  }

  public void addColumnGroup( final TableColumnGroup column ) {
    columnGroups.add( column );
    column.freeze();
    validated = false;
  }

  public void addAutoColumn() {
    final TableColumnGroup autoGroup = new TableColumnGroup();
    final TableColumn column = new TableColumn( Border.EMPTY_BORDER, RenderLength.AUTO, true );
    autoGroup.addColumn( column );
    autoGroup.freeze();
    columnGroups.add( autoGroup );
    validated = false;
  }

  public boolean isIncrementalModeSupported() {
    return true;
  }

  /**
   * The column count may change over time, when new columnGroups get added.
   *
   * @return
   */
  public int getColumnGroupCount() {
    return columnGroups.size();
  }

  public int getColumnCount() {
    buildColumns();
    return columns.length;
  }

  private void buildColumns() {
    if ( validated ) {
      return;
    }

    final ArrayList<TableColumn> cols = new ArrayList<TableColumn>();
    for ( int i = 0; i < columnGroups.size(); i++ ) {
      final TableColumnGroup node = columnGroups.get( i );

      final int count = node.getColumnCount();
      for ( int x = 0; x < count; x++ ) {
        final TableColumn column = node.getColumn( x );
        cols.add( column );
      }
    }

    columns = cols.toArray( new TableColumn[cols.size()] );
    validated = true;
  }

  public TableColumnGroup getColumnGroup( final int i ) {
    return columnGroups.get( i );
  }

  public TableColumn getColumn( final int i ) {
    buildColumns();
    if ( i >= columns.length ) {
      throw new ArrayIndexOutOfBoundsException( i );
    }
    return columns[i];
  }

  public TableColumn[] getColumns() {
    buildColumns();
    return columns;
  }

  public boolean isValidated() {
    return validated;
  }

  public long getBorderSpacing() {
    return 0;
  }

  public TableColumnGroup getGroupForIndex( final int idx ) {
    int offset = 0;
    for ( int j = 0; j < columnGroups.size(); j++ ) {
      final TableColumnGroup group = columnGroups.get( j );
      if ( offset + group.getColumnCount() <= idx ) {
        offset += group.getColumnCount();
      } else {
        return group;
      }
    }
    throw new IndexOutOfBoundsException( "No such group" );
  }

  public Object clone() throws CloneNotSupportedException {
    final AbstractColumnModel cm = (AbstractColumnModel) super.clone();
    cm.columns = null;
    cm.validated = false;
    cm.columnGroups = (ArrayList<TableColumnGroup>) columnGroups.clone();
    return cm;
  }

  public void clear() {
    columnGroups.clear();
    validated = false;
    columns = null;
  }

  public long getCellPosition( final int columnIndex ) {
    long pos = 0;
    for ( int i = 0; i < columnIndex; i++ ) {
      pos += getColumn( i ).getEffectiveSize();
    }
    return pos;
  }

  public void updateCellSize( final int columnIndex, final int colSpan, final long cachedWidth ) {
    logger.debug( "Update cell-size: " + columnIndex + ": col-span=" + colSpan + "; width=" + cachedWidth );
    getColumn( columnIndex ).setCachedSize( colSpan, cachedWidth );
  }

  public RenderLength getDefinedWidth( final int columnIndex ) {
    return getColumn( columnIndex ).getDefinedWidth();
  }

  public long getEffectiveColumnSize( final int columnIndex ) {
    return getColumn( columnIndex ).getEffectiveSize();
  }
}
