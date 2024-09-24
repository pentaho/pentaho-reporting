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
