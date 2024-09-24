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
 * 'glyph-orientation-vertical' controls glyph orientation when the flow orientation is vertical.
 *
 * @author Thomas Morgner
 */
public class GlyphOrientationVertical {
  public static final CSSConstant UPRIGHT = new CSSConstant( "upright" );
  public static final CSSConstant INLINE = new CSSConstant( "inline" );

  private GlyphOrientationVertical() {
  }
}
