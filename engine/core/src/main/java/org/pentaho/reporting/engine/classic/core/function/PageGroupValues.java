/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
