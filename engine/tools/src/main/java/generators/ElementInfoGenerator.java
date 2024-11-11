/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
