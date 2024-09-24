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

package org.pentaho.reporting.libraries.css.keys.font;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 28.11.2005, 16:44:47
 *
 * @author Thomas Morgner
 */
public class FontWeight {
  public static final CSSConstant NORMAL = new CSSConstant( "normal" );
  public static final CSSConstant BOLD = new CSSConstant( "bold" );

  public static final CSSConstant BOLDER = new CSSConstant( "bolder" );
  public static final CSSConstant LIGHTER = new CSSConstant( "lighter" );

  private FontWeight() {
  }
}
