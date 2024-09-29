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


package org.pentaho.reporting.libraries.css.model;

import java.util.ArrayList;

/**
 * Creation-Date: 23.11.2005, 11:00:04
 *
 * @author Thomas Morgner
 */
public class CSSMediaRule extends CSSDeclarationRule {
  private ArrayList rules;

  public CSSMediaRule( final StyleSheet parentStyle,
                       final StyleRule parentRule ) {
    super( parentStyle, parentRule );
    this.rules = new ArrayList();
  }

  public void addRule( final StyleRule rule ) {
    rules.add( rule );
  }

  public void insertRule( final int index, final StyleRule rule ) {
    rules.add( index, rule );
  }

  public void deleteRule( final int index ) {
    rules.remove( index );
  }

  public int getRuleCount() {
    return rules.size();
  }

  public StyleRule getRule( int index ) {
    return (StyleRule) rules.get( index );
  }
}
