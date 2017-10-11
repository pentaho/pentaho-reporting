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

package org.pentaho.reporting.engine.classic.extensions.docsupport;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.LengthPropertyEditor;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.FontSmooth;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;

public class StylePageGenerator {
  public static void main( String[] args ) {
    ClassicEngineBoot.getInstance().start();
    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    final GroupedMetaDataComparator comp = new GroupedMetaDataComparator();
    Arrays.sort( datas, comp );

    final Locale locale = Locale.getDefault();
    for ( int i = 0; i < datas.length; i++ ) {
      final ElementMetaData elementMetaData = datas[ i ];
      if ( elementMetaData.isHidden() ) {
        continue;
      }
      printMetaData( locale, elementMetaData );
    }

    printMetaData( locale, ElementTypeRegistry.getInstance().getElementType( "master-report" ) );
    printMetaData( locale, ElementTypeRegistry.getInstance().getElementType( "relational-group" ) );

  }

  private static void printMetaData( final Locale locale, final ElementMetaData elementMetaData ) {
    System.out.println();
    System.out.println( "h1. " + elementMetaData.getDisplayName( locale ) );

    final StyleMetaData[] styleMetaDatas = elementMetaData.getStyleDescriptions();
    printGrouped( styleMetaDatas, locale );
  }

  private static void printGrouped( final StyleMetaData[] datas, final Locale locale ) {
    Arrays.sort( datas, new GroupedMetaDataComparator() );
    String group = null;
    for ( int i = 0; i < datas.length; i++ ) {
      final StyleMetaData data = datas[ i ];
      if ( data.isHidden() ) {
        continue;
      }
      if ( ObjectUtilities.equal( data.getGrouping( locale ), group ) == false ) {
        group = data.getGrouping( locale );
        System.out.println();
        System.out.println( "h2. " + group );
        System.out.println( "||Name (Internal-Name)||Value-Type||Description||" );
      }
      final StyleKey key = data.getStyleKey();
      if ( data.isPreferred() ) {
        System.out.print( "|*" + data.getDisplayName( locale ) + "* (" + key.getName() + ") |" );
      } else if ( data.isDeprecated() ) {
        System.out.print( "|-" + data.getDisplayName( locale ) + "-|" );
      } else {
        System.out.print( "|" + data.getDisplayName( locale ) + "|" );
      }
      System.out.print( computeValueTypeText( key, data ) + " |" );
      System.out.print( computeDescription( locale, data ) + " |" );
      System.out.println();
    }
  }

  private static String computeValueTypeText( final StyleKey key, final StyleMetaData data ) {
    if ( key.equals( ElementStyleKeys.VALIGNMENT ) ) {
      return "ElementAlignment: " + printArray( ElementAlignment.TOP, ElementAlignment.MIDDLE,
        ElementAlignment.BOTTOM );
    }
    if ( key.equals( ElementStyleKeys.ALIGNMENT ) ) {
      return "ElementAlignment: " + printArray( ElementAlignment.LEFT, ElementAlignment.CENTER,
        ElementAlignment.RIGHT );
    }

    final Class aClass = key.getValueType();
    if ( String.class.equals( aClass ) ) {
      return "Text";
    }
    if ( data.getEditor() instanceof LengthPropertyEditor ) {
      return "Length";
    }
    if ( Integer.class.equals( aClass ) ) {
      return "Integer";
    }
    if ( Double.class.equals( aClass ) ) {
      return "Double";
    }
    if ( Float.class.equals( aClass ) ) {
      return "Float";
    }
    if ( Color.class.equals( aClass ) ) {
      return "Colour";
    }
    if ( Boolean.class.equals( aClass ) ) {
      return "Boolean: " + printArray( Boolean.TRUE, Boolean.FALSE );
    }
    if ( BorderStyle.class.equals( aClass ) ) {
      return "BorderStyle: " + printArray( BorderStyle.NONE, BorderStyle.HIDDEN, BorderStyle.DOTTED,
        BorderStyle.DASHED, BorderStyle.SOLID, BorderStyle.DOUBLE, BorderStyle.DOT_DASH,
        BorderStyle.DOT_DOT_DASH, BorderStyle.WAVE, BorderStyle.GROOVE, BorderStyle.RIDGE,
        BorderStyle.INSET, BorderStyle.OUTSET );
    }
    if ( BoxSizing.class.equals( aClass ) ) {
      return "BoxSizing: " + printArray( BoxSizing.BORDER_BOX, BoxSizing.CONTENT_BOX );
    }
    if ( FontSmooth.class.equals( aClass ) ) {
      return "FontSmooth: " + printArray( FontSmooth.AUTO, FontSmooth.ALWAYS, FontSmooth.NEVER );
    }
    if ( WhitespaceCollapse.class.equals( aClass ) ) {
      return "WhitespaceCollapse: " + printArray( WhitespaceCollapse.COLLAPSE, WhitespaceCollapse.DISCARD,
        WhitespaceCollapse.PRESERVE, WhitespaceCollapse.PRESERVE_BREAKS );
    }
    if ( TextWrap.class.equals( aClass ) ) {
      return "TextWrap: " + printArray( TextWrap.WRAP, TextWrap.NONE );
    }
    if ( VerticalTextAlign.class.equals( aClass ) ) {
      return "VerticalTextAlign: " + printArray( VerticalTextAlign.TOP, VerticalTextAlign.BOTTOM,
        VerticalTextAlign.MIDDLE, VerticalTextAlign.BASELINE, VerticalTextAlign.CENTRAL,
        VerticalTextAlign.SUB, VerticalTextAlign.SUPER, VerticalTextAlign.TEXT_BOTTOM,
        VerticalTextAlign.TEXT_TOP, VerticalTextAlign.TOP, VerticalTextAlign.USE_SCRIPT );
    }
    return String.valueOf( key.getValueType() );
  }

  private static String printArray( Object... arary ) {
    StringBuffer b = new StringBuffer();
    b.append( "One of " );
    for ( int i = 0; i < arary.length; i++ ) {
      final Object o = arary[ i ];
      if ( i > 0 ) {
        b.append( ", " );
      }
      b.append( "'" );
      b.append( o );
      b.append( "'" );

    }
    return b.toString();
  }

  private static String computeDescription( final Locale locale, final StyleMetaData data ) {
    String description = data.getDescription( locale );
    if ( data.getStyleKey().isInheritable() ) {
      if ( description.length() > 0 ) {
        description += "\\\\\n\\\\\n";
      }
      description += "Values for this key can be inherited from parent bands.";
    }
    return description;
  }
}
