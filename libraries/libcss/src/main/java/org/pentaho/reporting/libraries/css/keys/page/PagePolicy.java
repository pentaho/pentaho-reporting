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
 * Creation-Date: 30.11.2005, 18:39:57
 *
 * @author Thomas Morgner
 */
public class PagePolicy {
  public static CSSConstant START = new CSSConstant( "start" );
  public static CSSConstant FIRST = new CSSConstant( "first" );
  public static CSSConstant LAST = new CSSConstant( "last" );

  private PagePolicy() {
  }
}
