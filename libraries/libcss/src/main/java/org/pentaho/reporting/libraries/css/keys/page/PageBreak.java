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


package org.pentaho.reporting.libraries.css.keys.page;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 30.11.2005, 18:00:14
 *
 * @author Thomas Morgner
 */
public class PageBreak {
  public static CSSConstant ALWAYS = new CSSConstant( "always" );
  public static CSSConstant AVOID = new CSSConstant( "avoid" );
  public static CSSConstant LEFT = new CSSConstant( "left" );
  public static CSSConstant RIGHT = new CSSConstant( "right" );

  private PageBreak() {
  }
}
