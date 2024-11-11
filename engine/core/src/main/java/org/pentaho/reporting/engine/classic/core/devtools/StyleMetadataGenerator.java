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


package org.pentaho.reporting.engine.classic.core.devtools;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.MetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class StyleMetadataGenerator {
  private static class AttributeCarrier implements Comparable {
    public AttributeMetaData metaData;
    public String prefix;

    private AttributeCarrier( final String prefix, final AttributeMetaData metaData ) {
      this.prefix = prefix;
      this.metaData = metaData;
    }

    public int compareTo( final Object o ) {
      final AttributeCarrier carrier = (AttributeCarrier) o;
      final int compare = GroupedMetaDataComparator.ENGLISH.compare( this.metaData, carrier.metaData );
      if ( compare == 0 ) {
        return prefix.compareTo( carrier.prefix );
      }
      return compare;
    }
  }

  private static String readMetadataAttribute( final MetaData metaData, final String name, final String defaultValue ) {
    final String metaAttribute = metaData.getMetaAttribute( name, Locale.ENGLISH );
    if ( metaAttribute == null ) {
      return defaultValue;
    }
    return metaAttribute;
  }

  private static void printMetadata( final MetaData metaData, final String prefix, final String name,
      final String defaultValue ) {
    System.out.println( prefix + name + "=" + readMetadataAttribute( metaData, name, defaultValue ) );
  }

  public static void main( final String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final HashMap<String, StyleMetaData> allStyles = new HashMap<String, StyleMetaData>();

    final ElementMetaData[] allTypes = ElementTypeRegistry.getInstance().getAllElementTypes();
    Arrays.sort( allTypes, GroupedMetaDataComparator.ENGLISH );

    for ( int i = 0; i < allTypes.length; i++ ) {
      final ElementMetaData type = allTypes[i];
      final StyleMetaData[] styles = type.getStyleDescriptions();
      for ( int j = 0; j < styles.length; j++ ) {
        final StyleMetaData style = styles[j];
        allStyles.put( style.getName(), style );
      }
    }

    final StyleMetaData[] objects = allStyles.values().toArray( new StyleMetaData[allStyles.size()] );
    Arrays.sort( objects, GroupedMetaDataComparator.ENGLISH );
    for ( int i = 0; i < objects.length; i++ ) {
      final StyleMetaData style = objects[i];
      final String stylePrefix = "style." + style.getName() + ".";
      printMetadata( style, stylePrefix, "display-name", style.getName() );
      printMetadata( style, stylePrefix, "grouping", "" );
      printMetadata( style, stylePrefix, "grouping.ordinal", "0" );
      printMetadata( style, stylePrefix, "ordinal", "0" );
      printMetadata( style, stylePrefix, "description", "" );
      printMetadata( style, stylePrefix, "deprecated", "" );
      System.out.println();
    }
  }
}
