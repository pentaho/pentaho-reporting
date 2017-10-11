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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;

import java.io.Serializable;
import java.util.HashMap;

public class SubReportStorage implements Serializable {
  private HashMap<FunctionStorageKey, SubReport> storage;

  public SubReportStorage() {
    storage = new HashMap<FunctionStorageKey, SubReport>();
  }

  public void store( final FunctionStorageKey key, final SubReport subReport ) throws ReportProcessingException {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( subReport == null ) {
      throw new NullPointerException();
    }

    // derive would regenerate instance-IDs, which is not advisable.
    storage.put( key, subReport.derive( true ) );
  }

  public SubReport restore( final FunctionStorageKey key ) throws ReportProcessingException {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final SubReport subReport = storage.get( key );
    if ( subReport == null ) {
      return null;
    }

    return subReport.derive( true );
  }

  public boolean contains( final FunctionStorageKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    return storage.containsKey( key );
  }
}
