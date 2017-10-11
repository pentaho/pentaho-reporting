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

package org.pentaho.reporting.libraries.css.keys.color;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 30.10.2005, 18:47:30
 *
 * @author Thomas Morgner
 */
public final class ColorStyleKeys {
  public static final StyleKey COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "color", false, true, StyleKey.ALWAYS );

  /**
   * Not sure whether we can implement this one. It is a post-processing operation, and may or may not be supported by
   * the output target.
   */
  public static final StyleKey OPACITY =
    StyleKeyRegistry.getRegistry().createKey
      ( "opacity", false, false, StyleKey.ALWAYS );

  /**
   * For now, we do not care about color profiles. This might have to do with me being clueless about the topic, but
   * also with the cost vs. usefullness calculation involved.
   */
  public static final StyleKey COLOR_PROFILE =
    StyleKeyRegistry.getRegistry().createKey
      ( "color-profile", false, true, StyleKey.ALWAYS );
  public static final StyleKey RENDERING_INTENT =
    StyleKeyRegistry.getRegistry().createKey
      ( "rendering-intent", false, true, StyleKey.ALWAYS );

  private ColorStyleKeys() {
  }
}
