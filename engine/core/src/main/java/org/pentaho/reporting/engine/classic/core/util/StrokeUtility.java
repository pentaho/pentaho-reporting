/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.Arrays;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * This class provides helper methods to work with Strokes and line-styles.
 *
 * @author Thomas Morgner
 */
public class StrokeUtility {
  /**
   * A constant defining a stroke-type.
   */
  public static final int STROKE_SOLID = 0;
  /**
   * A constant defining a stroke-type.
   */
  public static final int STROKE_DASHED = 1;
  /**
   * A constant defining a stroke-type.
   */
  public static final int STROKE_DOTTED = 2;
  /**
   * A constant defining a stroke-type.
   */
  public static final int STROKE_DOT_DASH = 3;
  /**
   * A constant defining a stroke-type.
   */
  public static final int STROKE_DOT_DOT_DASH = 4;
  /**
   * A constant defining a stroke-type.
   */
  public static final int STROKE_NONE = 5;

  /**
   * Default Constructor. Private to prevent Object-creation.
   */
  private StrokeUtility() {
  }

  /**
   * Creates a new Stroke-Object for the given type and with.
   *
   * @param type
   *          the stroke-type. (Must be one of the constants defined in this class.)
   * @param width
   *          the stroke's width.
   * @return the stroke, never null.
   */
  public static Stroke createStroke( final int type, final float width ) {
    final Configuration repoConf = ClassicEngineBoot.getInstance().getGlobalConfig();
    final boolean useWidthForStrokes =
        "true".equals( repoConf.getConfigProperty( "org.pentaho.reporting.engine.classic.core.DynamicStrokeDashes" ) );

    final float effectiveWidth;
    if ( useWidthForStrokes ) {
      effectiveWidth = width;
    } else {
      effectiveWidth = 1;
    }

    switch ( type ) {
      case STROKE_DASHED:
        return new BasicStroke( width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {
          6 * effectiveWidth, 6 * effectiveWidth }, 0.0f );
      case STROKE_DOTTED:
        return new BasicStroke( width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 5.0f, new float[] { 0.0f,
          2 * effectiveWidth }, 0.0f );
      case STROKE_DOT_DASH:
        return new BasicStroke( width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 0,
          2 * effectiveWidth, 6 * effectiveWidth, 2 * effectiveWidth }, 0.0f );
      case STROKE_DOT_DOT_DASH:
        return new BasicStroke( width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 0,
          2 * effectiveWidth, 0, 2 * effectiveWidth, 6 * effectiveWidth, 2 * effectiveWidth }, 0.0f );
      default:
        return new BasicStroke( width );
    }
  }

  /**
   * Tries to extract the stroke-width from the given stroke object.
   *
   * @param s
   *          the stroke.
   * @return the stroke's width.
   */
  public static float getStrokeWidth( final Stroke s ) {
    if ( s instanceof BasicStroke ) {
      final BasicStroke bs = (BasicStroke) s;
      return bs.getLineWidth();
    }
    return 1;
  }

  /**
   * Tries to deduct the stroke-type from the given stroke object. This will result in funny return values if the stroke
   * was not created by the {@link #createStroke(int, float)} method.
   *
   * @param s
   *          the stroke.
   * @return the stroke's width.
   */
  public static int getStrokeType( final Stroke s ) {
    if ( s instanceof BasicStroke == false ) {
      return STROKE_SOLID;
    }
    final BasicStroke bs = (BasicStroke) s;
    if ( bs.getLineWidth() <= 0 ) {
      return STROKE_NONE;
    }

    final float[] dashes = bs.getDashArray();
    if ( dashes == null ) {
      return STROKE_SOLID;
    }
    if ( dashes.length < 2 ) {
      return STROKE_SOLID;
    }
    if ( dashes.length == 3 || dashes.length == 5 ) {
      return STROKE_SOLID;
    }

    if ( dashes.length == 2 ) {
      // maybe dashed or dotted ...
      // if (dashes[0] < 2 && dashes[1] < 2) {
      // return STROKE_DOTTED;
      // }
      final float factor = dashes[0] / dashes[1];
      if ( factor > 0.9 && factor < 1.1 ) {
        return STROKE_DASHED;
      } else if ( factor < 0.1 ) {
        return STROKE_DOTTED;
      }

      // else ... not recognized ...
      return STROKE_SOLID;
    } else if ( dashes.length == 4 ) {
      // maybe a dot-dashed stroke ...
      final float[] copyDashes = (float[]) dashes.clone();
      Arrays.sort( copyDashes );

      // the first value should be near zero ..
      if ( Math.abs( copyDashes[0] / bs.getLineWidth() ) > 0.5 ) {
        // not recognized ..
        return STROKE_SOLID;
      }

      // test that the first two values have the same size
      final float factor1 = ( 2 * bs.getLineWidth() ) / copyDashes[1];
      final float factor2 = ( 2 * bs.getLineWidth() ) / copyDashes[2];
      final float factorBig = ( 2 * bs.getLineWidth() ) / copyDashes[3];

      if ( ( factor1 < 0.9 || factor1 > 1.1 ) || ( factor2 < 0.9 || factor2 > 1.1 ) ) {
        // not recognized ...
        return STROKE_SOLID;
      }

      if ( factorBig < 0.4 || factorBig > 2.5 ) {
        return STROKE_DOT_DASH;
      }
      if ( factorBig < 0.9 || factorBig > 1.1 ) {
        return STROKE_DOTTED;
      }
      return STROKE_DASHED;
    } else if ( dashes.length == 6 ) {
      // maybe a dot-dashed stroke ...
      final float[] copyDashes = (float[]) dashes.clone();
      Arrays.sort( copyDashes );
      // test that the first three values have the same size

      // the first two values should be near zero ..
      if ( Math.abs( copyDashes[0] / bs.getLineWidth() ) > 0.5 ) {
        // not recognized ..
        return STROKE_SOLID;
      }
      if ( Math.abs( copyDashes[1] / bs.getLineWidth() ) > 0.5 ) {
        // not recognized ..
        return STROKE_SOLID;
      }

      final float factor2 = ( 2 * bs.getLineWidth() ) / copyDashes[2];
      final float factor3 = ( 2 * bs.getLineWidth() ) / copyDashes[3];
      final float factor4 = ( 2 * bs.getLineWidth() ) / copyDashes[4];
      final float factorBig = ( 2 * bs.getLineWidth() ) / copyDashes[5];

      if ( ( factor2 < 0.9 || factor2 > 1.1 ) || ( factor3 < 0.9 || factor3 > 1.1 )
          || ( factor4 < 0.9 || factor4 > 1.1 ) ) {
        return STROKE_SOLID;
      }

      if ( factorBig < 0.4 || factorBig > 2.5 ) {
        return STROKE_DOT_DOT_DASH;
      }
      if ( ( factorBig < 0.9 || factorBig > 1.1 ) ) {
        return STROKE_DOTTED;
      }
      return STROKE_DASHED;
    }
    // not recognized ...
    return STROKE_SOLID;
  }

  public static BorderStyle translateStrokeStyle( final Stroke s ) {
    final int style = StrokeUtility.getStrokeType( s );
    switch ( style ) {
      case StrokeUtility.STROKE_NONE:
        return BorderStyle.NONE;
      case StrokeUtility.STROKE_DASHED:
        return BorderStyle.DASHED;
      case StrokeUtility.STROKE_DOTTED:
        return BorderStyle.DOTTED;
      case StrokeUtility.STROKE_DOT_DASH:
        return BorderStyle.DOT_DASH;
      case StrokeUtility.STROKE_DOT_DOT_DASH:
        return BorderStyle.DOT_DOT_DASH;
      default:
        return BorderStyle.SOLID;
    }
  }
}
