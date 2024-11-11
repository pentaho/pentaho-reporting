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
 * Creation-Date: 02.12.2005, 19:51:15
 *
 * @author Thomas Morgner
 */
public class KerningMode {
  // none | [pair || contextual]
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant PAIR = new CSSConstant( "pair" );
  public static final CSSConstant CONTEXTUAL = new CSSConstant( "contextual" );

  private KerningMode() {
  }
}
