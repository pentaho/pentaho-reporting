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
 * Creation-Date: 02.12.2005, 18:50:43
 *
 * @author Thomas Morgner
 */
public class UnicodeBidi {
  public static final CSSConstant NORMAL = new CSSConstant( "normal" );
  public static final CSSConstant EMBED = new CSSConstant( "embed" );
  public static final CSSConstant BIDI_OVERRIDE = new CSSConstant( "bidi-override" );

  private UnicodeBidi() {
  }
}
