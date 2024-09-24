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
