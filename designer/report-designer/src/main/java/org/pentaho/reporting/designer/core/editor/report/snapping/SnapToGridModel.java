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
