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

import java.util.ArrayList;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CompoundSnapModel implements SnapPositionsModel {
  private ArrayList<SnapPositionsModel> snapModels;
  private transient SnapPositionsModel[] snapModelsAsArray;

  public CompoundSnapModel() {
    snapModels = new ArrayList<SnapPositionsModel>();
  }

  public void add( final SnapPositionsModel model ) {
    if ( model == null ) {
      throw new NullPointerException();
    }
    snapModels.add( model );
  }

  public void remove( final SnapPositionsModel model ) {
    if ( model == null ) {
      throw new NullPointerException();
    }
    snapModels.remove( model );
  }

  /**
   * Computes the nearest snap-point.
   *
   * @param position
   * @return
   */
  public long getNearestSnapPosition( final long position,
                                      final InstanceID owner ) {
    if ( snapModelsAsArray == null ) {
      snapModelsAsArray = snapModels.toArray( new SnapPositionsModel[ snapModels.size() ] );
    }

    long retval = position;
    long delta = Long.MAX_VALUE;

    for ( int i = 0; i < snapModelsAsArray.length; i++ ) {
      final SnapPositionsModel positionsModel = snapModelsAsArray[ i ];
      final long snapPos = positionsModel.getNearestSnapPosition( position, owner );
      final long newDelta = Math.abs( position - snapPos );
      if ( newDelta < delta ) {
        retval = snapPos;
        delta = newDelta;
      }
    }

    return retval;
  }
}
