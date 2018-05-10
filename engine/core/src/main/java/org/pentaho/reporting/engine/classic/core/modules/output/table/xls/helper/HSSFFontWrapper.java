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
 * Copyright (c) 2001 - 2018 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Font;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

/**
 * The HSSFFontWrapper is used to store excel style font information.
 *
 * @author Heiko Evermann
 */
public final class HSSFFontWrapper {
  /**
   * scale between Excel and awt. With this value it looks fine.
   *
   * @deprecated No longer used, will be removed in 6.0
   */
  public static final int FONT_FACTOR = 20;

  /**
   * the font name.
   */
  private final String fontName;

  /**
   * the excel color index.
   */
  private final short colorIndex;

  /**
   * the font size.
   */
  private final int fontHeight;

  /**
   * the font's bold flag.
   */
  private final boolean bold;

  /**
   * the font's italic flag.
   */
  private final boolean italic;

  /**
   * the font's underline flag.
   */
  private final boolean underline;

  /**
   * the font's strikethrough flag.
   */
  private final boolean strikethrough;

  /**
   * the cached hashcode.
   */
  private int hashCode;

  public HSSFFontWrapper( final StyleSheet contentStyle, final short colorIndex ) {
    if ( colorIndex < 0 ) {
      throw new IllegalArgumentException( "Negative color index is not allowed" );
    }

    this.fontName = normalizeFontName( (String) contentStyle.getStyleProperty( TextStyleKeys.FONT ) );
    this.fontHeight = contentStyle.getIntStyleProperty( TextStyleKeys.FONTSIZE, 0 );
    this.bold = contentStyle.getBooleanStyleProperty( TextStyleKeys.BOLD );
    this.italic = contentStyle.getBooleanStyleProperty( TextStyleKeys.ITALIC );
    this.underline = contentStyle.getBooleanStyleProperty( TextStyleKeys.UNDERLINED );
    this.strikethrough = contentStyle.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH );
    this.colorIndex = colorIndex;
  }

  /**
   * Creates a new HSSFFontWrapper for the given font and color.
   *
   * @param fontName
   *          the name of the wrapped font.
   * @param fontSize
   *          the name of the wrapped font.
   * @param bold
   *          a font style flag.
   * @param italic
   *          a font style flag.
   * @param underline
   *          a font style flag.
   * @param strikethrough
   *          a font style flag.
   * @param colorIndex
   *          the foreground color.
   */
  public HSSFFontWrapper( final String fontName, final short fontSize, final boolean bold, final boolean italic,
      final boolean underline, final boolean strikethrough, final short colorIndex ) {
    if ( fontName == null ) {
      throw new NullPointerException( "FontDefinition is null" );
    }
    if ( colorIndex < 0 ) {
      throw new IllegalArgumentException( "Negative color index is not allowed" );
    }

    this.fontName = normalizeFontName( fontName );
    this.fontHeight = fontSize;
    this.bold = bold;
    this.italic = italic;
    this.underline = underline;
    this.strikethrough = strikethrough;
    this.colorIndex = colorIndex;
  }

  private String normalizeFontName( final String fontName ) {
    if ( "SansSerif".equalsIgnoreCase( fontName ) || "Dialog".equalsIgnoreCase( fontName ) ) {
      return "Arial";
    } else if ( "Monospace".equalsIgnoreCase( fontName ) ) {
      return "Courier New";
    } else if ( "Serif".equalsIgnoreCase( fontName ) ) {
      return "Times New Roman";
    } else {
      return fontName;
    }
  }

  /**
   * Creates a HSSFFontWrapper for the excel font.
   *
   * @param font
   *          the font.
   */
  public HSSFFontWrapper( final Font font ) {
    if ( font == null ) {
      throw new NullPointerException( "Font is null" );
    }
    if ( font.getColor() < 0 ) {
      throw new IllegalArgumentException( "Negative color index is not allowed" );
    }

    fontName = normalizeFontName( font.getFontName() );
    fontHeight = font.getFontHeightInPoints();
    bold = font.getBold();
    italic = font.getItalic();
    underline = ( font.getUnderline() != HSSFFont.U_NONE );
    strikethrough = font.getStrikeout();
    colorIndex = font.getColor();
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o
   *          the compared object.
   * @return true, if the font wrapper contains the same font definition, false otherwise.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof HSSFFontWrapper ) ) {
      return false;
    }

    final HSSFFontWrapper wrapper = (HSSFFontWrapper) o;

    if ( bold != wrapper.bold ) {
      return false;
    }
    if ( underline != wrapper.underline ) {
      return false;
    }
    if ( strikethrough != wrapper.strikethrough ) {
      return false;
    }
    if ( colorIndex != wrapper.colorIndex ) {
      return false;
    }
    if ( fontHeight != wrapper.fontHeight ) {
      return false;
    }
    if ( italic != wrapper.italic ) {
      return false;
    }
    if ( !fontName.equals( wrapper.fontName ) ) {
      return false;
    }

    return true;
  }

  /**
   * Returns a hash code value for the object. This method is supported for the benefit of hashtables such as those
   * provided by <code>java.util.Hashtable</code>.
   *
   * @return the hash code.
   */
  public int hashCode() {
    if ( hashCode == 0 ) {
      int result = fontName.hashCode();
      result = 29 * result + colorIndex;
      result = 29 * result + fontHeight;
      result = 29 * result + ( bold ? 1 : 0 );
      result = 29 * result + ( italic ? 1 : 0 );
      result = 29 * result + ( underline ? 1 : 0 );
      result = 29 * result + ( strikethrough ? 1 : 0 );
      hashCode = result;
    }
    return hashCode;
  }

  @Override
  public String toString() {
    return "HSSFFontWrapper{" + "fontName='" + fontName + '\'' + ", colorIndex=" + colorIndex + ", fontHeight="
        + fontHeight + ", bold=" + bold + ", italic=" + italic + ", underline=" + underline + ", strikethrough="
        + strikethrough + ", hashCode=" + hashCode + '}';
  }

  public boolean isBold() {
    return bold;
  }

  public short getColorIndex() {
    return colorIndex;
  }

  public int getFontHeight() {
    return fontHeight;
  }

  public String getFontName() {
    return fontName;
  }

  public int getHashCode() {
    return hashCode;
  }

  public boolean isItalic() {
    return italic;
  }

  public boolean isStrikethrough() {
    return strikethrough;
  }

  public boolean isUnderline() {
    return underline;
  }
}
