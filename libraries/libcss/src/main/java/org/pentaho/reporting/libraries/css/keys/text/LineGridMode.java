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


package org.pentaho.reporting.libraries.css.keys.text;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 03.12.2005, 19:46:22
 *
 * @author Thomas Morgner
 */
public class LineGridMode {
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant IDEOGRAPH = new CSSConstant( "ideograph" );
  public static final CSSConstant ALL = new CSSConstant( "all" );

  private LineGridMode() {
  }
}
