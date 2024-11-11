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
 * Describes how replaced content should be scaled if neither the width or height of an element is set to 'auto'.
 *
 * @author Thomas Morgner
 */
public class Fit {
  public static final CSSConstant FILL = new CSSConstant( "fill" );
  public static final CSSConstant NONE = new CSSConstant( "none" );
  public static final CSSConstant MEET = new CSSConstant( "meet" );
  public static final CSSConstant SLICE = new CSSConstant( "slice" );

  private Fit() {
  }
}
