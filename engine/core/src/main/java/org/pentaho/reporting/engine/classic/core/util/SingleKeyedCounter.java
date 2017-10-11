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
