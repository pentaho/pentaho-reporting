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

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.BulkArrayList;
import org.pentaho.reporting.engine.classic.core.util.Sequence;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Simple data structure to replace HashMap and improve Total functions performance
 *
 * @param <T>
 */
public class StateSequence<T> implements Serializable {


  private static final int CAPACITY = 30;
  private transient BulkArrayList<ReportStateKey> stateKeys;
  private transient BulkArrayList<Sequence<T>> sequences;

  public StateSequence( final int capacity ) {
    this.stateKeys = new BulkArrayList<>( capacity );
    this.sequences = new BulkArrayList<>( capacity );
  }

  public StateSequence() {
    this.stateKeys = new BulkArrayList<>( CAPACITY );
    this.sequences = new BulkArrayList<>( CAPACITY );
  }

  public void clear() {
    stateKeys.clear();
    sequences.clear();
  }

  public void add( final ReportStateKey key, final Sequence<T> result ) {
    stateKeys.add( key );
    sequences.add( result );
  }

  public void update( final int index, final ReportStateKey key, final Sequence<T> result ) {
    stateKeys.set( index, key );
    sequences.set( index, result );
  }

  boolean resultExists() {
    return sequences.size() > 0;
  }

  void updateResult( final int index, final Sequence<T> result ) {
    sequences.set( index, result );
  }

  public Sequence<T> getResult( final int index ) {
    return sequences.get( index );
  }

  public BulkArrayList<ReportStateKey> getKeys() {
    return stateKeys;
  }

  // from head or from tail?
  public int getKeyIndex( final ReportStateKey key ) {
    if ( stateKeys.size() == 0 || key == null ) {
      return -1;
    }
    ReportStateKey current;
    for ( int j = stateKeys.size() - 1; j > -1; j-- ) {
      current = stateKeys.get( j );

      // equal is more expensive then hashCode
      if ( current.hashCode() == key.hashCode() && current.equals( key ) ) {
        return j;
      }
    }
    return -1;
  }

  private Object readResolve() throws ObjectStreamException {
    this.stateKeys = new BulkArrayList<>( CAPACITY );
    this.sequences = new BulkArrayList<>( CAPACITY );
    return this;
  }
}
