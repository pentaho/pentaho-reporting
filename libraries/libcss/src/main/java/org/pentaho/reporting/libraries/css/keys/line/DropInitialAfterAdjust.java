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

package org.pentaho.reporting.libraries.css.keys.line;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 28.11.2005, 19:23:08
 *
 * @author Thomas Morgner
 */
public class DropInitialAfterAdjust {
  // central | middle | after-edge | text-after-edge | ideographic | alphabetic | mathematical
  public static final CSSConstant CENTRAL =
    new CSSConstant( "central" );
  public static final CSSConstant MIDDLE =
    new CSSConstant( "middle" );
  public static final CSSConstant AFTER_EDGE =
    new CSSConstant( "after-edge" );
  public static final CSSConstant TEXT_AFTER_EDGE =
    new CSSConstant( "text-after-edge" );
  public static final CSSConstant IDEOGRAPHIC =
    new CSSConstant( "ideographic" );
  public static final CSSConstant ALPHABETIC =
    new CSSConstant( "alphabetic" );
  public static final CSSConstant MATHEMATICAL =
    new CSSConstant( "mathematical" );

  private DropInitialAfterAdjust() {
  }
}
