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


package org.pentaho.reporting.engine.classic.core.util.beans;

import java.awt.Font;

public class FontValueConverter implements ValueConverter {
  public FontValueConverter() {
  }

  /**
   * Converts an object to an attribute value.
   *
   * @param o
   *          the object.
   * @return the attribute value.
   * @throws BeanException
   *           if there was an error during the conversion.
   */
  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    if ( o instanceof Font == false ) {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a Font." );
    }
    final Font font = (Font) o;
    final int fontSize = font.getSize();
    final String fontName = font.getName();
    final int fontStyle = font.getStyle();

    return fontName + '-' + styleToString( fontStyle ) + '-' + fontSize;
  }

  private static String styleToString( final int style ) {
    if ( style == 0 ) {
      return "plain";
    }
    if ( style == Font.BOLD ) {
      return "bold";
    }
    if ( style == Font.ITALIC ) {
      return "italic";
    }
    if ( style == ( Font.BOLD | Font.ITALIC ) ) {
      return "bolditalic";
    }
    return "plain";
  }

  /**
   * Converts a string to a property value.
   *
   * @param s
   *          the string.
   * @return a property value.
   * @throws BeanException
   *           if there was an error during the conversion.
   */
  public Object toPropertyValue( final String s ) throws BeanException {
    if ( s == null ) {
      throw new NullPointerException();
    }
    return Font.decode( s );
  }
}
