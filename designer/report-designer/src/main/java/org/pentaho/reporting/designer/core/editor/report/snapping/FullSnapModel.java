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
public class FullSnapModel implements SnapPositionsModel {
  private SnapToGridModel gridModel;
  private SnapToPositionModel elementModel;
  private SnapToPositionModel guidesModel;

  private boolean enableGrid;
  private boolean enableGuides;
  private boolean enableElements;

  public FullSnapModel() {
    gridModel = new SnapToGridModel();

    elementModel = new SnapToPositionModel();
    guidesModel = new SnapToPositionModel();
  }

  public SnapToGridModel getGridModel() {
    return gridModel;
  }

  public SnapToPositionModel getElementModel() {
    return elementModel;
  }

  public SnapToPositionModel getGuidesModel() {
    return guidesModel;
  }

  public boolean isEnableGrid() {
    return enableGrid;
  }

  public void setEnableGrid( final boolean enableGrid ) {
    this.enableGrid = enableGrid;
  }

  public boolean isEnableGuides() {
    return enableGuides;
  }

  public void setEnableGuides( final boolean enableGuides ) {
    this.enableGuides = enableGuides;
  }

  public boolean isEnableElements() {
    return enableElements;
  }

  public void setEnableElements( final boolean enableElements ) {
    this.enableElements = enableElements;
  }

  /**
   * Computes the nearest snap-point.
   *
   * @param position
   * @return
   */
  public long getNearestSnapPosition( final long position,
                                      final InstanceID owner ) {
    long retval = position;
    long delta = Long.MAX_VALUE;

    if ( isEnableGrid() ) {
      final long snapPos = gridModel.getNearestSnapPosition( position, owner );
      final long newDelta = Math.abs( position - snapPos );
      if ( newDelta != 0 && newDelta < delta ) {
        retval = snapPos;
        delta = newDelta;
      }
    }
    if ( isEnableElements() ) {
      final long snapPos = elementModel.getNearestSnapPosition( position, owner );
      final long newDelta = Math.abs( position - snapPos );
      if ( newDelta != 0 && newDelta < delta ) {
        retval = snapPos;
        delta = newDelta;
      }
    }
    if ( isEnableGuides() ) {
      final long snapPos = guidesModel.getNearestSnapPosition( position, owner );
      final long newDelta = Math.abs( position - snapPos );
      if ( newDelta != 0 && newDelta < delta ) {
        retval = snapPos;
      }
    }
    return retval;
  }
}
