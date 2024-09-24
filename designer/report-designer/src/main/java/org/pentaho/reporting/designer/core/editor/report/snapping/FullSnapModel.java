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
