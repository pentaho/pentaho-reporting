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
