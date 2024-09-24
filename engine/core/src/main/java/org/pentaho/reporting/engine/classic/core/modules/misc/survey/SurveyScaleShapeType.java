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

package org.pentaho.reporting.engine.classic.core.modules.misc.survey;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public enum SurveyScaleShapeType {
  DownTriangle, UpTriangle, Diamond, LargeRect, SmallRect, LargeCircle, SmallCircle;

  public Shape getShape() {
    switch ( this ) {
      case DownTriangle:
        return SurveyScale.createDownTriangle( 4.0f );
      case UpTriangle:
        return SurveyScale.createUpTriangle( 4.0f );
      case Diamond:
        return SurveyScale.createDiamond( 4.0f );
      case SmallRect:
        return new Rectangle2D.Double( -3.0, -3.0, 6.0, 6.0 );
      case SmallCircle:
        return new Ellipse2D.Double( -3.0, -3.0, 6.0, 6.0 );
      case LargeRect:
        return new Rectangle2D.Double( -4.0, -4.0, 8.0, 8.0 );
      case LargeCircle:
        return new Ellipse2D.Double( -4.0, -4.0, 8.0, 8.0 );
      default:
        throw new IllegalStateException();
    }
  }
}
