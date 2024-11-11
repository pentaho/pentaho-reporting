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


package org.pentaho.reporting.libraries.css.model;

public class CSSCounterRule extends CSSDeclarationRule {
  private String name;

  public CSSCounterRule( final StyleSheet parentStyle,
                         final StyleRule parentRule,
                         final String name ) {
    super( parentStyle, parentRule );
    // name can be null, to define a global counter
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
