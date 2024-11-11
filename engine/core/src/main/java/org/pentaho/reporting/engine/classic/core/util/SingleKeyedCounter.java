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


package org.pentaho.reporting.engine.classic.core.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SingleKeyedCounter<T> implements Serializable {
  private HashMap<T, Integer> usageCounter;

  public SingleKeyedCounter() {
    usageCounter = new HashMap<T, Integer>();
  }

  public void increaseCounter( T t ) {
    final Integer integer = usageCounter.get( t );
    if ( integer == null ) {
      usageCounter.put( t, 1 );
    } else {
      usageCounter.put( t, integer + 1 );
    }
  }

  public int getCounter( T t ) {
    final Integer integer = usageCounter.get( t );
    if ( integer == null ) {
      return 0;
    }
    return integer;
  }

  public String printStatistic() {
    final StringBuilder b = new StringBuilder();
    for ( final Map.Entry<T, Integer> entry : usageCounter.entrySet() ) {
      final T key = entry.getKey();
      b.append( key );
      b.append( "=" );
      b.append( entry.getValue() );
      b.append( "\n" );
    }
    return b.toString();
  }
}
