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
