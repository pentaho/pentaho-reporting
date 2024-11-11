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


package org.pentaho.reporting.libraries.fonts.pfm;

import org.pentaho.reporting.libraries.fonts.registry.DefaultFontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontSource;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

/**
 * Creation-Date: 21.07.2007, 19:12:20
 *
 * @author Thomas Morgner
 */
public class PfmFontRecord implements FontSource, FontIdentifier {
  private DefaultFontFamily fontFamily;
  private boolean bold;
  private boolean italic;
  private String fontFile;
  private boolean embeddable;

  public PfmFontRecord( final PfmFont font, final DefaultFontFamily fontFamily ) {
    this.embeddable = font.isEmbeddable();
    this.fontFile = font.getFilename();
    this.fontFamily = fontFamily;
    final PfmFontHeader header = font.getHeader();
    this.bold = header.getWeight() > 400;
    this.italic = header.isItalic();
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
    return FontType.PFM;
  }
}
