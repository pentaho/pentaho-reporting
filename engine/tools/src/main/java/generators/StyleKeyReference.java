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
