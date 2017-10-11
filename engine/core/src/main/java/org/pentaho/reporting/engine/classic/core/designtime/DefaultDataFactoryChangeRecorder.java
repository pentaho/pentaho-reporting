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
