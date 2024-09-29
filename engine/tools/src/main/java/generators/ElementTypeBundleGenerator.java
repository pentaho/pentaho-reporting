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
import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ElementTypeBundleGenerator {
  public static final String GLOBAL_BUNDLE = "org.pentaho.reporting.engine.classic.core.metadata.messages";

  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final TreeMap globalAttributes = new TreeMap();
    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    for ( int i = 0; i < datas.length; i++ ) {
      final ElementMetaData data = datas[ i ];
      if ( data instanceof AbstractMetaData == false ) {
        continue;
      }
      printMetaBundle( data, globalAttributes );
    }
    System.out.println( "-----------------------------------------------------" );

    final Iterator iterator = globalAttributes.entrySet().iterator();
    while ( iterator.hasNext() ) {
      final Map.Entry o = (Map.Entry) iterator.next();

      final AttributeMetaData attribute = (AttributeMetaData) o.getValue();
      final AbstractMetaData aamd = (AbstractMetaData) attribute;
      final String akeyPrefix = aamd.getKeyPrefix();
      final String abundle = aamd.getBundleLocation();
      final String aname = attribute.getName();

      System.out.println( akeyPrefix + aname + ".display-name=" + aname );
      System.out.println( akeyPrefix + aname + ".grouping=" + filter( aamd.getGrouping( Locale.ENGLISH ), "Group" ) );
      System.out.println( akeyPrefix + aname + ".description=" + filter( aamd.getDescription( Locale.ENGLISH ), "" ) );
      System.out
        .println( akeyPrefix + aname + ".deprecated=" + filter( aamd.getDeprecationMessage( Locale.ENGLISH ), "" ) );
    }
  }

  private static void printMetaBundle( final ElementMetaData data, Map globalAttributes ) {
    System.out.println( "-----------------------------------------------------" );
    final AbstractMetaData amd = (AbstractMetaData) data;
    final String keyPrefix = amd.getKeyPrefix();
    final String ename = amd.getName();
    final String ebundle = amd.getBundleLocation();

    System.out.println( keyPrefix + ename + ".display-name=" + ename );
    System.out.println( keyPrefix + ename + ".grouping=" + filter( amd.getGrouping( Locale.ENGLISH ), "Group" ) );
    System.out.println( keyPrefix + ename + ".description=" + filter( amd.getDescription( Locale.ENGLISH ), "" ) );
    System.out
      .println( keyPrefix + ename + ".deprecated=" + filter( amd.getDeprecationMessage( Locale.ENGLISH ), "" ) );

    final AttributeMetaData[] attributes = data.getAttributeDescriptions();

    for ( int j = 0; j < attributes.length; j++ ) {
      final AttributeMetaData attribute = attributes[ j ];
      final AbstractMetaData aamd = (AbstractMetaData) attribute;
      final String akeyPrefix = aamd.getKeyPrefix();
      final String abundle = aamd.getBundleLocation();
      final String aname = attribute.getName();
      if ( abundle.equals( GLOBAL_BUNDLE ) && akeyPrefix.startsWith( "attribute." ) ) {
        globalAttributes.put( aname, attribute );
        continue;
      }
      System.out.println( akeyPrefix + aname + ".display-name=" + aname );
      System.out.println( akeyPrefix + aname + ".grouping=" + filter( aamd.getGrouping( Locale.ENGLISH ), "Group" ) );
      System.out.println( akeyPrefix + aname + ".description=" + filter( aamd.getDescription( Locale.ENGLISH ), "" ) );
      System.out
        .println( akeyPrefix + aname + ".deprecated=" + filter( aamd.getDeprecationMessage( Locale.ENGLISH ), "" ) );
    }

    System.out.println( "-----------------------------------------------------" );
  }


  private static String filter( final String grouping, final String defaultValue ) {
    if ( grouping.startsWith( "!" ) && grouping.endsWith( "!" ) ) {
      return defaultValue;
    }
    return grouping;
  }
}
