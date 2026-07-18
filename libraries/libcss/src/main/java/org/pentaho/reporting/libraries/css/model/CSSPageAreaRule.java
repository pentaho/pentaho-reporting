/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.model;

import org.pentaho.reporting.libraries.css.PageAreaType;

/**
 * Creation-Date: 29.05.2006, 18:04:01
 *
 * @author Thomas Morgner
 */
public class CSSPageAreaRule extends CSSDeclarationRule {
  private PageAreaType pageArea;

  public CSSPageAreaRule( final StyleSheet parentStyle,
                          final StyleRule parentRule,
                          final PageAreaType pageArea ) {
    super( parentStyle, parentRule );
    if ( pageArea == null ) {
      throw new NullPointerException();
    }
    this.pageArea = pageArea;
  }

  public PageAreaType getPageArea() {
    return pageArea;
  }
}
