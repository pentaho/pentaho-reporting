/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.dom;

import org.pentaho.reporting.engine.classic.core.ReportElement;

public class OrMatcher implements NodeMatcher {
  private NodeMatcher[] conditions;

  public OrMatcher( final NodeMatcher... conditions ) {
    this.conditions = conditions;
  }

  public boolean matches( final MatcherContext context, final ReportElement node ) {
    boolean result = false;

    for ( int i = 0; i < conditions.length; i++ ) {
      final NodeMatcher condition = conditions[i];
      final boolean subresult = condition.matches( context, node );
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
