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

package org.pentaho.reporting.libraries.css.keys.border;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 30.10.2005, 19:18:06
 *
 * @author Thomas Morgner
 */
public class BackgroundAttachment {
  public static final CSSConstant SCROLL = new CSSConstant(
    "scroll" );
  public static final CSSConstant FIXED = new CSSConstant(
    "fixed" );
  public static final CSSConstant LOCAL = new CSSConstant(
    "local" );

  private BackgroundAttachment() {
  }
}
