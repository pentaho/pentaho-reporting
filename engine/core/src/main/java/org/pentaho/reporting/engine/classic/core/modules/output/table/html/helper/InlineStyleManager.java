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
