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

package org.pentaho.reporting.engine.classic.core.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DoubleKeyedCounter<T, U> implements Serializable {
  private HashMap<T, HashMap<U, Integer>> extendedUsageCounter;

  public DoubleKeyedCounter() {
    extendedUsageCounter = new HashMap<T, HashMap<U, Integer>>();
  }

  public void increaseCounter( final T t, final U u ) {
    HashMap<U, Integer> map = extendedUsageCounter.get( t );
    if ( map == null ) {
      map = new HashMap<U, Integer>();
      extendedUsageCounter.put( t, map );
    }

    final Integer count = map.get( u );
    if ( count == null ) {
      map.put( u, 1 );
    } else {
      map.put( u, count + 1 );
    }
  }

  public int get( final T t, final U u ) {
    final HashMap<U, Integer> map = extendedUsageCounter.get( t );
    if ( map == null ) {
      return 0;
    }
    final Integer count = map.get( u );
    if ( count == null ) {
      return 0;
    }
    return count;
  }

  public String printStatistic() {
    final StringBuilder b = new StringBuilder();
    for ( final Map.Entry<T, HashMap<U, Integer>> entry : extendedUsageCounter.entrySet() ) {
      final T key = entry.getKey();
      b.append( key );
      b.append( "\n" );

      final HashMap<U, Integer> lmap = entry.getValue();
      if ( lmap != null ) {
        for ( final Map.Entry<U, Integer> entry2 : lmap.entrySet() ) {
          b.append( "   " );
          b.append( entry2.getKey() );
          b.append( " = " );
          b.append( entry2.getValue() );
          b.append( "\n" );
        }
      }
    }
    return b.toString();
  }
}
