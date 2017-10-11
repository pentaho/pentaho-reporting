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
