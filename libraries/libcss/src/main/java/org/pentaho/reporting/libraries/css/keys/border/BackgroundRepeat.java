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


package org.pentaho.reporting.libraries.css.keys.border;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 30.10.2005, 19:14:47
 *
 * @author Thomas Morgner
 */
public class BackgroundRepeat {
  public static final CSSConstant REPEAT = new CSSConstant( "repeat" );
  public static final CSSConstant NOREPEAT = new CSSConstant( "no-repeat" );
  public static final CSSConstant SPACE = new CSSConstant( "space" );

  public static final CSSConstant REPEAT_X = new CSSConstant( "repeat-y" );
  public static final CSSConstant REPEAT_Y = new CSSConstant( "repeat-x" );

  private BackgroundRepeat() {
  }

}
