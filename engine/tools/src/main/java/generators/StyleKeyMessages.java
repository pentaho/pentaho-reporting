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

package generators;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.PlainMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class StyleKeyMessages {
  public static void main( final String[] args ) {
    final HashMap styles = new HashMap();

    ClassicEngineBoot.getInstance().start();
    final ElementMetaData[] elementMetaDatas = ElementTypeRegistry.getInstance().getAllElementTypes();
    for ( int i = 0; i < elementMetaDatas.length; i++ ) {
      final ElementMetaData elementMetaData = elementMetaDatas[ i ];
      final StyleMetaData[] styleMetaDatas = elementMetaData.getStyleDescriptions();
      for ( int j = 0; j < styleMetaDatas.length; j++ ) {
        final StyleMetaData styleMetaData = styleMetaDatas[ j ];
        styles.put( styleMetaData.getName(), styleMetaData );
      }
    }

    final StyleMetaData[] stylesArray = (StyleMetaData[]) styles.values().toArray( new StyleMetaData[ styles.size() ] );
    Arrays.sort( stylesArray, new PlainMetaDataComparator() );

    for ( int i = 0; i < stylesArray.length; i++ ) {
      StyleMetaData metaData = stylesArray[ i ];
      final String name = metaData.getName();
      System.out.println( "style." + name + ".display-name=" + name );
      System.out.println( "style." + name + ".grouping=" + filter( metaData.getGrouping( Locale.ENGLISH ), "Group" ) );
      System.out.println( "style." + name + ".description=" + filter( metaData.getDescription( Locale.ENGLISH ), "" ) );
      System.out
        .println( "style." + name + ".deprecated=" + filter( metaData.getDeprecationMessage( Locale.ENGLISH ), "" ) );

    }
  }

  private static String filter( final String grouping, final String defaultValue ) {
    if ( grouping.startsWith( "!" ) && grouping.endsWith( "!" ) ) {
      return defaultValue;
    }
    return grouping;
  }
}
