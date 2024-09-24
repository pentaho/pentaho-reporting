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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;

import java.io.IOException;
import java.io.Writer;

public class InlineStyleManager implements StyleManager {
  public InlineStyleManager() {
  }

  /**
   * Updates the given attribute-List according to the current style rules.
   *
   * @param styleBuilder
   * @param attributeList
   * @return the modified attribute list.
   */
  public AttributeList updateStyle( final StyleBuilder styleBuilder, final AttributeList attributeList ) {
    if ( attributeList == null ) {
      throw new NullPointerException();
    }
    if ( styleBuilder == null ) {
      throw new NullPointerException();
    }
    if ( styleBuilder.isEmpty() ) {
      return attributeList;
    }

    final String style = attributeList.getAttribute( HtmlPrinter.XHTML_NAMESPACE, "style" );
    if ( style != null ) {
      final String trimmedStyle = style.trim();
      if ( trimmedStyle.length() > 0 && trimmedStyle.charAt( trimmedStyle.length() - 1 ) == ';' ) {
        attributeList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "style", style + ' ' + styleBuilder.toString() );
      } else {
        attributeList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "style", style + "; " + styleBuilder.toString() );
      }
      return attributeList;
    }

    attributeList.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "style", styleBuilder.toString() );
    return attributeList;
  }

  /**
   * Returns the global stylesheet, or null, if no global stylesheet was built.
   *
   * @return
   */
  public String getGlobalStyleSheet() {
    return null;
  }

  public void write( final Writer writer ) throws IOException {
    throw new IOException( "InlineStyleManager cannot generate a global style" );
  }
}
