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

package org.pentaho.reporting.libraries.css.keys.hyperlinks;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 17:26:17
 *
 * @author Thomas Morgner
 */
public class TargetName {
  public static final CSSConstant CURRENT =
    new CSSConstant( "current" );
  public static final CSSConstant ROOT =
    new CSSConstant( "root" );
  public static final CSSConstant PARENT =
    new CSSConstant( "parent" );
  public static final CSSConstant NEW =
    new CSSConstant( "new" );
  public static final CSSConstant MODAL =
    new CSSConstant( "modal" );

  private TargetName() {
  }
}
