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
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;

import java.util.Iterator;
import java.util.TreeMap;

public class ElementMetaGenerator {
  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final TreeMap allStyles = new TreeMap();
    final ElementMetaData[] allTypes = ElementTypeRegistry.getInstance().getAllElementTypes();
    for ( int i = 0; i < allTypes.length; i++ ) {
      final ElementMetaData type = allTypes[ i ];
      final String prefix = "element." + type.getName();
      System.out.println( prefix + ".display-name=" + type.getName() );
      System.out.println( prefix + ".description=" + type.getName() );
      System.out.println( prefix + ".grouping=Group" );

      final AttributeMetaData[] attributes = type.getAttributeDescriptions();
      for ( int j = 0; j < attributes.length; j++ ) {
        final AttributeMetaData attribute = attributes[ j ];
        final String attrNsPrefix = ElementTypeRegistry.getInstance().getNamespacePrefix( attribute.getNameSpace() );
        final String attrPrefix = "element." + type.getName() + ".attribute." +
          attrNsPrefix + "." + attribute.getName();
        System.out.println( attrPrefix + ".display-name=" + attribute.getName() );
        System.out.println( attrPrefix + ".description=" + attribute.getName() );
        System.out.println( attrPrefix + ".grouping=" + attrNsPrefix );
      }

      final StyleMetaData[] styles = type.getStyleDescriptions();
      for ( int j = 0; j < styles.length; j++ ) {
        final StyleMetaData style = styles[ j ];
        allStyles.put( style.getName(), style );
      }
    }

    final Iterator styleIt = allStyles.values().iterator();
    while ( styleIt.hasNext() ) {
      StyleMetaData style = (StyleMetaData) styleIt.next();
      final String attrPrefix = "style." + style.getName();
      System.out.println( attrPrefix + ".display-name=" + style.getName() );
      System.out.println( attrPrefix + ".description=" );
      System.out.println( attrPrefix + ".grouping=Group" );
    }
  }
}
