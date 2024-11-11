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


package org.pentaho.reporting.libraries.css.keys.text;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 02.12.2005, 19:43:56
 *
 * @author Thomas Morgner
 */
public class TextAutoSpace {
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant IDEOGRAPH_NUMERIC = new CSSConstant( "ideograph-numeric" );
  public static final CSSConstant IDEOGRAPH_ALPHA = new CSSConstant( "ideograph-alpha" );
  public static final CSSConstant IDEOGRAPH_SPACE = new CSSConstant( "ideograph-space" );
  public static final CSSConstant IDEOGRAPH_PARENTHESIS = new CSSConstant( "ideograph-parenthesis" );


  private TextAutoSpace() {
  }
}
