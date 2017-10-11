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
 * Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
 */

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
