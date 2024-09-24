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
import java.awt.geom.Rectangle2D;

public class RectangleImageMapEntry extends AbstractImageMapEntry {
  private float x1;
  private float y1;
  private float x2;
  private float y2;

  public RectangleImageMapEntry( final float x1, final float y1, final float x2, final float y2 ) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  public String getAreaType() {
    return "rect";
  }

  public float[] getAreaCoordinates() {
    return new float[] { x1, y1, x2, y2 };
  }

  public Shape getShape() {
    return new Rectangle2D.Double( x1, y1, x2 - x1, y2 - y1 );
  }

  public boolean contains( final float x, final float y ) {
    return x1 >= x && x2 <= x && y1 >= y && y2 <= y;
  }
}
