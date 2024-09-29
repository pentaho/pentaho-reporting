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
 * Creation-Date: 02.12.2005, 19:26:56
 *
 * @author Thomas Morgner
 */
public class TextOverflowMode {
  // clip | ellipsis | ellipsis-word
  public static final CSSConstant CLIP = new CSSConstant( "clip" );
  public static final CSSConstant ELLIPSIS = new CSSConstant( "ellipsis" );
  public static final CSSConstant ELLIPSIS_WORD = new CSSConstant( "ellipsis-word" );

  private TextOverflowMode() {
  }
}
