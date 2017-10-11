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

package org.pentaho.reporting.libraries.fonts.registry;

import java.io.Serializable;

/**
 * Creation-Date: 07.11.2005, 19:07:09
 *
 * @author Thomas Morgner
 */
public interface FontRecord extends Serializable {
  /**
   * Returns the family for this record.
   *
   * @return the font family.
   */
  public FontFamily getFamily();

  /**
   * Returns true, if this font corresponds to a bold version of the font. A font-renderer that renders a font that does
   * not provide a bold face must emulate the boldness using other means.
   * <p/>
   * The font should *not* lie here and say 'true', if it does not contain bold glyphs, as this will make it impossible
   * to differentiate between native bold fonts and fonts for which the renderer will have to provide boldness.
   *
   * @return true, if the font provides bold glyphs, false otherwise.
   */
  public boolean isBold();

  /**
   * Returns true, if this font includes italic glyphs. Italics is different from oblique, as certain glyphs (most
   * notably the lowercase 'f') will have a different appearance, making the font look more like a script font. A
   * font-renderer that renders a font that does not provide an italic face must emulate the italics using other means.
   * <p/>
   * The font should *not* lie here and say 'true', if it does not contain italic glyphs, as this will make it
   * impossible to differentiate between native italics fonts and fonts for which the renderer will have to provide the
   * italics style.
   *
   * @return true, if the font is italic.
   */
  public boolean isItalic();

  /**
   * Returns tue, if this font's italic mode is in fact some sort of being oblique. An oblique font's glyphs are
   * sheared, but they are not made to look more script like. A font-renderer that renders a font that does not provide
   * a oblique face must emulate the oblique-mode using other means.
   * <p/>
   * The font should *not* lie here and say 'true', if it does not contain oblique glyphs, as this will make it
   * impossible to differentiate between native oblique fonts and fonts for which the renderer will have to provide the
   * oblique style.
   *
   * @return true, if the font is oblique. All italic fonts are also oblique.
   */
  public boolean isOblique();

  public FontIdentifier getIdentifier();
}
