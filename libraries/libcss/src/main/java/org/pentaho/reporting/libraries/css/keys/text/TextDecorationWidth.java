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

public class TextDecorationWidth {
  /**
   * A bold is basicly an auto, that is thicker than the normal auto value.
   */
  public static final CSSConstant BOLD = new CSSConstant( "bold" );

  /**
   * A dash is basicly an auto, that is thinner than the normal auto value.
   */
  public static final CSSConstant DASH = new CSSConstant( "dash" );

  /**
   * The text decoration width is the normal text decoration width for the nominal font. If no font characteristic
   * exists for the width of the text decoration in question, the user agent should proceed as though 'auto' were
   * specified.
   * <p/>
   * The computed value is 'normal'.
   */
  public static final CSSConstant NORMAL = new CSSConstant( "normal" );

  private TextDecorationWidth() {
  }
}
