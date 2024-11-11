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


package org.pentaho.reporting.libraries.css.keys.line;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 17:08:01
 *
 * @author Thomas Morgner
 */
public class VerticalAlign {
  public static final CSSConstant USE_SCRIPT =
    new CSSConstant( "use-script" );
  public static final CSSConstant BASELINE =
    new CSSConstant( "baseline" );
  public static final CSSConstant SUB =
    new CSSConstant( "sub" );
  public static final CSSConstant SUPER =
    new CSSConstant( "super" );

  public static final CSSConstant TOP =
    new CSSConstant( "top" );
  public static final CSSConstant TEXT_TOP =
    new CSSConstant( "text-top" );
  public static final CSSConstant CENTRAL =
    new CSSConstant( "central" );
  public static final CSSConstant MIDDLE =
    new CSSConstant( "middle" );
  public static final CSSConstant BOTTOM =
    new CSSConstant( "bottom" );
  public static final CSSConstant TEXT_BOTTOM =
    new CSSConstant( "text-bottom" );

  private VerticalAlign() {
  }
}
