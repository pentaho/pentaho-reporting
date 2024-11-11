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


package org.pentaho.reporting.designer.core.editor.report.snapping;

import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class SnapToGridModel implements SnapPositionsModel {
  private long gridSize;

  public SnapToGridModel() {
  }

  public long getGridSize() {
    return gridSize;
  }

  public void setGridSize( final long gridSize ) {
    this.gridSize = gridSize;
  }

  /**
   * Computes the nearest snap-point.
   *
   * @param position
   * @return
   */
  public long getNearestSnapPosition( final long position,
                                      final InstanceID owner ) {
    if ( gridSize < 2 ) {
      return position;
    }
    if ( position <= 0 ) {
      return position;
    }

    final long div1 = position / gridSize;
    final long snapPos1 = div1 * gridSize;
    final long div2 = ( position + gridSize ) / gridSize;
    final long snapPos2 = div2 * gridSize;
    if ( Math.abs( snapPos1 - position ) > Math.abs( snapPos2 - position ) ) {
      return snapPos2;
    } else {
      return snapPos1;
    }
  }
}
