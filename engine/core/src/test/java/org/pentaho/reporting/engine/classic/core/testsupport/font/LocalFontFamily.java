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

package org.pentaho.reporting.engine.classic.core.testsupport.font;

import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;

/**
 * Creation-Date: 16.12.2005, 20:44:11
 *
 * @author Thomas Morgner
 */
public class LocalFontFamily implements FontFamily {
  private String fontName;
  private LocalFontRecord[] fonts;

  public LocalFontFamily( final String fontName ) {
    this.fontName = fontName;
    this.fonts = new LocalFontRecord[4];
  }

  /**
   * Returns the name of the font family (in english).
   *
   * @return
   */
  public String getFamilyName() {
    return fontName;
  }

  public String[] getAllNames() {
    return new String[] { fontName };
  }

  /**
   * This selects the most suitable font in that family. Italics fonts are preferred over oblique fonts.
   *
   * @param bold
   * @param italics
   * @return
   */
  public FontRecord getFontRecord( final boolean bold, final boolean italics ) {

    int index = 0;
    if ( bold ) {
      index += 1;
    }
    if ( italics ) {
      index += 2;
    }
    return fonts[index];
  }

  public void setFontRecord( final boolean bold, final boolean italics, final String sourceFile ) {
    int index = 0;
    if ( bold ) {
      index += 1;
    }
    if ( italics ) {
      index += 2;
    }
    fonts[index] = new LocalFontRecord( this, sourceFile, bold, italics );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final LocalFontFamily that = (LocalFontFamily) o;

    if ( !fontName.equals( that.fontName ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return fontName.hashCode();
  }
}
