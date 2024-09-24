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

package org.pentaho.reporting.engine.classic.core.imagemap;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class PolygonImageMapEntry extends AbstractImageMapEntry {
  private float[] coordinates;
  private GeneralPath polygon;

  public PolygonImageMapEntry( final float[] coordinates ) {
    if ( coordinates == null ) {
      throw new NullPointerException();
    }
    if ( coordinates.length == 0 ) {
      throw new IllegalArgumentException();
    }
    this.coordinates = coordinates.clone();
  }

  public String getAreaType() {
    return "poly";
  }

  public float[] getAreaCoordinates() {
    return (float[]) coordinates.clone();
  }

  public Shape getShape() {
    if ( polygon == null ) {
      polygon = new GeneralPath();

      for ( int i = 0; i < coordinates.length; i += 2 ) {
        float coordinateX = coordinates[i];
        float coordinateY = coordinates[i + 1];
        if ( i == 0 ) {
          polygon.moveTo( coordinateX, coordinateY );
        } else {
          polygon.lineTo( coordinateX, coordinateY );
        }
      }
      polygon.closePath();
    }
    return polygon;
  }
}
