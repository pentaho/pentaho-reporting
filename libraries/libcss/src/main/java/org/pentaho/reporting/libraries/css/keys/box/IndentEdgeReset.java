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
 * Creation-Date: 30.10.2005, 18:27:41
 *
 * @author Thomas Morgner
 */
public class IndentEdgeReset {
  public static final CSSConstant NONE =
    new CSSConstant( "none" );
  public static final CSSConstant MARGIN_EDGE =
    new CSSConstant( "margin-edge" );
  public static final CSSConstant BORDER_EDGE =
    new CSSConstant( "border-edge" );
  public static final CSSConstant PADDING_EDGE =
    new CSSConstant( "padding-edge" );
  public static final CSSConstant CONTENT_EDGE =
    new CSSConstant( "content-edge" );

  private IndentEdgeReset() {
  }

}
