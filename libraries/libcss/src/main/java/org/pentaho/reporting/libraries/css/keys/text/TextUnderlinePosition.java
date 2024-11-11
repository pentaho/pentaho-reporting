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
 * Creation-Date: 02.12.2005, 20:25:56
 *
 * @author Thomas Morgner
 */
public class TextUnderlinePosition {
  public static final CSSConstant BEFORE_EDGE =
    new CSSConstant( "before-edge" );
  public static final CSSConstant AFTER_EDGE =
    new CSSConstant( "after-edge" );
  public static final CSSConstant ALPHABETIC =
    new CSSConstant( "alphabetic" );

  private TextUnderlinePosition() {
  }
}
