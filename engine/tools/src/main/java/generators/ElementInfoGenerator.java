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
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.libraries.base.util.HashNMap;

import java.util.Arrays;
import java.util.Locale;

public class ElementInfoGenerator {
  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final HashNMap expressionsByGroup = new HashNMap();
    final Locale locale = Locale.getDefault();

    ElementTypeRegistry registry = ElementTypeRegistry.getInstance();
    final ElementMetaData[] elementMetaDatas = registry.getAllElementTypes();
    for ( int i = 0; i < elementMetaDatas.length; i++ ) {
      final ElementMetaData metaData = elementMetaDatas[ i ];
      final String grouping = metaData.getGrouping( locale );
      expressionsByGroup.add( grouping, metaData );
    }

    final Object[] keys = expressionsByGroup.keySet().toArray();
    Arrays.sort( keys );
    for ( int i = 0; i < keys.length; i++ ) {
      final Object key = keys[ i ];
      System.out.println( "Group: '" + key + "' Size: " + expressionsByGroup.getValueCount( key ) );
      final Object[] objects = expressionsByGroup.toArray( key );
      for ( int j = 0; j < objects.length; j++ ) {
        ElementMetaData metaData = (ElementMetaData) objects[ j ];
        System.out.println( "   " + metaData.getName() );
        final StyleMetaData[] styleDescriptions = metaData.getStyleDescriptions();
        Arrays.sort( styleDescriptions, new GroupedMetaDataComparator() );
        System.out.println( "     Styles:" );
        for ( int k = 0; k < styleDescriptions.length; k++ ) {
          final StyleMetaData styleMetaData = styleDescriptions[ k ];
          System.out.println( "       " + styleMetaData.getGrouping( locale ) + ":" + styleMetaData.getDisplayName(
            locale ) );
        }

        //        final AttributeMetaData[] attrs = metaData.getAttributeDescriptions();
        //        Arrays.sort(attrs, new GroupedMetaDataComparator());
        //        System.out.println ("     Attributes:");
        //        for (int k = 0; k < attrs.length; k++)
        //        {
        //          final AttributeMetaData attr = attrs[k];
        //          System.out.println ("       " + attr.getGrouping(locale) + ":"  + attr.getDisplayName(locale));
        //        }
      }
    }
  }
}
