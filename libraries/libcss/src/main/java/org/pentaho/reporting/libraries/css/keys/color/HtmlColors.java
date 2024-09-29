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


package org.pentaho.reporting.libraries.css.keys.color;

import org.pentaho.reporting.libraries.css.values.CSSColorValue;

/**
 * Contains all colors defined for HTML 4.01
 *
 * @author Thomas Morgner
 */
public final class HtmlColors {
  public static final CSSColorValue BLACK = new CSSColorValue( 0x000000, false );
  public static final CSSColorValue GREEN = new CSSColorValue( 0x008000, false );
  public static final CSSColorValue SILVER = new CSSColorValue( 0xC0C0C0, false );
  public static final CSSColorValue LIME = new CSSColorValue( 0x00FF00, false );
  public static final CSSColorValue GRAY = new CSSColorValue( 0x808080, false );
  public static final CSSColorValue OLIVE = new CSSColorValue( 0x808000, false );
  public static final CSSColorValue WHITE = new CSSColorValue( 0xFFFFFF, false );
  public static final CSSColorValue YELLOW = new CSSColorValue( 0xFFFF00, false );
  public static final CSSColorValue MAROON = new CSSColorValue( 0x800000, false );
  public static final CSSColorValue NAVY = new CSSColorValue( 0x000080, false );
  public static final CSSColorValue RED = new CSSColorValue( 0xFF0000, false );
  public static final CSSColorValue BLUE = new CSSColorValue( 0x0000FF, false );
  public static final CSSColorValue PURPLE = new CSSColorValue( 0x800080, false );
  public static final CSSColorValue TEAL = new CSSColorValue( 0x008080, false );
  public static final CSSColorValue FUCHSIA = new CSSColorValue( 0xFF00FF, false );
  public static final CSSColorValue AQUA = new CSSColorValue( 0x00FFFF, false );

  private HtmlColors() {
  }
}
