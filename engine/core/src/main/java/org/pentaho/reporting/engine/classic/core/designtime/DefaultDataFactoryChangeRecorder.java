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


package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;

import java.util.ArrayList;

public class DefaultDataFactoryChangeRecorder implements DataFactoryChangeRecorder {
  private ArrayList<DataFactoryChange> changes;

  public DefaultDataFactoryChangeRecorder() {
    changes = new ArrayList<DataFactoryChange>();
  }

  public void recordChange( final DataFactoryChange change ) {
    changes.add( change );
  }

  public DataFactoryChange[] getChanges() {
    return changes.toArray( new DataFactoryChange[changes.size()] );
  }

  public static void applyChanges( final CompoundDataFactory cdf, final DataFactoryChange[] changes ) {
    for ( int i = 0; i < changes.length; i++ ) {
      final DataFactoryChange change = changes[i];
      final DataFactory oldValue = change.getOldValue();
      final DataFactory newValue = change.getNewValue();
      if ( oldValue == newValue ) {
        continue;
      }
      if ( oldValue != null && newValue != null ) {
        final int index = cdf.indexOfByReference( oldValue );
        cdf.set( index, newValue );
        continue;
      }
      if ( oldValue != null ) {
        final int index = cdf.indexOfByReference( oldValue );
        cdf.remove( index );
        continue;
      }
      cdf.add( newValue );
    }
  }
}
