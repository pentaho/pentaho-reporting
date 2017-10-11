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

package org.pentaho.reporting.libraries.css.values;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.css.util.ColorUtil;

import java.awt.*;

public class ColorParseTest extends TestCase {
  public ColorParseTest() {
  }

  public ColorParseTest( final String s ) {
    super( s );
  }

  public void testParseNamedColor() {
    assertColor( (CSSColorValue) ColorUtil.parseColor( "black" ), Color.black.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "Black" ), Color.black.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "bLack" ), Color.black.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "blAck" ), Color.black.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "BLACK" ), Color.black.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "bLACK" ), Color.black.getRGB() );
  }

  public void testLongColors() {
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#000000" ), Color.black.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#FF0000" ), Color.red.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#ff0000" ), Color.red.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#00FF00" ), Color.green.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#00ff00" ), Color.green.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#0000FF" ), Color.blue.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#0000ff" ), Color.blue.getRGB() );
  }

  public void testShortColors() {
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#000" ), Color.black.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#f00" ), Color.red.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#F00" ), Color.red.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#0f0" ), Color.green.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#0F0" ), Color.green.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#00f" ), Color.blue.getRGB() );
    assertColor( (CSSColorValue) ColorUtil.parseColor( "#00F" ), Color.blue.getRGB() );
  }

  private void assertColor( final CSSColorValue value, final int rgb ) {
    assertEquals( rgb, value.getRGB() );
  }
}
