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
 * Creation-Date: 23.11.2005, 12:26:51
 *
 * @author Thomas Morgner
 */
public class BorderImageStretchType {
  public static final CSSConstant STRETCH =
    new CSSConstant( "stretch" );
  public static final CSSConstant REPEAT =
    new CSSConstant( "repeat" );
  public static final CSSConstant ROUND =
    new CSSConstant( "round" );

  private BorderImageStretchType() {
  }
}
