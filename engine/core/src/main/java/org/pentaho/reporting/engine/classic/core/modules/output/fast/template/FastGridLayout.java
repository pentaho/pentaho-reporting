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

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.LongList;
import org.pentaho.reporting.libraries.base.util.GenericObjectTable;

public class FastGridLayout {
  public static class GridCell {
    private CellLayoutInfo layoutInfo;

    private GridCell( final CellLayoutInfo background ) {
      this.layoutInfo = background;
    }

    public CellLayoutInfo getLayoutInfo() {
      return layoutInfo;
    }

    public InstanceID getInstanceId() {
      return null;
    }
  }

  public static class ContentGridCell extends GridCell {
    private final InstanceID instanceId;

    private ContentGridCell( final CellLayoutInfo background, final InstanceID instanceId ) {
      super( background );
      this.instanceId = instanceId;
    }

    public InstanceID getInstanceId() {
      return instanceId;
    }
  }

  private GenericObjectTable<GridCell> cells;
  private HashMap<InstanceID, Point> cellIndex;
  private LongList gridHeights;

  public FastGridLayout() {
    gridHeights = new LongList( 20 );
    cells = new GenericObjectTable<GridCell>();
    cellIndex = new HashMap<InstanceID, Point>();
  }

  public void addRow( final int index, final long rowHeight ) {
    gridHeights.set( index, rowHeight );
  }

  public void addBackground( final CellLayoutInfo layoutInfo ) {
    cells.setObject( layoutInfo.getY1(), layoutInfo.getX1(), new GridCell( layoutInfo ) );
  }

  public void addContent( final InstanceID instanceId, final CellLayoutInfo layoutInfo ) {
    cells.setObject( layoutInfo.getY1(), layoutInfo.getX1(), new ContentGridCell( layoutInfo, instanceId ) );
    cellIndex.put( instanceId, new Point( layoutInfo.getX1(), layoutInfo.getY1() ) );
  }

  public GridCell get( int row, int col ) {
    return cells.getObject( row, col );
  }

  public LongList getCellHeights() {
    return gridHeights;
  }

  public Point getIndex( InstanceID id ) {
    return cellIndex.get( id );
  }

  public int getRowCount() {
    return cells.getRowCount();
  }

  public int getColumnCount() {
    return cells.getColumnCount();
  }

  public List<InstanceID> getOrderedElements() {
    List<InstanceID> list = new ArrayList<InstanceID>();
    for ( int y = 0; y < cells.getRowCount(); y += 1 ) {
      for ( int x = 0; x < cells.getColumnCount(); x += 1 ) {
        GridCell object = cells.getObject( y, x );
        if ( object.getInstanceId() != null ) {
          list.add( object.getInstanceId() );
        }
      }
    }
    return list;
  }
}
