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
