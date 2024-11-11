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


package org.pentaho.reporting.libraries.css.keys.box;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 28.11.2005, 15:55:25
 *
 * @author Thomas Morgner
 */
public class Clear {
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant LEFT = new CSSConstant( "left" );
  public static final CSSConstant RIGHT = new CSSConstant( "right" );
  public static final CSSConstant TOP = new CSSConstant( "top" );
  public static final CSSConstant BOTTOM = new CSSConstant( "bottom" );
  public static final CSSConstant INSIDE = new CSSConstant( "inside" );
  public static final CSSConstant OUTSIDE = new CSSConstant( "outside" );
  public static final CSSConstant START = new CSSConstant( "start" );
  public static final CSSConstant END = new CSSConstant( "end" );
  public static final CSSConstant BOTH = new CSSConstant( "both" );

  private Clear() {
  }
}
