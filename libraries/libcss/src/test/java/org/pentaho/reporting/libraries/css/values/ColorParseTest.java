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
