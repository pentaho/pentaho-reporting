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
 * Creation-Date: 28.11.2005, 19:55:17
 *
 * @author Thomas Morgner
 */
public class TextJustify {
  public static final CSSConstant INTER_WORD = new CSSConstant( "inter-word" );
  public static final CSSConstant INTER_IDEOGRAPH = new CSSConstant( "inter-ideograph" );
  public static final CSSConstant INTER_CHARACTER = new CSSConstant( "inter-character" );
  public static final CSSConstant INTER_CLUSTER = new CSSConstant( "inter-cluster" );
  public static final CSSConstant KASHIDA = new CSSConstant( "kashida" );
  public static final CSSConstant SIZE = new CSSConstant( "size" );

  private TextJustify() {
  }
}
