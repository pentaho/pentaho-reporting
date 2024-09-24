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
 * Creation-Date: 24.11.2005, 16:38:22
 *
 * @author Thomas Morgner
 */
public class LineStackingStrategy {
  public static final CSSConstant INLINE_LINE_HEIGHT =
    new CSSConstant( "inline-line-height" );
  public static final CSSConstant BLOCK_LINE_HEIGHT =
    new CSSConstant( "block-line-height" );
  public static final CSSConstant MAX_LINE_HEIGHT =
    new CSSConstant( "max-line-height" );
  public static final CSSConstant GRID_HEIGHT =
    new CSSConstant( "grid-height" );


  private LineStackingStrategy() {
  }
}
