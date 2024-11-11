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


package org.pentaho.reporting.engine.classic.core.states.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple central storage for all sort of caching needs. On use, use your caller's class-name as key to avoid
 * conflicts with other users. This cache is instantiated per report-run and does not need synchronisation.
 */
public class ReportProcessStore implements Serializable {
  private HashMap<String, HashMap<?, ?>> storage;

  public ReportProcessStore() {
    storage = new HashMap<String, HashMap<?, ?>>();
  }

  public <K, V> Map<K, V> get( String id ) {
    HashMap<?, ?> cache = storage.get( id );
    if ( cache != null ) {
      return (Map<K, V>) cache;
    }
    HashMap<K, V> m = new HashMap<K, V>();
    storage.put( id, m );
    return m;
  }
}
