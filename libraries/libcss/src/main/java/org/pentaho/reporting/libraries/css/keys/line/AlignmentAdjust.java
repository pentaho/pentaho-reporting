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


package org.pentaho.reporting.libraries.css.keys.line;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 16:50:16
 *
 * @author Thomas Morgner
 */
public class AlignmentAdjust {
  public static final CSSConstant BASELINE =
    new CSSConstant( "baseline" );
  public static final CSSConstant AFTER_EDGE =
    new CSSConstant( "after-edge" );
  public static final CSSConstant BEFORE_EDGE =
    new CSSConstant( "before-edge" );
  public static final CSSConstant TEXT_AFTER_EDGE =
    new CSSConstant( "text-after-edge" );
  public static final CSSConstant TEXT_BEFORE_EDGE =
    new CSSConstant( "text-before-edge" );

  public static final CSSConstant ALPHABETIC =
    new CSSConstant( "alphabetic" );
  public static final CSSConstant HANGING =
    new CSSConstant( "hanging" );
  public static final CSSConstant IDEOGRAPHIC =
    new CSSConstant( "ideographic" );
  public static final CSSConstant MATHEMATICAL =
    new CSSConstant( "mathematical" );
  public static final CSSConstant CENTRAL =
    new CSSConstant( "central" );
  public static final CSSConstant MIDDLE =
    new CSSConstant( "middle" );

  private AlignmentAdjust() {
  }
}
