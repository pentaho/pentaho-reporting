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

package org.pentaho.reporting.libraries.fonts.monospace;

import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

/**
 * Creation-Date: 13.05.2007, 13:14:16
 *
 * @author Thomas Morgner
 */
public class MonospaceFontRecord implements FontRecord, FontIdentifier {
  private MonospaceFontFamily fontFamily;
  private boolean bold;
  private boolean italics;

  public MonospaceFontRecord( final MonospaceFontFamily fontFamily, final boolean bold, final boolean italics ) {
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
    return false;
  }

  /**
   * Returns tue, if this font's italic mode is in fact some sort of being oblique. An oblique font's glyphs are
   * sheared, but they are not made to look more script like.
   *
   * @return true, if the font is oblique. All italic fonts are also oblique.
   */
  public boolean isOblique() {
    return italics;
  }

  /**
   * Defines, whether the font identifier represents a scalable font type. Such fonts usually create one font metric
   * object for each physical font, and apply the font size afterwards.
   *
   * @return true, if the font is scalable, false otherwise
   */
  public boolean isScalable() {
    return false;
  }

  /**
   * Returns the general type of this font identifier. This is for debugging, not for the real world.
   *
   * @return
   */
  public FontType getFontType() {
    return FontType.MONOSPACE;
  }

  public FontIdentifier getIdentifier() {
    return this;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final MonospaceFontRecord that = (MonospaceFontRecord) o;

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
