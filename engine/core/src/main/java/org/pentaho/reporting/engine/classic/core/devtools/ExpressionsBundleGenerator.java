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
import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.MetaData;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;

public class ExpressionsBundleGenerator {
  private ExpressionsBundleGenerator() {
  }

  public static void main( final String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final ExpressionMetaData[] datas = ExpressionRegistry.getInstance().getAllExpressionMetaDatas();
    Arrays.sort( datas, GroupedMetaDataComparator.ENGLISH );
    for ( int i = 0; i < datas.length; i++ ) {
      final ExpressionMetaData data = datas[i];
      if ( data instanceof AbstractMetaData == false ) {
        continue;
      }
      printMetaBundle( data );
    }
  }

  private static void printMetaBundle( final ExpressionMetaData type ) {
    System.out.println( "# -----------------------------------------------------" );
    System.out.println( "# " + printBundleLocation( type ) );
    System.out.println( "# -----------------------------------------------------" );
    final String prefix = calculatePrefix( type );

    printMetadata( type, prefix, "display-name", type.getName() );
    printMetadata( type, prefix, "grouping", "" );
    printMetadata( type, prefix, "grouping.ordinal", "0" );
    printMetadata( type, prefix, "ordinal", "0" );
    printMetadata( type, prefix, "description", "" );
    printMetadata( type, prefix, "deprecated", "" );
    printMetadata( type, prefix, "icon", "" );
    System.out.println();

    final ExpressionPropertyMetaData[] attributes = type.getPropertyDescriptions();
    Arrays.sort( attributes, GroupedMetaDataComparator.ENGLISH );

    for ( int j = 0; j < attributes.length; j++ ) {
      final ExpressionPropertyMetaData attribute = attributes[j];
      final String propertyPrefix = calculatePrefix( attribute );

      printMetadata( attribute, propertyPrefix, "display-name", attribute.getName() );
      printMetadata( attribute, propertyPrefix, "grouping", "" );
      printMetadata( attribute, propertyPrefix, "grouping.ordinal", "0" );
      printMetadata( attribute, propertyPrefix, "ordinal", "0" );
      printMetadata( attribute, propertyPrefix, "description", "" );
      printMetadata( attribute, propertyPrefix, "deprecated", "" );
      System.out.println();
    }

    System.out.println( "-----------------------------------------------------" );
  }

  private static String calculatePrefix( final MetaData type ) {
    final String prefix;
    if ( type instanceof AbstractMetaData ) {
      final AbstractMetaData metaData = (AbstractMetaData) type;
      final String prefixMetadata = metaData.getKeyPrefix();
      if ( StringUtils.isEmpty( prefixMetadata ) ) {
        prefix = "";
      } else {
        prefix = prefixMetadata + type.getName() + ".";
      }
    } else {
      prefix = "";
    }
    return prefix;
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

  private static String printBundleLocation( final ExpressionMetaData metaData ) {
    if ( metaData instanceof AbstractMetaData ) {
      final AbstractMetaData metaDataImpl = (AbstractMetaData) metaData;
      return metaDataImpl.getBundleLocation().replace( '.', '/' ) + ".properties";
    }

    return metaData.getExpressionType().getCanonicalName().replace( '.', '/' ) + "Bundle.properties";
  }
}
