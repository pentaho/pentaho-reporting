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

package org.pentaho.reporting.libraries.css.util;

import org.pentaho.reporting.libraries.css.keys.color.CSSSystemColors;
import org.pentaho.reporting.libraries.css.keys.color.HtmlColors;
import org.pentaho.reporting.libraries.css.keys.color.SVGColors;
import org.pentaho.reporting.libraries.css.values.CSSColorValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Creation-Date: 16.04.2006, 15:23:58
 *
 * @author Thomas Morgner
 */
public final class ColorUtil {
  private static final HashMap knownColorNamesByColor;
  private static final HashMap knownColorsByName;

  static {
    knownColorNamesByColor = new HashMap();
    knownColorsByName = new HashMap();
    fillColorsFromClass( SVGColors.class );
    fillColorsFromClass( HtmlColors.class );
    fillColorsFromClass( CSSSystemColors.class );
  }

  private static void fillColorsFromClass( final Class c ) {
    try {
      final Field[] fields = c.getFields();
      for ( int i = 0; i < fields.length; i++ ) {
        final Field f = fields[ i ];
        if ( Modifier.isPublic( f.getModifiers() )
          && Modifier.isFinal( f.getModifiers() )
          && Modifier.isStatic( f.getModifiers() ) ) {
          final String name = f.getName();
          final Object oColor = f.get( null );
          if ( oColor instanceof CSSColorValue ) {
            knownColorNamesByColor.put( oColor, name.toLowerCase() );
            knownColorsByName.put( name.toLowerCase(), oColor );
          }
        }
      }
    } catch ( Exception e ) {
      // ignore ..
    }
  }

  private ColorUtil() {
  }

  private static final float ONE_THIRD = 1f / 3f;

  /*
   * HOW TO RETURN hsl.to.rgb(h, s, l):
       SELECT:
	  l<=0.5: PUT l*(s+1) IN m2
	  ELSE: PUT l+s-l*s IN m2
       PUT l*2-m2 IN m1
       PUT hue.to.rgb(m1, m2, h+1/3) IN r
       PUT hue.to.rgb(m1, m2, h    ) IN g
       PUT hue.to.rgb(m1, m2, h-1/3) IN b
       RETURN (r, g, b)

    HOW TO RETURN hue.to.rgb(m1, m2, h):
       IF h<0: PUT h+1 IN h
       IF h>1: PUT h-1 IN h
       IF h*6<1: RETURN m1+(m2-m1)*h*6
       IF h*2<1: RETURN m2
       IF h*3<2: RETURN m1+(m2-m1)*(2/3-h)*6
       RETURN m1
   */
  public static float[] hslToRGB( int h, float s, float l ) {
    final int hue = normalizeHue( h );

    float saturation = s;
    if ( saturation > 100 ) {
      saturation = 100;
    }
    if ( saturation < 0 ) {
      saturation = 0;
    }
    float lightness = l;
    if ( lightness > 100 ) {
      lightness = 100;
    }
    if ( lightness < 0 ) {
      lightness = 0;
    }
    float m2;
    if ( lightness <= 0.5 ) {
      m2 = lightness * ( saturation + 1 );
    } else {
      m2 = lightness + saturation - lightness * saturation;
    }
    float m1 = lightness * 2 - m2;

    float r = hueToRGB( m1, m2, hue + ONE_THIRD );
    float g = hueToRGB( m1, m2, hue );
    float b = hueToRGB( m1, m2, hue - ONE_THIRD );
    return new float[] { r, g, b };

  }

  private static float hueToRGB( float m1, float m2, float h ) {
    if ( h < 0 ) {
      h = h + 1;
    }
    if ( h > 1 ) {
      h = h - 1;
    }
    if ( ( h * 6f ) < 1 ) {
      return m1 + ( m2 - m1 ) * h * 6;
    }
    if ( ( h * 2f ) < 1 ) {
      return m2;
    }
    if ( ( h * 3f ) < 2 ) {
      return m1 + ( m2 - m1 ) * ( 2 * ONE_THIRD - h ) * 6;
    }
    return m1;
  }

  private static int normalizeHue( final int integerValue ) {
    return ( ( integerValue % 360 ) + 360 ) % 360;
  }

  public static CSSValue parseColor( String colorSpec ) {
    final CSSValue color = parseIdentColor( colorSpec );
    if ( color != null ) {
      return color;
    }
    try {
      if ( colorSpec.length() == 4 ) // #rgb
      {
        final int redColorValue = charToNumber( colorSpec.charAt( 1 ) );
        final int greenColorValue = charToNumber( colorSpec.charAt( 2 ) );
        final int blueColorValue = charToNumber( colorSpec.charAt( 3 ) );
        final int colorValue =
          redColorValue << 20 | redColorValue << 16 |
            greenColorValue << 12 | greenColorValue << 8 |
            blueColorValue << 4 | blueColorValue;
        return new CSSColorValue( colorValue, false );
      }

      final Integer decoded = Integer.decode( colorSpec );
      return new CSSColorValue( decoded.intValue(), false );
    } catch ( Exception e ) {
      return null;
    }
  }

  private static int charToNumber( char character ) {
    switch( character ) {
      case '0':
        return 0;
      case '1':
        return 1;
      case '2':
        return 2;
      case '3':
        return 3;
      case '4':
        return 4;
      case '5':
        return 5;
      case '6':
        return 6;
      case '7':
        return 7;
      case '8':
        return 8;
      case '9':
        return 9;
      case 'A':
        return 10;
      case 'a':
        return 10;
      case 'B':
        return 11;
      case 'b':
        return 11;
      case 'C':
        return 12;
      case 'c':
        return 12;
      case 'D':
        return 13;
      case 'd':
        return 13;
      case 'E':
        return 14;
      case 'e':
        return 14;
      case 'F':
        return 15;
      case 'f':
        return 15;
      default:
        throw new NullPointerException();
    }
  }

  public static CSSValue parseIdentColor( String name ) {
    if ( CSSSystemColors.CURRENT_COLOR.getCSSText().equalsIgnoreCase( name ) ) {
      return CSSSystemColors.CURRENT_COLOR;
    }

    return (CSSValue) knownColorsByName.get( name.toLowerCase() );
  }
}
