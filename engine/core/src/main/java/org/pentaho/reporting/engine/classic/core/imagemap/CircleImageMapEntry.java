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
import java.awt.geom.Ellipse2D;

public class CircleImageMapEntry extends AbstractImageMapEntry {
  private float radius;
  private float x;
  private float y;

  public CircleImageMapEntry( final float x, final float y, final float radius ) {
    this.x = x;
    this.y = y;
    this.radius = radius;
  }

  public String getAreaType() {
    return "circle";
  }

  public float[] getAreaCoordinates() {
    return new float[] { x, y, radius };
  }

  public Shape getShape() {
    return new Ellipse2D.Float( x, y, 2 * radius, 2 * radius );
  }
}
