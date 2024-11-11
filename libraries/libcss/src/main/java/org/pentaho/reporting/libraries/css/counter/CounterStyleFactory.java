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
