/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.keys.font;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * http://www.w3.org/TR/css3-fonts/
 *
 * @author Thomas Morgner
 */
public class FontStyleKeys {
  /**
   * Font-Effects are not used yet. It should be implemented later, if possible.
   */
  public static final StyleKey FONT_EFFECT =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-effect", false, true, StyleKey.ALWAYS );

  /**
   * Font-Emphasize is not used yet. It is needed for proper Asian font support.
   */
  public static final StyleKey FONT_EMPHASIZE_STYLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-emphasize-style", false, true, StyleKey.ALWAYS );

  /**
   * Font-Emphasize is not used yet. It is needed for proper Asian font support.
   */
  public static final StyleKey FONT_EMPHASIZE_POSITION =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-emphasize-position", false, true, StyleKey.ALWAYS );
  /**
   * The font-family holds the fully resolved name of an valid font. The font-family value may be null, if the specified
   * font resolved to 'none'.
   */
  public static final StyleKey FONT_FAMILY =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-family", false, true, StyleKey.ALWAYS );

  /**
   * The font-size holds the size of the font in points.
   */
  public static final StyleKey FONT_SIZE =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-size", false, true, StyleKey.ALWAYS );

  /**
   * The font-size-adjust is not used for now.
   */
  public static final StyleKey FONT_SIZE_ADJUST =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-size-adjust", false, true, StyleKey.ALWAYS );

  /**
   * The font-smooth controls the anti-aliasing for the rendering process. This may affect the rendered font size.
   * Resolving the font-smooth property must be done elsewhere (translating auto, never, always or the size
   * specifications into a boolean).
   */
  public static final StyleKey FONT_SMOOTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-smooth", false, true, StyleKey.ALWAYS );

  public static final StyleKey FONT_STYLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-style", false, true, StyleKey.ALWAYS );

  public static final StyleKey FONT_VARIANT =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-variant", false, true, StyleKey.ALWAYS );

  public static final StyleKey FONT_WEIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-weight", false, true, StyleKey.ALWAYS );

  public static final StyleKey FONT_STRETCH =
    StyleKeyRegistry.getRegistry().createKey
      ( "font-stretch", false, true, StyleKey.ALWAYS );
  /**
   * Used in conjunction with text-align-last: size;
   */
  public static final StyleKey MIN_FONT_SIZE =
    StyleKeyRegistry.getRegistry().createKey
      ( "min-font-size", false, true, StyleKey.ALWAYS );
  /**
   * Used in conjunction with text-align-last: size;
   */
  public static final StyleKey MAX_FONT_SIZE =
    StyleKeyRegistry.getRegistry().createKey
      ( "max-font-size", false, true, StyleKey.ALWAYS );

  //// Later, if we ever implement smart font systems.
  //  /**
  //   * If used in a @font rule, this defines an unique name/handle for a font
  //   * definition. If used in any other style rule, this references to a previously
  //   * defined font. If that font is not defined, the key is ignored.
  //   * <p/>
  //   * This is another idea stolen from OpenOffice :)
  //   */
  //  public static final StyleKey FONT_NAME =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-font-name", false, true, StyleKey.ALWAYS);
  //  /**
  //   * A descriptor for @font rules, telling the system that the font either has
  //   * a fixed width for each char ('fixed') or that each char may have a different
  //   * width ('variable').
  //   * <p/>
  //   * This is another idea stolen from OpenOffice :)
  //   */
  //  public static final StyleKey FONT_PITCH =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-font-pitch", false, true, StyleKey.ALWAYS);

  private FontStyleKeys() {
  }


}
