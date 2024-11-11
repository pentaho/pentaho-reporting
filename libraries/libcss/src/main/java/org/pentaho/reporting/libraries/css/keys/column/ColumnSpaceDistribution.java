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


package org.pentaho.reporting.libraries.css.keys.column;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 03.12.2005, 21:11:43
 *
 * @author Thomas Morgner
 */
public class ColumnSpaceDistribution {
  // [start || end || inner || outer || between] | inherit
  public static final CSSConstant START =
    new CSSConstant( "start" );
  public static final CSSConstant END =
    new CSSConstant( "end" );
  public static final CSSConstant INNER =
    new CSSConstant( "inner" );
  public static final CSSConstant OUTER =
    new CSSConstant( "outer" );
  public static final CSSConstant BETWEEN =
    new CSSConstant( "between" );

  private ColumnSpaceDistribution() {
  }
}
