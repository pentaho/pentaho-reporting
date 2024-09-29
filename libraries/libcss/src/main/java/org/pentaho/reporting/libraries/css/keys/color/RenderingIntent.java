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


package org.pentaho.reporting.libraries.css.keys.color;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 30.10.2005, 18:58:52
 *
 * @author Thomas Morgner
 */
public class RenderingIntent {
  public static final CSSConstant PERCEPTUAL = new CSSConstant(
    "perceptual" );
  public static final CSSConstant RELATIVE_COLORIMETRIC = new CSSConstant(
    "relative-colorimetric" );
  public static final CSSConstant SATURATION = new CSSConstant(
    "saturation" );
  public static final CSSConstant ABSOLUTE_COLORIMETRIC = new CSSConstant(
    "absolute-colorimetric" );

  private RenderingIntent() {
  }
}
