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


package org.pentaho.reporting.libraries.css.keys.color;

import org.pentaho.reporting.libraries.css.values.CSSColorValue;
import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * A set of color mappings which map the constants to system default values. This *could* also include colors which map
 * to standard UI colors, like 'caption background' etc.
 *
 * @author Thomas Morgner
 */
public final class CSSSystemColors {
  private CSSSystemColors() {
  }

  public static final CSSColorValue TRANSPARENT = new CSSColorValue( 0, 0, 0, 0 );
  public static final CSSConstant CURRENT_COLOR = new CSSConstant( "currentColor" );
}
