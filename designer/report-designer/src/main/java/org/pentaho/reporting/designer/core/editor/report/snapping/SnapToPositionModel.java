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
