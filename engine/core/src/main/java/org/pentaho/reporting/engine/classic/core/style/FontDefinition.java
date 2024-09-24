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

package org.pentaho.reporting.engine.classic.core.style;

import java.awt.Font;
import java.io.Serializable;

import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * The FontDefinition encapsulates all Font-Style information. The java.awt.Font class does not support extended Styles
 * like Strikethrough or Underline or font metadata like the base encoding.
 *
 * @author Thomas Morgner
 * @deprecated use single properties instead.
 */
@SuppressWarnings( "deprecation" )
public class FontDefinition implements Serializable, Cloneable {
  /**
   * a constant to draw a font in bold style.
   */
  public static final boolean BOLD = true;

  /**
   * a constant to draw a font in italic style.
   */
  public static final boolean ITALIC = true;

  /**
   * a constant to draw a font with underline style.
   */
  public static final boolean UNDERLINE = true;

  /**
   * a constant to draw a font with strikethrough style.
   */
  public static final boolean STRIKETHROUGH = true;

  /**
   * a constant to draw a font in plain style.
   */
  public static final boolean PLAIN = false;

  /**
   * the preferred text encoding for this font.
   */
  private String fontEncoding;

  /**
   * The FontName of the font. This defines the Font-Family.
   */
  private String fontName;

  /**
   * the font size in point.
   */
  private int fontSize;

  /**
   * this font's bold flag.
   */
  private boolean isBold;

  /**
   * this font's italic flag.
   */
  private boolean isItalic;

  /**
   * this font's underline flag.
   */
  private boolean isUnderline;

  /**
   * this font's strikethrough flag.
   */
  private boolean isStrikeThrough;

  /**
   * the AWT-Font represented by this font definition.
   */
  private transient Font font;

  /**
   * whether to embedd the font in the target documents, if supported.
   */
  private boolean embeddedFont;

  /**
   * a cached hashcode.
   */
  private transient int hashCode;

  // color is defined elsewhere

  /**
   * Creates a font definition using the given name and size and with the given styles defined.
   *
   * @param fontName
   *          the font name used in this font definition.
   * @param fontSize
   *          the font size for the defined font.
   * @param bold
   *          true, if the font should be bold, false otherwise
   * @param italic
   *          true, if the font should be italic, false otherwise
   * @param underline
   *          true, if the font should be drawn with underline style, false otherwise
   * @param strikeThrough
   *          true, if the font should be drawn with strikethrough style, false otherwise
   * @param encoding
   *          the default text encoding that should be used with this font.
   * @param embedded
   *          whether this font should be embedded in the target document.
   */
  public FontDefinition( final String fontName, final int fontSize, final boolean bold, final boolean italic,
      final boolean underline, final boolean strikeThrough, final String encoding, final boolean embedded ) {
    if ( fontName == null ) {
      throw new NullPointerException( "FontName must not be null" );
    }
    if ( fontSize <= 0 ) {
      throw new IllegalArgumentException( "FontSize must be greater than 0" );
    }
    this.fontName = fontName;
    this.fontSize = fontSize;
    isBold = bold;
    isItalic = italic;
    isUnderline = underline;
    isStrikeThrough = strikeThrough;
    this.fontEncoding = encoding;
    this.embeddedFont = embedded;
  }

  /**
   * Creates a font definition using the given name and size and with the given styles defined.
   *
   * @param fontName
   *          the font name used in this font definition.
   * @param fontSize
   *          the font size for the defined font.
   * @param bold
   *          true, if the font should be bold, false otherwise
   * @param italic
   *          true, if the font should be italic, false otherwise
   * @param underline
   *          true, if the font should be drawn with underline style, false otherwise
   * @param strikeThrough
   *          true, if the font should be drawn with strikethrough style, false otherwise
   */
  public FontDefinition( final String fontName, final int fontSize, final boolean bold, final boolean italic,
      final boolean underline, final boolean strikeThrough ) {
    this( fontName, fontSize, bold, italic, underline, strikeThrough, null, false );
  }

  /**
   * Creates a font definition using the given name and size and with no additional style enabled.
   *
   * @param fontName
   *          the font name used in this font definition.
   * @param fontSize
   *          the font size for the defined font.
   */
  public FontDefinition( final String fontName, final int fontSize ) {
    this( fontName, fontSize, false, false, false, false, null, false );
  }

  /**
   * Creates a font definition base on the given AWT font.
   *
   * @param font
   *          the awt font that should be used as definition source.
   */
  public FontDefinition( final Font font ) {
    this( font.getName(), font.getSize(), font.isBold(), font.isItalic(), false, false, null, false );
  }

  /**
   * Returns the font name of this font definition. The font name is the font face name.
   *
   * @return the name of the font.
   */
  public String getFontName() {
    return fontName;
  }

  /**
   * Returns the size of the defined font.
   *
   * @return the font size in points.
   */
  public int getFontSize() {
    return fontSize;
  }

  /**
   * Returns whether the font should be embedded in the target document.
   *
   * @return true, if the font should be embedded.
   */
  public boolean isEmbeddedFont() {
    return embeddedFont;
  }

  /**
   * Returns the bold style of this font definition.
   *
   * @return true, if the font should be drawn in bold style.
   */
  public boolean isBold() {
    return isBold;
  }

  /**
   * Returns the italic style of this font definition.
   *
   * @return true, if the font should be drawn in italic style.
   */
  public boolean isItalic() {
    return isItalic;
  }

