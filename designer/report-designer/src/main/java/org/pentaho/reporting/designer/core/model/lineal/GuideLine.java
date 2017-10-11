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

package org.pentaho.reporting.designer.core.model.lineal;

import java.io.Serializable;

/**
 * User: Martin Date: 26.01.2006 Time: 10:44:44
 */
public class GuideLine implements Serializable {
  private double position;
  private boolean active;

  public GuideLine( final double position, final boolean active ) {
    this.position = position;
    this.active = active;
  }

  public GuideLine updateActive( final boolean active ) {
    return new GuideLine( position, active );
  }

  public double getPosition() {
    return position;
  }

  public boolean isActive() {
    return active;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof GuideLine ) ) {
      return false;
    }

    final GuideLine guideLine = (GuideLine) o;

    if ( Double.compare( guideLine.position, position ) != 0 ) {
      return false;
    }

    return true;
  }

  public String externalize() {
    return "(" + active + ',' + position + ')';
  }

  public int hashCode() {
    final long temp = position != +0.0d ? Double.doubleToLongBits( position ) : 0L;
    return (int) ( temp ^ ( temp >>> 32 ) );
  }
}
