/*
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
 * Copyright (c) 2001 - 2023 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Creation-Date: 06.05.2007, 19:26:00
 *
 * @author Thomas Morgner
 */
public class GlobalStyleManager implements StyleManager {
  private static class EntryComparator implements Comparator, Serializable {
    private EntryComparator() {
    }

    public int compare( final Object o1, final Object o2 ) {
      final Map.Entry e1 = (Map.Entry) o1;
      final Map.Entry e2 = (Map.Entry) o2;
      return String.valueOf( e1.getValue() ).compareTo( String.valueOf( e2.getValue() ) );
    }
  }

  private static final EntryComparator comparator = new EntryComparator();

  private HashMap<StoredStyle, String> styles;
  private HashSet<String> stylesText;
  private int nameCounter;
  private String lineSeparator;

  public GlobalStyleManager() {
    this.styles = new HashMap<StoredStyle, String>();
    this.stylesText = new HashSet<String>();
    this.lineSeparator = StringUtils.getLineSeparator();
  }

  /**
   * Updates the given attribute-List according to the current style rules.
   *
   * @param styleBuilder
   * @param attributeList
   * @return the modified attribute list.
   */
  public AttributeList updateStyle( final StyleBuilder styleBuilder, final AttributeList attributeList ) {
    return updateStyleForcedStyleName( styleBuilder, attributeList, null );
  }

  /**
   * Updates the given attribute-List according to the current style rules. ForcedStyleNames cannot be reused
   * and will be ignored if null or empty.
   *
   * @param styleBuilder
   * @param attributeList
   * @param forcedStyleName
   * @return the modified attribute list.
   */
  public AttributeList updateStyleForcedStyleName( final StyleBuilder styleBuilder, final AttributeList attributeList,
                                                   String forcedStyleName ) {
    if ( styleBuilder.isEmpty() ) {
      return attributeList;
    }

    final StoredStyle value = new StoredStyle( styleBuilder );
    final String styleText = styleBuilder.toString();
    String styleName = styles.get( value );
    if ( styleName == null ) {
      styleName = StringUtils.isEmpty( forcedStyleName ) ? "style-" + nameCounter++ : forcedStyleName;
      styles.put( value, styleName );
      if ( stylesText.contains( styleText ) ) {
        throw new IllegalStateException();
      }
      stylesText.add( styleText );
    }

    final String attribute = attributeList.getAttribute( HtmlPrinter.XHTML_NAMESPACE, "class" );
    if ( attribute != null ) {
      attributeList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "class", attribute + ' ' + styleName );
    } else {
      attributeList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "class", styleName );
    }
    return attributeList;
  }

  /**
   * Returns the global stylesheet, or null, if no global stylesheet was built.
   *
   * @return
   */
  public String getGlobalStyleSheet() {

    final StringBuffer b = new StringBuffer( 8192 );
    final Map.Entry<StoredStyle, String>[] keys = styles.entrySet().toArray( new Map.Entry[styles.size()] );
    Arrays.sort( keys, GlobalStyleManager.comparator );
    for ( final Map.Entry<StoredStyle, String> entry : keys ) {
      final StoredStyle style = entry.getKey();
      final String name = entry.getValue();

      b.append( '.' );
      b.append( name );
      b.append( " {" );
      b.append( lineSeparator );
      style.print( b, false );
      b.append( lineSeparator );
      b.append( '}' );
      b.append( lineSeparator );
      b.append( lineSeparator );
    }
    return b.toString();
  }

  public void write( final Writer writer ) throws IOException {
    final Map.Entry<StoredStyle, String>[] keys = styles.entrySet().toArray( new Map.Entry[styles.size()] );
    Arrays.sort( keys, GlobalStyleManager.comparator );

    for ( final Map.Entry<StoredStyle, String> entry : keys ) {
      final StoredStyle style = entry.getKey();
      final String name = entry.getValue();

      writer.write( '.' );
      writer.write( name );
      writer.write( " {" );
      writer.write( lineSeparator );
      style.print( writer, false );
      writer.write( lineSeparator );
      writer.write( '}' );
      writer.write( lineSeparator );
      writer.write( lineSeparator );
    }
  }
}