  /**
   * Returns the underline style of this font definition.
   *
   * @return true, if the font should be drawn in underline style.
   */
  public boolean isUnderline() {
    return isUnderline;
  }

  /**
   * Returns the strikethrough style of this font definition.
   *
   * @return true, if the font should be drawn in strikethrough style.
   */
  public boolean isStrikeThrough() {
    return isStrikeThrough;
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException
   *           if a error occured during cloning .
   * @see java.lang.Cloneable
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Returns the AWT StyleInformation, only Bold and Italic are encoding in the return value.
   *
   * @return the AWT-compatible style information.
   */
  private int getFontStyle() {
    int fontstyle = Font.PLAIN;
    if ( isBold() ) {
      fontstyle += Font.BOLD;
    }
    if ( isItalic() ) {
      fontstyle += Font.ITALIC;
    }
    return fontstyle;
  }

  /**
   * returns the AWT-Font defined by this FontDefinition.
   *
   * @return the AWT font.
   */
  public Font getFont() {
    if ( font == null ) {
      // noinspection MagicConstant
      font = new Font( getFontName(), getFontStyle(), getFontSize() );
    }
    return font;
  }

  /**
   * Returns this fonts string encoding. If the font does not define an encoding, the given default encoding is
   * returned.
   *
   * @param defaultEncoding
   *          the font encoding to be used if this font definition does not define an own encoding.
   * @return the font encoding or the default encoding.
   */
  public String getFontEncoding( final String defaultEncoding ) {
    if ( this.fontEncoding == null ) {
      return defaultEncoding;
    } else {
      return fontEncoding;
    }
  }

  /**
   * Returns a string representation of this font definition.
   *
   * @return a string representation of this font definition.
   */
  public String toString() {
    final StringBuilder buffer = new StringBuilder();
    buffer.append( "FontDefinition='fontname=\"" );
    buffer.append( fontName );
    buffer.append( "\"; fontSize=" );
    buffer.append( fontSize );
    buffer.append( "; bold=" );
    buffer.append( isBold );
    buffer.append( "; italic=" );
    buffer.append( isItalic );
    buffer.append( "; underline=" );
    buffer.append( isUnderline );
    buffer.append( "; strike=" );
    buffer.append( isStrikeThrough );
    buffer.append( "; embedded=" );
    buffer.append( embeddedFont );
    buffer.append( '\'' );
    return buffer.toString();
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o
   *          the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
   * @see #hashCode()
   * @see java.util.Hashtable
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof FontDefinition ) ) {
      return false;
    }

    final FontDefinition definition = (FontDefinition) o;

    if ( embeddedFont != definition.embeddedFont ) {
      return false;
    }
    if ( fontSize != definition.fontSize ) {
      return false;
    }
    if ( isBold != definition.isBold ) {
      return false;
    }
    if ( isItalic != definition.isItalic ) {
      return false;
    }
    if ( isStrikeThrough != definition.isStrikeThrough ) {
      return false;
    }
    if ( isUnderline != definition.isUnderline ) {
      return false;
    }
    if ( !fontName.equals( definition.fontName ) ) {
      return false;
    }
    if ( fontEncoding != null ? !fontEncoding.equals( definition.fontEncoding ) : definition.fontEncoding != null ) {
      return false;
    }

    return true;
  }

  /**
   * Returns a hash code value for the object. This method is supported for the benefit of hashtables such as those
   * provided by <code>java.util.Hashtable</code>.
   *
   * @return a hash code value for this object.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public int hashCode() {
    if ( hashCode == 0 ) {
      int result = ( fontEncoding != null ? fontEncoding.hashCode() : 0 );
      result = 29 * result + fontName.hashCode();
      result = 29 * result + fontSize;
      result = 29 * result + ( isBold ? 1 : 0 );
      result = 29 * result + ( isItalic ? 1 : 0 );
      result = 29 * result + ( isUnderline ? 1 : 0 );
      result = 29 * result + ( isStrikeThrough ? 1 : 0 );
      result = 29 * result + ( embeddedFont ? 1 : 0 );
      hashCode = result;
    }
    return hashCode;
  }

  /**
   * Returns true if the logical font name is equivalent to 'SansSerif', and false otherwise.
   *
   * @return true or false.
   */
  public boolean isSansSerif() {
    return StringUtils.startsWithIgnoreCase( fontName, "SansSerif" ) // NON-NLS
        || StringUtils.startsWithIgnoreCase( fontName, "Dialog" ) // NON-NLS
        || StringUtils.startsWithIgnoreCase( fontName, "SanSerif" ); // NON-NLS
    // is it a bug? Somewhere in the JDK this name is used (typo, but heck, we accept it anyway).
  }

  /**
   * Returns true if the logical font name is equivalent to 'Courier', and false otherwise.
   *
   * @return true or false.
   */
  public boolean isCourier() {
    return ( StringUtils.startsWithIgnoreCase( fontName, "dialoginput" ) // NON-NLS
      || StringUtils.startsWithIgnoreCase( fontName, "monospaced" ) ); // NON-NLS
  }

  /**
   * Returns true if the logical font name is equivalent to 'Serif', and false otherwise.
   *
   * @return true or false.
   */
  public boolean isSerif() {
    return ( StringUtils.startsWithIgnoreCase( fontName, "serif" ) ); // NON-NLS
  }

}
