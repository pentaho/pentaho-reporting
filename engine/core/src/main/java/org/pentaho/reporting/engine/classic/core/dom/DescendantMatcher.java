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

package org.pentaho.reporting.engine.classic.core.dom;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;

public class DescendantMatcher implements NodeMatcher {
  private NodeMatcher childMatcher;

  public DescendantMatcher( final NodeMatcher childMatcher ) {
    this.childMatcher = childMatcher;
  }

  public boolean matches( final MatcherContext context, final ReportElement node ) {
    ReportElement n = node;
    while ( n != null ) {

      final Section parent = context.getParent( n );
      if ( parent == null ) {
        break;
      }
      if ( childMatcher.matches( context, parent ) ) {
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
