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

package org.pentaho.reporting.libraries.pixie.wmf;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A Windows metafile logical brush object.
 */
public class MfLogBrush implements WmfObject {
  private static final boolean WHITE = false;
  private static final boolean BLACK = true;

  private static final boolean[] IMG_HS_HORIZONTAL =
    {
      WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
      BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
      BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
      WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
    };

  private static final boolean[] IMG_HS_VERTICAL =
    {
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
    };

  private static final boolean[] IMG_HS_FDIAGONAL =
    {
      BLACK, BLACK, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
      WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, WHITE, WHITE,
      WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE,
      WHITE, WHITE, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE,
      WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, BLACK, BLACK,
      BLACK, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, BLACK,
    };

  private static final boolean[] IMG_HS_BDIAGONAL =
    {
      BLACK, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, BLACK,
      WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, BLACK, BLACK,
      WHITE, WHITE, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE,
      WHITE, WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, WHITE,
      WHITE, BLACK, BLACK, WHITE, WHITE, WHITE, WHITE, WHITE,
      BLACK, BLACK, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
    };

  private static final boolean[] IMG_HS_CROSS =
    {
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
      BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
    };

  private static final boolean[] IMG_HS_DIAGCROSS =
    {
      BLACK, BLACK, WHITE, WHITE, WHITE, WHITE, BLACK, BLACK,
      WHITE, BLACK, BLACK, WHITE, WHITE, BLACK, BLACK, WHITE,
      WHITE, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, WHITE,
      WHITE, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, WHITE,
      WHITE, WHITE, BLACK, BLACK, BLACK, BLACK, WHITE, WHITE,
      WHITE, BLACK, BLACK, WHITE, WHITE, BLACK, BLACK, WHITE,
      BLACK, BLACK, WHITE, WHITE, WHITE, WHITE, BLACK, BLACK,
      BLACK, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, BLACK,
    };

  public static final int COLOR_FULL_ALPHA = 0x00FFFFFF;

  // Brush styles.
  public static final int BS_SOLID = 0;
  public static final int BS_NULL = 1;
  public static final int BS_HATCHED = 2;
  public static final int BS_PATTERN = 3;
  public static final int BS_INDEXED = 4;
  public static final int BS_DIBPATTERN = 5;

  // Hatch styles.
  public static final int HS_HORIZONTAL = 0;
  public static final int HS_VERTICAL = 1;
  public static final int HS_FDIAGONAL = 2;
  public static final int HS_BDIAGONAL = 3;
  public static final int HS_CROSS = 4;
  public static final int HS_DIAGCROSS = 5;

  private int style;
  private Color color;
  private Color bgColor;
  private int hatch;
  private Paint lastPaint;
  private BufferedImage bitmap;

  /**
   * The default brush for a new DC.
   */
  public MfLogBrush() {
    style = BS_SOLID;
    color = Color.white;
    bgColor = new Color( COLOR_FULL_ALPHA );
    hatch = HS_HORIZONTAL;

  }

  public boolean isVisible() {
    return getStyle() != BS_NULL;
  }

  public int getType() {
    return OBJ_BRUSH;
  }

  /**
   * The style of this brush.
   */
  public int getStyle() {
    return style;
  }

  public void setStyle( final int style ) {
    this.style = style;
  }

  /**
   * Return the color of the current brush, or null.
   */
  public Color getColor() {
    return color;
  }

  public void setColor( final Color color ) {
    this.color = color;
    lastPaint = null;
  }

  /**
   * The hatch style of this brush.
   */
  public int getHatchedStyle() {
    return hatch;
  }

  public void setHatchedStyle( final int hstyle ) {
    this.hatch = hstyle;
    lastPaint = null;
  }

  public Paint getPaint() {
    if ( lastPaint != null ) {
      return lastPaint;
    }

    switch( getStyle() ) {
      case BS_SOLID:
        lastPaint = getColor();
        break;
      case BS_NULL:
        lastPaint = new GDIColor( COLOR_FULL_ALPHA );
        break;
      case BS_HATCHED: {
        final BufferedImage image = createHatchStyle();
        lastPaint = new TexturePaint( image, new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) );
        break;
      }
      case BS_DIBPATTERN: {
        if ( bitmap == null ) {
          lastPaint = new GDIColor( COLOR_FULL_ALPHA );
        } else {
          lastPaint = new TexturePaint( bitmap, new Rectangle( 0, 0, bitmap.getWidth(), bitmap.getHeight() ) );
        }
        break;
      }
      default: {
        // Unknown Paint Mode
        lastPaint = new GDIColor( COLOR_FULL_ALPHA );
      }
    }
    return lastPaint;
  }

  private BufferedImage createHatchStyle() {
    final int style = getHatchedStyle();

    final BufferedImage image = new BufferedImage( 8, 8, BufferedImage.TYPE_INT_ARGB );
    switch( style ) {
      case HS_HORIZONTAL:
        image.setRGB( 0, 0, 8, 8, transform( IMG_HS_HORIZONTAL ), 0, 8 );
        break;
      case HS_VERTICAL:
        image.setRGB( 0, 0, 8, 8, transform( IMG_HS_VERTICAL ), 0, 8 );
        break;
      case HS_FDIAGONAL:
        image.setRGB( 0, 0, 8, 8, transform( IMG_HS_FDIAGONAL ), 0, 8 );
        break;
      case HS_BDIAGONAL:
        image.setRGB( 0, 0, 8, 8, transform( IMG_HS_BDIAGONAL ), 0, 8 );
        break;
      case HS_CROSS:
        image.setRGB( 0, 0, 8, 8, transform( IMG_HS_CROSS ), 0, 8 );
        break;
      case HS_DIAGCROSS:
        image.setRGB( 0, 0, 8, 8, transform( IMG_HS_DIAGCROSS ), 0, 8 );
        break;
      default:
        throw new IllegalArgumentException();
    }
    return image;
  }

  public int[] transform( final boolean[] data ) {
    final int color = getColor().getRGB();
    final int bgColor = getBackgroundColor().getRGB();

    final int[] retval = new int[ data.length ];
    for ( int i = 0; i < retval.length; i++ ) {
      if ( data[ i ] == true ) {
        retval[ i ] = color;
      } else {
        retval[ i ] = bgColor;
      }
    }
    return retval;
  }

  public void setBackgroundColor( final Color bg ) {
    this.bgColor = bg;
    lastPaint = null;
  }

  public Color getBackgroundColor() {
    return bgColor;
  }

  public void setBitmap( final BufferedImage bitmap ) {
    this.bitmap = bitmap;
  }


  public BufferedImage getBitmap() {
    return bitmap;
  }
}
