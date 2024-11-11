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
 * Creation-Date: 30.10.2005, 19:59:54
 *
 * @author Thomas Morgner
 */
public class WhitespaceCollapse {
  public static final CSSConstant PRESERVE = new CSSConstant(
    "preserve" );
  public static final CSSConstant COLLAPSE = new CSSConstant(
    "collapse" );
  public static final CSSConstant PRESERVE_BREAKS = new CSSConstant(
    "preserve-breaks" );
  public static final CSSConstant DISCARD = new CSSConstant(
    "discard" );

  private WhitespaceCollapse() {
  }
}
