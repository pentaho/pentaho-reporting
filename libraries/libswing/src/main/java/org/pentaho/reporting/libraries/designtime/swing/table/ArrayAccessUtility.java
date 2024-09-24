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

package org.pentaho.reporting.libraries.designtime.swing.table;

import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.FastPropertyEditorManager;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;

public class ArrayAccessUtility {
  public static boolean isArray( final Object array ) {
    if ( array == null ) {
      return false;
    }
    if ( array.getClass().isArray() == false ) {
      return false;
    }
    return true;
  }

  public static Object[] normalizeArray( final Object array ) {
    if ( array == null ) {
      throw new IllegalArgumentException();
    }
    if ( array.getClass().isArray() == false ) {
      throw new IllegalArgumentException();
    }

    final Object[] retval = new Object[ Array.getLength( array ) ];
    for ( int i = 0; i < retval.length; i++ ) {
      retval[ i ] = Array.get( array, i );
    }
    return retval;
  }

  public static Object normalizeNative( final Object[] data, final Class arrayType ) {
    final Object array = Array.newInstance( arrayType, data.length );
    for ( int i = 0; i < data.length; i++ ) {
      final Object o = data[ i ];
      if ( o != null && arrayType.isInstance( o ) == false ) {
        throw new ClassCastException( "Object " + o + " cannot be cast to " + arrayType );
      }
      Array.set( array, i, o );
    }
    return array;
  }

  public static String getArrayAsString( final Object array ) {
    if ( array == null ) {
      throw new IllegalArgumentException();
    }
    if ( array.getClass().isArray() == false ) {
      throw new IllegalArgumentException();
    }
    final StringBuilder b = new StringBuilder();
    b.append( '[' );
    final int length = Array.getLength( array );
    for ( int i = 0; i < length; i++ ) {
      if ( i > 0 ) {
        b.append( ", " );
      }

      final Object value = Array.get( array, i );
      if ( value == null ) {
        b.append( "<null>" ); // NON-NLS
      } else if ( value instanceof String ) {
        b.append( value );
      } else {
        final PropertyEditor propertyEditor = FastPropertyEditorManager.findEditor( value.getClass() );
        if ( propertyEditor != null ) {
          propertyEditor.setValue( value );
          b.append( propertyEditor.getAsText() );
        } else {
          b.append( value );
        }
      }
    }

    b.append( ']' );
    return b.toString();
  }
}
