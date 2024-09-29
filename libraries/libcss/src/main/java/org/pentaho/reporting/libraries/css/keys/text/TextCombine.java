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
 * Creation-Date: 03.12.2005, 20:04:17
 *
 * @author Thomas Morgner
 */
public class TextCombine {
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant LETTER = new CSSConstant( "letter" );
  public static final CSSConstant LINE = new CSSConstant( "line" );

  private TextCombine() {
  }
}
