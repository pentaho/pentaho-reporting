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

package org.pentaho.reporting.engine.classic.extensions.toc;

import java.util.Arrays;

public class IndexUtility {
  private IndexUtility() {
  }

  public static String getIndexText( final Integer[] groupCount, final String indexSeparator ) {
    final StringBuilder indexValues = new StringBuilder();
    for ( int i = 0; i < groupCount.length; i++ ) {
      if ( i != 0 ) {
        indexValues.append( indexSeparator );
      }
      indexValues.append( groupCount[ i ] );
    }
    return indexValues.toString();
  }

  public static String getCondensedIndexText( final Integer[] groupCount, final String indexSeparator ) {
    if ( groupCount.length == 0 ) {
      return "";
    }
    if ( groupCount.length == 1 ) {
      return String.valueOf( groupCount[ 0 ] );
    }

    Arrays.sort( groupCount );

    final StringBuffer indexValues = new StringBuffer();
    int lastIndexPrinted = 0;
    indexValues.append( groupCount[ 0 ] );
    for ( int i = 1; i < groupCount.length; i++ ) {
      final Integer integer = groupCount[ i ];
      final Integer prev = groupCount[ i - 1 ];
      if ( integer.intValue() - 1 == prev.intValue() ) {
        continue;
      }

      if ( i - lastIndexPrinted > 2 ) {
        indexValues.append( "-" );
        indexValues.append( prev );
      } else if ( i - lastIndexPrinted == 2 ) {
        indexValues.append( "," );
        indexValues.append( prev );
      }
      indexValues.append( indexSeparator );
      indexValues.append( integer );
      lastIndexPrinted = i;
    }

    if ( lastIndexPrinted < ( groupCount.length - 1 ) ) {
      indexValues.append( "-" );
      indexValues.append( groupCount[ groupCount.length - 1 ] );
    }

    return indexValues.toString();
  }

  public static void main( String[] args ) {
  }

}
