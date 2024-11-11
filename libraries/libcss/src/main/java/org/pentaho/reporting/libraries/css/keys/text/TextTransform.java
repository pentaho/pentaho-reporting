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
 * Creation-Date: 03.12.2005, 19:56:12
 *
 * @author Thomas Morgner
 */
public class TextTransform {
  public static final CSSConstant CAPITALIZE = new CSSConstant( "capitalize" );
  public static final CSSConstant UPPERCASE = new CSSConstant( "uppercase" );
  public static final CSSConstant LOWERCASE = new CSSConstant( "lowercase" );
  public static final CSSConstant NONE = new CSSConstant( "none" );

  private TextTransform() {
  }
}
