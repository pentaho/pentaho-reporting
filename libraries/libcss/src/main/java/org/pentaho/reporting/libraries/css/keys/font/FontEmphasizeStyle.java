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


package org.pentaho.reporting.libraries.css.keys.font;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

public class FontEmphasizeStyle {
  public static final CSSConstant NONE =
    new CSSConstant( "none" );
  public static final CSSConstant ACCENT =
    new CSSConstant( "accent" );
  public static final CSSConstant DOT =
    new CSSConstant( "dot" );
  public static final CSSConstant CIRCLE =
    new CSSConstant( "circle" );
  public static final CSSConstant DISC =
    new CSSConstant( "disc" );

  private FontEmphasizeStyle() {
  }
}
