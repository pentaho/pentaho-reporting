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

import java.util.ArrayList;

public class Sequence<T> extends ArrayList<T> {
  public Sequence( final int initialCapacity ) {
    super( initialCapacity );
  }

  public Sequence() {
  }

  private void fillSequence( final int targetSize ) {
    while ( size() < targetSize ) {
      add( null );
    }
  }

  public T get( final int index ) {
    if ( index < 0 ) {
      throw new IndexOutOfBoundsException();
    }

    if ( index >= size() ) {
      return null;
    }
    return super.get( index );
  }

  public T set( final int position, final T value ) {
    if ( position < 0 ) {
      throw new IndexOutOfBoundsException();
    }

    if ( position >= size() ) {
      fillSequence( position + 1 );
    }
    return super.set( position, value );
  }

  public Sequence<T> clone() {
    return (Sequence<T>) super.clone();
  }
}
