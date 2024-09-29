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


package org.pentaho.reporting.libraries.css.keys.positioning;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 08.12.2005, 14:52:59
 *
 * @author Thomas Morgner
 */
public class Position {
  public static final CSSConstant ABSOLUTE = new CSSConstant( "absolute" );
  public static final CSSConstant RELATIVE = new CSSConstant( "relative" );
  public static final CSSConstant STATIC = new CSSConstant( "static" );
  public static final CSSConstant FIXED = new CSSConstant( "fixed" );

  private Position() {
  }
}
