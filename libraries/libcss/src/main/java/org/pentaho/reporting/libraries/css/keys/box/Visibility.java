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


package org.pentaho.reporting.libraries.css.keys.box;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 30.10.2005, 18:43:27
 *
 * @author Thomas Morgner
 */
public class Visibility {
  public static final CSSConstant VISIBLE = new CSSConstant( "visible" );
  public static final CSSConstant HIDDEN = new CSSConstant( "hidden" );
  public static final CSSConstant COLLAPSE = new CSSConstant( "collapse" );

  private Visibility() {
  }
}
