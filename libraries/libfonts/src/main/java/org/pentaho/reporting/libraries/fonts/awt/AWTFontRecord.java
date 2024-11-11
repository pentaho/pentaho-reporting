/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.awt;

import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

/**
 * Creation-Date: 16.12.2005, 20:06:51
 *
 * @author Thomas Morgner
 */
public class AWTFontRecord implements FontRecord, FontIdentifier {
  private FontFamily fontFamily;
  private boolean bold;
  private boolean italics;

  public AWTFontRecord( final FontFamily fontFamily,
                        final boolean bold, final boolean italics ) {
    if ( fontFamily == null ) {
      throw new NullPointerException();
    }
    this.fontFamily = fontFamily;
    this.bold = bold;
    this.italics = italics;
  }

  /**
   * Returns the family for this record.
   *
   * @return the font family.
   */
  public FontFamily getFamily() {
    return fontFamily;
  }

  /**
   * Returns true, if this font corresponds to a bold version of the font. A font that does not provide a bold face must
   * emulate the boldness using other means.
   *
   * @return true, if the font provides bold glyphs, false otherwise.
   */
  public boolean isBold() {
    return bold;
  }

  /**
   * Returns true, if this font includes italic glyphs. Italics is different from oblique, as certain glyphs (most
   * notably the lowercase 'f') will have a different appearance, making the font look more like a script font.
   *
   * @return true, if the font is italic.
   */
  public boolean isItalic() {
    return italics;
  }

  /**
   * Returns tue, if this font's italic mode is in fact some sort of being oblique. An oblique font's glyphs are
   * sheared, but they are not made to look more script like.
   *
   * @return true, if the font is oblique.
   */
  public boolean isOblique() {
    return italics;
  }

  public FontIdentifier getIdentifier() {
    return this;
  }

  /**
   * Defines, whether the font identifier represents a scalable font type. Such fonts usually create one font metric
   * object for each physical font, and apply the font size afterwards.
   *
   * @return true, if the font is scalable, false otherwise
   */
  public boolean isScalable() {
    return true;
  }

  public FontType getFontType() {
    return FontType.AWT;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final AWTFontRecord that = (AWTFontRecord) o;

    if ( bold != that.bold ) {
      return false;
    }
    if ( italics != that.italics ) {
      return false;
    }
    if ( !fontFamily.equals( that.fontFamily ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = fontFamily.hashCode();
    result = 29 * result + ( bold ? 1 : 0 );
    result = 29 * result + ( italics ? 1 : 0 );
    return result;
  }
}
