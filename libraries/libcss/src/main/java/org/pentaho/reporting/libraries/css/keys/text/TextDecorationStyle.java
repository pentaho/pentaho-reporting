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

package org.pentaho.reporting.libraries.css.keys.text;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Used for underline, strike-through and overline.
 *
 * @author Thomas Morgner
 */
public class TextDecorationStyle {
  // none | solid | double | dotted | dashed | dot-dash | dot-dot-dash | wave
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant SOLID = new CSSConstant( "solid" );
  public static final CSSConstant DOUBLE = new CSSConstant( "double" );
  public static final CSSConstant DOTTED = new CSSConstant( "dotted" );
  public static final CSSConstant DASHED = new CSSConstant( "dashed" );
  public static final CSSConstant DOT_DASH = new CSSConstant( "dot-dash" );
  public static final CSSConstant DOT_DOT_DASH = new CSSConstant( "dot-dot-dash" );
  public static final CSSConstant WAVE = new CSSConstant( "wave" );
  // This is an open-office addition ...
  public static final CSSConstant LONG_DASH = new CSSConstant( "-x-pentaho-css-long-dash" );

  private TextDecorationStyle() {
  }
}
