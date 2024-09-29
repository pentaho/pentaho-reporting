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
