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


package org.pentaho.reporting.engine.classic.core.testsupport.selector;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

public class AndMatcher implements NodeMatcher {
  private NodeMatcher left;
  private NodeMatcher right;

  public AndMatcher( final NodeMatcher left, final NodeMatcher right ) {
    this.left = left;
    this.right = right;
  }

  public boolean matches( final RenderNode node ) {
    if ( left.matches( node ) == false ) {
      return false;
    }
    if ( right.matches( node ) == false ) {
      return false;
    }

    return true;
  }

  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append( "AndMatcher(" );
    b.append( left );
    b.append( ";" );
    b.append( right );
    b.append( ")" );
    return b.toString();
  }
}
