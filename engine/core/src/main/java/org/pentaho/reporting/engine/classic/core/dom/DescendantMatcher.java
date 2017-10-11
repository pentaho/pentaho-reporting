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
