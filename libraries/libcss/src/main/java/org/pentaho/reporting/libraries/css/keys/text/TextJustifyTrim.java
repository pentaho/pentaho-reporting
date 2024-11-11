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
 * Creation-Date: 08.12.2005, 16:56:56
 *
 * @author Thomas Morgner
 */
public class TextJustifyTrim {
  // none | punctuation | punctuation-and-kana
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant PUNCTUATION = new CSSConstant( "punctuation" );
  public static final CSSConstant PUNCTUATION_AND_KANA = new CSSConstant( "punctuation-and-kana" );

  private TextJustifyTrim() {

  }
}
