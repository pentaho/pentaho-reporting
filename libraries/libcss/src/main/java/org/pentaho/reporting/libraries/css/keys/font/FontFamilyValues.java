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

/**
 * Creation-Date: 28.11.2005, 16:36:30
 *
 * @author Thomas Morgner
 */
public class FontFamilyValues {
  public static final CSSConstant SERIF = new CSSConstant( "serif" );
  public static final CSSConstant SANS_SERIF = new CSSConstant( "sans-serif" );
  public static final CSSConstant FANTASY = new CSSConstant( "fantasy" );
  public static final CSSConstant CURSIVE = new CSSConstant( "cursive" );
  public static final CSSConstant MONOSPACE = new CSSConstant( "monospace" );
  public static final CSSConstant NONE = new CSSConstant( "none" );

  private FontFamilyValues() {
  }
}
