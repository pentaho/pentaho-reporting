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
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;


public class StyleKeyReference {
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
    Arrays.sort( stylesArray, new GroupedMetaDataComparator() );

    printWikiHeader( stylesArray );

    final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
    for ( int i = 0; i < keys.length; i++ ) {
      StyleKey key = keys[ i ];
      if ( styles.containsKey( key.getName() ) == false ) {
        System.out.println( key );
      }
    }

  }

  private static void printWikiHeader( final StyleMetaData[] stylesArray ) {
    Object oldGroup = null;
    for ( int i = 0; i < stylesArray.length; i++ ) {
      final StyleMetaData data = stylesArray[ i ];
      final String grouping = escapeForWiki( data.getGrouping( Locale.ENGLISH ) );
      if ( ObjectUtilities.equal( oldGroup, grouping ) == false ) {
        System.out.println( "h2. " + grouping );
        System.out.println();
        oldGroup = grouping;
      }

      final String name = data.getName();
      final String description = escapeForWiki( data.getDescription( Locale.ENGLISH ) );
      final String displayName = escapeForWiki( data.getDisplayName( Locale.ENGLISH ) );
      final String deprecationMessage = escapeForWiki( data.getDeprecationMessage( Locale.ENGLISH ) );
      final boolean expert = data.isExpert();
      final boolean preferred = data.isPreferred();
      final boolean hidden = data.isHidden();
      final boolean deprecated = data.isDeprecated();

      if ( preferred ) {
        System.out.println( "* *" + name + "* " + "(" + displayName + ")\\\\" );
      } else {
        System.out.println( "* " + name + " (" + displayName + ")\\\\" );
      }

      System.out.println( "\\\\" );
      System.out.println( description + "\\\\" );
      if ( deprecated ) {

        System.out.println( "{warning:title=This style-key is deprecated}" );
        System.out.println(
          "Deprecated stylekeys exist only for legacy purposes to support older report-definitions and must not be "
            + "used. " );
        if ( deprecationMessage != null ) {
          System.out.println( deprecationMessage );
        }
        System.out.println( "{warning}" );
      } else if ( expert ) {
        System.out.println( "{note:title=This style-key is an expert option}" );
        System.out
          .println( "Expert style-keys can be used to fine tune the report. However, they can be dangerous and you" +
            "should know what you are doing or strange things may happen." );
        System.out.println( "{note}" );
      } else if ( hidden ) {
        System.out.println( "{info:title=This style-key is hidden}" );
        System.out.println( "Hidden style-keys do not show up in the report-designer. These style-keys are used to " +
          "transport internal state information and should never be modified outside the report-processing." );
        System.out.println( "{info}" );
      }

      System.out.println();

    }
  }

  public static String escapeForWiki( String text ) {
    return text.replaceAll( "!", "\\\\!" );
  }

}
