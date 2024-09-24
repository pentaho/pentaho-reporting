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

package org.pentaho.reporting.libraries.css.keys.list;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 01.12.2005, 18:40:19
 *
 * @author Thomas Morgner
 */
public class ListStyleTypeGlyphs {
  // box | check | circle | diamond | disc | hyphen | square
  public static final CSSConstant BOX = new CSSConstant( "box" );
  public static final CSSConstant CHECK = new CSSConstant( "check" );
  public static final CSSConstant CIRCLE = new CSSConstant( "circle" );
  public static final CSSConstant DIAMOND = new CSSConstant( "diamon" );
  public static final CSSConstant DISC = new CSSConstant( "disc" );
  public static final CSSConstant HYPHEN = new CSSConstant( "hyphen" );
  public static final CSSConstant SQUARE = new CSSConstant( "square" );

  private ListStyleTypeGlyphs() {
  }
}
