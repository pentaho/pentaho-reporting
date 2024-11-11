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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

public class DescendantMatcher implements NodeMatcher {
  private NodeMatcher childMatcher;

  public DescendantMatcher( final NodeMatcher childMatcher ) {
    this.childMatcher = childMatcher;
  }

  public boolean matches( final RenderNode node ) {
    RenderNode n = node;
    while ( n != null ) {
      final RenderBox parent = n.getParent();
      if ( parent == null ) {
        break;
      }
      if ( childMatcher.matches( parent ) ) {
        return true;
      }
      n = parent;
    }
    return false;
  }

  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append( "Descendant(" );
    b.append( childMatcher );
    b.append( ")" );
    return b.toString();
  }
}
