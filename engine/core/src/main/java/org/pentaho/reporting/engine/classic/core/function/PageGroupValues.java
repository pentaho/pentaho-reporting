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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Convenience class to manage getting and putting values stored by page and group.
 * <p/>
 * <p>
 * Used by <code>TotalPageItemCountFunction</code>.
 * </p>
 *
 * @author Matt Campbell
 */
class PageGroupValues {
  private Map<Integer, Map<ReportStateKey, Object>> pagedResults;

  PageGroupValues() {
    pagedResults = new HashMap<Integer, Map<ReportStateKey, Object>>();
  }

  public Object get( final int page, final ReportStateKey group ) {
    if ( pagedResults.containsKey( page ) && pagedResults.get( page ).containsKey( group ) ) {
      return pagedResults.get( page ).get( group );
    } else {
      return 0;
    }
  }

  public void put( final int page, final ReportStateKey group, final Object value ) {
    final Map<ReportStateKey, Object> map;
    if ( pagedResults.containsKey( page ) ) {
      map = pagedResults.get( page );
    } else {
      map = new HashMap<ReportStateKey, Object>();
      pagedResults.put( page, map );
    }
    map.put( group, value );
  }
}
