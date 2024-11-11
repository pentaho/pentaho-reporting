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


package org.pentaho.reporting.libraries.css.keys.box;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Defines the floating property. Floating elements create a new flow inside an existing flow.
 * <p/>
 * The properties left and top are equivalent, as as right and bottom. All properties in the specification can be
 * reduced to either left or right in the computation phase.
 * <p/>
 * Floating images cannot leave their containing block vertically or horizontally. If negative margins are given, they
 * may be shifted outside the content area, but vertical margins will increase the 'empty-space' between the blocks
 * instead of messing up the previous element.
 *
 * @author Thomas Morgner
 */
public class Floating {
  public static final CSSConstant LEFT = new CSSConstant( "left" );
  public static final CSSConstant RIGHT = new CSSConstant( "right" );
  public static final CSSConstant TOP = new CSSConstant( "top" );
  public static final CSSConstant BOTTOM = new CSSConstant( "bottom" );
  public static final CSSConstant INSIDE = new CSSConstant( "inside" );
  public static final CSSConstant OUTSIDE = new CSSConstant( "outside" );
  public static final CSSConstant START = new CSSConstant( "start" );
  public static final CSSConstant END = new CSSConstant( "end" );
  public static final CSSConstant NONE = new CSSConstant( "none" );

  // from the column stuff
  public static final CSSConstant IN_COLUMN = new CSSConstant( "in-column" );
  public static final CSSConstant MID_COLUMN = new CSSConstant( "mid-column" );

  private Floating() {
  }

}
