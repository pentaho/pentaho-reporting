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
 * Creation-Date: 30.10.2005, 20:06:54
 *
 * @author Thomas Morgner
 */
public class WordBreak {
  public static final CSSConstant NORMAL = new CSSConstant( "normal" );
  public static final CSSConstant KEEP_ALL = new CSSConstant( "keep-all" );
  public static final CSSConstant LOOSE = new CSSConstant( "loose" );
  public static final CSSConstant BREAK_STRICT = new CSSConstant( "break-strict" );
  public static final CSSConstant BREAK_ALL = new CSSConstant( "break-all" );

  private WordBreak() {
  }
}
