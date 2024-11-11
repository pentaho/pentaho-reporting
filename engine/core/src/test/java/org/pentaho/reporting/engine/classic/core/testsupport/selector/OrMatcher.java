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


package org.pentaho.reporting.engine.classic.core.testsupport.selector;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

public class OrMatcher implements NodeMatcher {
  private NodeMatcher[] conditions;

  public OrMatcher( final NodeMatcher... conditions ) {
    this.conditions = conditions;
  }

  public boolean matches( final RenderNode node ) {
    boolean result = false;

    for ( int i = 0; i < conditions.length; i++ ) {
      final NodeMatcher condition = conditions[i];
      final boolean subresult = condition.matches( node );
      if ( subresult ) {
        result = true;
      }
    }
    return result;
  }

  public String toString() {
    final StringBuilder b = new StringBuilder();
    for ( int i = 0; i < conditions.length; i++ ) {
      final NodeMatcher condition = conditions[i];
      if ( i != 0 ) {
        b.append( ",\n" );
      }
      b.append( condition );
    }
    return b.toString();
  }
}
