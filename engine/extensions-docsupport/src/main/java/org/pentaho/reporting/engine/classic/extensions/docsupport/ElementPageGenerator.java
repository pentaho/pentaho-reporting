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

package org.pentaho.reporting.engine.classic.extensions.docsupport;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Arrays;
import java.util.Locale;

public class ElementPageGenerator {
  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    final GroupedMetaDataComparator comp = new GroupedMetaDataComparator();
    Arrays.sort( datas, comp );

    final Locale locale = Locale.getDefault();
    String group = null;
    for ( int i = 0; i < datas.length; i++ ) {
      final ElementMetaData data = datas[ i ];
      if ( data.isHidden() ) {
        continue;
      }
      if ( ObjectUtilities.equal( data.getGrouping( locale ), group ) == false ) {
        group = data.getGrouping( locale );
        System.out.println();
        System.out.println( "h2. " + group );
      }
      printElementInfo( locale, data );
    }

    System.out.println();
    System.out.println( "h2. structural-elements" );
    printElementInfo( locale, ElementTypeRegistry.getInstance().getElementType( "master-report" ) );
    printElementInfo( locale, ElementTypeRegistry.getInstance().getElementType( "relational-group" ) );

  }

  private static void printElementInfo( final Locale locale, final ElementMetaData data ) {
    System.out.print( "* " );
    if ( data.isDeprecated() ) {
      System.out.print( "Deprecated - " );
    }
    if ( data.isPreferred() ) {
      System.out.print( "*" );
    }
    System.out.print( "[" + data.getDisplayName( locale ) + "|" + data.getName() + "]" );
    if ( data.isPreferred() ) {
      System.out.print( "*" );
    }
    System.out.println();
  }
}
