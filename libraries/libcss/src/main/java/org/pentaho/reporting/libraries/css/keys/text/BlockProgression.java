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
 * Creation-Date: 02.12.2005, 17:32:42
 *
 * @author Thomas Morgner
 */
public class BlockProgression {
  // flow orientation horizontal
  public static CSSConstant TB = new CSSConstant( "tb" );
  // flow orientation vertical
  public static CSSConstant RL = new CSSConstant( "rl" );
  public static CSSConstant LR = new CSSConstant( "lr" );

  private BlockProgression() {
  }
}
