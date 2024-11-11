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
