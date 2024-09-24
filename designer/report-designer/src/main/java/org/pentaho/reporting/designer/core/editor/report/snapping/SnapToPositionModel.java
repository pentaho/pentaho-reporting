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

import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class SnapToPositionModel implements SnapPositionsModel {
  private BreakPositionsList positionList;

  public SnapToPositionModel() {
    this.positionList = new BreakPositionsList( 20, true );
  }

  public boolean add( final long key, final InstanceID owner ) {
    return positionList.add( key, owner );
  }

  public void clear() {
    positionList.clear();
  }

  public long[] getKeys() {
    return positionList.getKeys();
  }

  public InstanceID getOwner( final long key ) {
    return positionList.getOwner( key );
  }

  public int size() {
    return positionList.size();
  }

  /**
   * Computes the nearest snap-point.
   *
   * @param position
   * @return
   */
  public long getNearestSnapPosition( final long position,
                                      final InstanceID owner ) {
    final long next = positionList.getNext( position );
    final long prev = positionList.getPrevious( position );

    if ( owner == null ) {
      if ( Math.abs( next - position ) > Math.abs( prev - position ) ) {
        return prev;
      } else {
        return next;
      }
    }

    final InstanceID ownerNext = positionList.getOwner( next );
    final InstanceID ownerPrev = positionList.getOwner( prev );
    if ( ownerNext == owner && ownerPrev == owner ) {
      return position;
    }
    if ( ownerPrev == owner ) {
      return next;
    }
    if ( ownerNext == owner ) {
      return prev;
    }
    if ( Math.abs( next - position ) > Math.abs( prev - position ) ) {
      return prev;
    } else {
      return next;
    }
  }

}
