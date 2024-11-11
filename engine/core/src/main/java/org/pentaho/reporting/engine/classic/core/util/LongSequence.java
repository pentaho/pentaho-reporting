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


package org.pentaho.reporting.engine.classic.core.util;

public class LongSequence extends LongList {
  private long fillValue;

  public LongSequence( final int capacity, final long fillValue ) {
    super( capacity );
    this.fillValue = fillValue;
  }

  public void increment( final int position ) {
    if ( position < 0 ) {
      throw new IndexOutOfBoundsException();
    }

    if ( position >= size() ) {
      fillSequence( position + 1 );
    }
    final long oldValue = get( position );
    set( position, oldValue + 1 );
  }

  private void fillSequence( final int targetSize ) {
    while ( size() < targetSize ) {
      add( fillValue );
    }
  }

  public long get( final int index ) {
    if ( index < 0 ) {
      throw new IndexOutOfBoundsException();
    }

    if ( index >= size() ) {
      return 0;
    }
    return super.get( index );
  }

  public void set( final int position, final long value ) {
    if ( position < 0 ) {
      throw new IndexOutOfBoundsException();
    }

    if ( position >= size() ) {
      fillSequence( position + 1 );
    }
    super.set( position, value );
  }

  public void fill( final long value ) {
    super.fill( value );
  }
}
