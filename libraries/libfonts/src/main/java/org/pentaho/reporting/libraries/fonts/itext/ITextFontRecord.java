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

package org.pentaho.reporting.libraries.fonts.itext;

import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontType;

/**
 * Creation-Date: 20.07.2007, 19:59:37
 *
 * @author Thomas Morgner
 */
public class ITextFontRecord implements FontRecord, FontIdentifier {
  private FontFamily fontFamily;
  private boolean bold;
  private boolean italic;
  private boolean oblique;

  public ITextFontRecord( final FontFamily fontFamily,
                          final boolean bold,
                          final boolean italic,
                          final boolean oblique ) {
    if ( fontFamily == null ) {
      throw new NullPointerException();
    }
    this.fontFamily = fontFamily;
    this.bold = bold;
    this.italic = italic;
    this.oblique = oblique;
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
    return oblique;
  }

  public FontIdentifier getIdentifier() {
    return this;
  }

  public boolean isScalable() {
    return true;
  }

  public FontType getFontType() {
    return FontType.OTHER;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ITextFontRecord that = (ITextFontRecord) o;

    if ( bold != that.bold ) {
      return false;
    }
    if ( italic != that.italic ) {
      return false;
    }
    if ( oblique != that.oblique ) {
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
    result = 29 * result + ( italic ? 1 : 0 );
    result = 29 * result + ( oblique ? 1 : 0 );
    return result;
  }
}
