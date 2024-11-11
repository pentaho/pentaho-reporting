/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.css.keys.border;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 30.10.2005, 19:37:35
 *
 * @author Thomas Morgner
 */
public class BorderStyle {
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant HIDDEN = new CSSConstant( "hidden" );
  public static final CSSConstant DOTTED = new CSSConstant( "dotted" );
  public static final CSSConstant DASHED = new CSSConstant( "dashed" );
  public static final CSSConstant SOLID = new CSSConstant( "solid" );
  public static final CSSConstant DOUBLE = new CSSConstant( "double" );
  public static final CSSConstant DOT_DASH = new CSSConstant( "dot-dash" );
  public static final CSSConstant DOT_DOT_DASH = new CSSConstant( "dot-dot-dash" );
  public static final CSSConstant WAVE = new CSSConstant( "wave" );
  public static final CSSConstant GROOVE = new CSSConstant( "groove" );
  public static final CSSConstant RIDGE = new CSSConstant( "ridge" );
  public static final CSSConstant INSET = new CSSConstant( "inset" );
  public static final CSSConstant OUTSET = new CSSConstant( "outset" );

  private BorderStyle() {
  }
}
