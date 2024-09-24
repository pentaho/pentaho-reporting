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
 * A page rule contains (among others) page area rules as childs.
 *
 * @author Thomas Morgner
 */
public class CSSPageRule extends CSSDeclarationRule {
  private ArrayList rules; // the margin rules ...
  private String name;
  private String pseudoPage;

  public CSSPageRule( final StyleSheet parentStyle,
                      final StyleRule parentRule,
                      final String name,
                      final String pseudoPage ) {
    super( parentStyle, parentRule );
    this.pseudoPage = pseudoPage;
    this.name = name;
    this.rules = new ArrayList();
  }

  public void addRule( final CSSPageAreaRule rule ) {
    rules.add( rule );
  }

  public void insertRule( final int index, final CSSPageAreaRule rule ) {
    rules.add( index, rule );
  }

  public void deleteRule( final int index ) {
    rules.remove( index );
  }

  public int getRuleCount() {
    return rules.size();
  }

  public CSSPageAreaRule getRule( int index ) {
    return (CSSPageAreaRule) rules.get( index );
  }

  public String getName() {
    return name;
  }

  public String getPseudoPage() {
    return pseudoPage;
  }
}
