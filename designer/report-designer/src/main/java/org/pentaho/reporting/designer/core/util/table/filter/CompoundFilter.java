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

package org.pentaho.reporting.designer.core.util.table.filter;

import java.util.ArrayList;

public class CompoundFilter implements Filter {
  private ArrayList<Filter> filters;

  public CompoundFilter() {
    filters = new ArrayList<Filter>();
  }

  public void addFilter( final Filter f ) {
    if ( f == null ) {
      throw new NullPointerException();
    }
    filters.add( f );
  }

  public void removeFilter( final Filter f ) {
    if ( f == null ) {
      throw new NullPointerException();
    }
    filters.remove( f );
  }

  public Result isMatch( final Object o ) {
    for ( final Filter filter : filters ) {
      final Result match = filter.isMatch( o );
      if ( match == Result.REJECT ) {
        return Result.REJECT;
      }
      if ( match == Result.ACCEPT ) {
        return Result.ACCEPT;
      }
    }
    return Result.UNDECIDED;
  }

  public static CompoundFilter create( final Filter... filters ) {
    final CompoundFilter filter = new CompoundFilter();
    for ( int i = 0; i < filters.length; i++ ) {
      filter.addFilter( filters[ i ] );
    }
    return filter;
  }
}
