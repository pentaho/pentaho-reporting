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


package org.pentaho.reporting.engine.classic.core.dom;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;

public class ChildMatcher implements NodeMatcher {
  private NodeMatcher childMatcher;

  public ChildMatcher( final NodeMatcher childMatcher ) {
    this.childMatcher = childMatcher;
  }

  public boolean matches( final MatcherContext context, final ReportElement node ) {
    final Section parent = context.getParent( node );
    if ( parent == null ) {
      return false;
    }

    return childMatcher.matches( context, parent );
  }

  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append( "ChildMatcher(" );
    b.append( childMatcher );
    b.append( ")" );
    return b.toString();
  }
}
