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

package org.pentaho.reporting.libraries.css.counter;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.css.LibCssBoot;
import org.pentaho.reporting.libraries.css.counter.numeric.DecimalCounterStyle;

import java.util.HashMap;
import java.util.Iterator;


public class CounterStyleFactory {
  private static final CounterStyle DEFAULTCOUNTER = new DecimalCounterStyle();

  private static CounterStyleFactory factory;
  public static final String PREFIX = "org.pentaho.reporting.libraries.css.counter.numbering.";

  public static synchronized CounterStyleFactory getInstance() {
    if ( factory == null ) {
      factory = new CounterStyleFactory();
      factory.registerDefaults();
    }
    return factory;
  }

  private HashMap knownCounters;

  private CounterStyleFactory() {
    knownCounters = new HashMap();
  }

  public void registerDefaults() {
    final Configuration config = LibCssBoot.getInstance().getGlobalConfig();
    final Iterator it = config.findPropertyKeys( PREFIX );
    while ( it.hasNext() ) {
      final String key = (String) it.next();
      final String counterClass = config.getConfigProperty( key );
      if ( counterClass == null ) {
        continue;
      }
      final Object o = ObjectUtilities.loadAndInstantiate
        ( counterClass, CounterStyleFactory.class, CounterStyle.class );
      if ( o instanceof CounterStyle ) {
        final String name = key.substring( PREFIX.length() );
        knownCounters.put( name, o );
      }
    }
  }

  public CounterStyle getCounterStyle( final String name ) {
    final CounterStyle cs = (CounterStyle) knownCounters.get( name );
    if ( cs != null ) {
      return cs;
    }
    return DEFAULTCOUNTER;
  }
}
