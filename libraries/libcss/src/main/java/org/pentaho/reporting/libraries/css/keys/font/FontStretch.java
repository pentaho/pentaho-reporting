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

package org.pentaho.reporting.libraries.css.keys.font;

import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;

public class FontStretch {
  public static final CSSConstant NORMAL =
    new CSSConstant( "normal" );
  public static final CSSConstant ULTRA_CONDENSED =
    new CSSConstant( "ultra-condensed" );
  public static final CSSConstant EXTRA_CONDENSED =
    new CSSConstant( "extra-condensed" );
  public static final CSSConstant CONDENSED =
    new CSSConstant( "condensed" );
  public static final CSSConstant SEMI_CONDENSED =
    new CSSConstant( "semi-condensed" );
  public static final CSSConstant SEMI_EXPANDED =
    new CSSConstant( "semi-expanded" );
  public static final CSSConstant EXPANDED =
    new CSSConstant( "expanded" );
  public static final CSSConstant EXTRA_EXPANDED =
    new CSSConstant( "extra-expanded" );
  public static final CSSConstant ULTRA_EXPANDED =
    new CSSConstant( "ultra-expanded" );

  public static final CSSConstant WIDER = new CSSConstant( "wider" );
  public static final CSSConstant NARROWER = new CSSConstant( "narrower" );

  private FontStretch() {
  }

  public static int getOrder( CSSValue fs ) {
    if ( ULTRA_CONDENSED.equals( fs ) ) {
      return -4;
    }
    if ( EXTRA_CONDENSED.equals( fs ) ) {
      return -3;
    }
    if ( CONDENSED.equals( fs ) ) {
      return -2;
    }
    if ( SEMI_CONDENSED.equals( fs ) ) {
      return -1;
    }
    if ( NORMAL.equals( fs ) ) {
      return 0;
    }
    if ( SEMI_EXPANDED.equals( fs ) ) {
      return 1;
    }
    if ( EXPANDED.equals( fs ) ) {
      return 2;
    }
    if ( EXTRA_EXPANDED.equals( fs ) ) {
      return 3;
    }
    if ( ULTRA_EXPANDED.equals( fs ) ) {
      return 4;
    }
    return 0;
  }

  public static CSSConstant getByOrder( int order ) {
    switch( order ) {
      case -4:
        return ULTRA_CONDENSED;
      case -3:
        return EXTRA_CONDENSED;
      case -2:
        return CONDENSED;
      case -1:
        return SEMI_CONDENSED;
      case 1:
        return SEMI_EXPANDED;
      case 2:
        return EXPANDED;
      case 3:
        return EXTRA_EXPANDED;
      case 4:
        return ULTRA_EXPANDED;
      case 0:
        return NORMAL;
    }

    if ( order < -4 ) {
      return ULTRA_CONDENSED;
    }
    return ULTRA_EXPANDED;
  }
}
