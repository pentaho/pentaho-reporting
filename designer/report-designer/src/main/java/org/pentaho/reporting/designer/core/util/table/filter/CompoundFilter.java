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
