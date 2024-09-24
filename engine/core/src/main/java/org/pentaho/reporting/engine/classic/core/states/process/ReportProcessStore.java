/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
