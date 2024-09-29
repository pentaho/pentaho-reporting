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
 * Creation-Date: 27.11.2005, 21:21:59
 *
 * @author Thomas Morgner
 */
public class BorderWidth {
  public static final CSSConstant THIN =
    new CSSConstant( "thin" );
  public static final CSSConstant MEDIUM =
    new CSSConstant( "medium" );
  public static final CSSConstant THICK =
    new CSSConstant( "thick" );

  private BorderWidth() {
  }
}
