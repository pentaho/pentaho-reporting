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

package org.pentaho.reporting.libraries.fonts.afm;

import org.pentaho.reporting.libraries.fonts.registry.DefaultFontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontSource;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

/**
 * Creation-Date: 22.07.2007, 17:19:04
 *
 * @author Thomas Morgner
 */
public class AfmFontRecord implements FontSource, FontIdentifier {
  //  private AfmFont font;
  private DefaultFontFamily fontFamily;
  private boolean bold;
  private boolean italic;
  private String fontFile;
  private boolean embeddable;

  public AfmFontRecord( final AfmFont font,
                        final DefaultFontFamily fontFamily ) {
    if ( font == null ) {
      throw new NullPointerException();
    }
    if ( fontFamily == null ) {
      throw new NullPointerException();
    }
    this.embeddable = font.isEmbeddable();
    this.fontFile = font.getFilename();
    this.fontFamily = fontFamily;
    final AfmHeader header = font.getHeader();
    this.bold = header.getWeight() > 400;
    this.italic = font.getDirectionSection( 0 ).getItalicAngle() != 0;
  }

  public FontFamily getFamily() {
    return fontFamily;
  }

  public boolean isBold() {
    return bold;
  }

  public boolean isItalic() {
    return italic;
  }

  public boolean isOblique() {
    return false;
  }

  public FontIdentifier getIdentifier() {
    return this;
  }

  public String getFontSource() {
    return fontFile;
  }

  public boolean isEmbeddable() {
    return embeddable;
  }

  public boolean isScalable() {
    return true;
  }

  public FontType getFontType() {
    return FontType.AFM;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final AfmFontRecord that = (AfmFontRecord) o;

    if ( bold != that.bold ) {
      return false;
    }
    if ( embeddable != that.embeddable ) {
      return false;
    }
    if ( italic != that.italic ) {
      return false;
    }
    if ( !fontFamily.equals( that.fontFamily ) ) {
      return false;
    }
    if ( !fontFile.equals( that.fontFile ) ) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    int result;
    result = fontFamily.hashCode();
    result = 31 * result + ( bold ? 1 : 0 );
    result = 31 * result + ( italic ? 1 : 0 );
    result = 31 * result + fontFile.hashCode();
    result = 31 * result + ( embeddable ? 1 : 0 );
    return result;
  }
}
