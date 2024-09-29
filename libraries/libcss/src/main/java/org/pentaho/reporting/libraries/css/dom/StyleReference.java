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


package org.pentaho.reporting.libraries.css.dom;

/**
 * There are two kinds of style-references. Type one simply references a inline style, which has no style-source. The
 * second one is a external stylesheet that has a style-source, and possibly has a style-content as well.
 *
 * @author : Thomas Morgner
 */
public class StyleReference {
  public static final int LINK = 0;
  public static final int INLINE = 1;

  private String styleContent;
  private int type;

  public StyleReference( final int type, final String styleContent ) {
    this.type = type;
    this.styleContent = styleContent;
  }

  public int getType() {
    return type;
  }

  public String getStyleContent() {
    return styleContent;
  }
}
