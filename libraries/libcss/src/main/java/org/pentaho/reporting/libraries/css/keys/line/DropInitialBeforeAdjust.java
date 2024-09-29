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
public class DropInitialBeforeAdjust {
  public static final CSSConstant CENTRAL =
    new CSSConstant( "central" );
  public static final CSSConstant MIDDLE =
    new CSSConstant( "middle" );
  public static final CSSConstant MATHEMATICAL =
    new CSSConstant( "mathematical" );
  public static final CSSConstant BEFORE_EDGE =
    new CSSConstant( "before-edge" );
  public static final CSSConstant TEXT_BEFORE_EDGE =
    new CSSConstant( "text-before-edge" );
  public static final CSSConstant HANGING =
    new CSSConstant( "hanging" );

  private DropInitialBeforeAdjust() {
  }
}
