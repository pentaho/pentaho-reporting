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
