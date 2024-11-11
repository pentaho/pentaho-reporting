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


package org.pentaho.reporting.libraries.css.keys.font;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Defines the emphasize marker position in asian texts. If the text layout is horizontal (ie Roman style), before means
 * above the text, and after means below the text.
 * <p/>
 * See: CSS3-Fonts ?4-3
 *
 * @author Thomas Morgner
 */
public class FontEmphasizePosition {
  public static final CSSConstant BEFORE =
    new CSSConstant( "before" );
  public static final CSSConstant AFTER =
    new CSSConstant( "after" );
  public static final CSSConstant ABOVE =
    new CSSConstant( "above" );
  public static final CSSConstant BELOW =
    new CSSConstant( "below" );

  private FontEmphasizePosition() {
  }
}
