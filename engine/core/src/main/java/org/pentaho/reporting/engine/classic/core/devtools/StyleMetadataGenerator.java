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
