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
