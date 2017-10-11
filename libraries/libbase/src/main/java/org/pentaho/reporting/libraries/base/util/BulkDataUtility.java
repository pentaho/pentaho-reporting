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

package org.pentaho.reporting.libraries.base.util;

/**
 * A utility class to support reordering operations of arrays.
 *
 * @author Thomas Morgner
 */
public final class BulkDataUtility {
  /**
   * Private constructor prevents instantiation.
   */
  private BulkDataUtility() {
  }

  /**
   * Pushes the selected elements up. The elements are given in the data-array, while a selector on whether they should
   * be pushed up is given in the selection array. The operation modifies the data-array.
   *
   * @param data      the array holding the data-objects.
   * @param selection the selection of which elements should be pushed up.
   */
  public static void pushUp( final Object[] data, final boolean[] selection ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    if ( selection == null ) {
      throw new NullPointerException();
    }
    if ( selection.length != data.length ) {
      throw new IllegalArgumentException();
    }

    for ( int i = 1; i < data.length; i++ ) {
      final Object o = data[ i ];
      final boolean selected = selection[ i ];
      if ( selected && ( selection[ i - 1 ] == false ) ) {
        data[ i ] = data[ i - 1 ];
        selection[ i ] = selection[ i - 1 ];
        data[ i - 1 ] = o;
        selection[ i - 1 ] = true;
      }
    }
  }

  /**
   * Pushes the selected elements down. The elements are given in the data-array, while a selector on whether they
   * should be pushed down is given in the selection array. The operation modifies the data-array.
   *
   * @param data      the array holding the data-objects.
   * @param selection the selection of which elements should be pushed down.
   */
  public static void pushDown( final Object[] data, final boolean[] selection ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    if ( selection == null ) {
      throw new NullPointerException();
    }
    if ( selection.length != data.length ) {
      throw new IllegalArgumentException();
    }

    for ( int i = data.length - 2; i >= 0; i-- ) {
      final Object o = data[ i ];
      final boolean selected = selection[ i ];
      if ( selected && ( selection[ i + 1 ] == false ) ) {
        data[ i ] = data[ i + 1 ];
        selection[ i ] = selection[ i + 1 ];
        data[ i + 1 ] = o;
        selection[ i + 1 ] = true;
      }
    }
  }

  /**
   * Pushes up the selected element. The element is compared via reference-equality, so <code>equals()</code> will not
   * be called. It is assumed that the selected object is part of the data-collection. The operation modifies the
   * data-array.
   *
   * @param data      the array holding the data-objects.
   * @param selection the selectioned object that be pushed up.
   */
  public static void pushUpSingleValue( final Object[] data, final Object selection ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    if ( selection == null ) {
      throw new NullPointerException();
    }

    for ( int i = 1; i < data.length; i++ ) {
      final Object o = data[ i ];
      //noinspection ObjectEquality
      final boolean selected = ( selection == o );
      if ( selected ) {
        data[ i ] = data[ i - 1 ];
        data[ i - 1 ] = o;
      }
    }
  }

  /**
   * Pushes the selected element down. The element is compared via reference-equality, so <code>equals()</code> will not
   * be called. It is assumed that the selected object is part of the data-collection. The operation modifies the
   * data-array.
   *
   * @param data      the array holding the data-objects.
   * @param selection the selectioned object that be pushed down.
   */
  public static void pushDownSingleValue( final Object[] data, final Object selection ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    if ( selection == null ) {
      throw new NullPointerException();
    }

    for ( int i = data.length - 2; i >= 0; i-- ) {
      final Object o = data[ i ];
      //noinspection ObjectEquality
      final boolean selected = selection == o;
      if ( selected ) {
        data[ i ] = data[ i + 1 ];
        data[ i + 1 ] = o;
      }
    }
  }
}
