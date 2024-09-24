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

package org.pentaho.reporting.libraries.css.keys.content;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * http://www.w3.org/TR/css3-content/
 *
 * @author Thomas Morgner
 */
public class ContentStyleKeys {
  public static final StyleKey MOVE_TO =
    StyleKeyRegistry.getRegistry().createKey
      ( "move-to", false, false,
        StyleKey.DOM_ELEMENTS |
          StyleKey.PSEUDO_BEFORE |
          StyleKey.PSEUDO_ALTERNATE |
          StyleKey.PSEUDO_AFTER );

  public static final StyleKey QUOTES =
    StyleKeyRegistry.getRegistry().createKey
      ( "quotes", false, false,
        StyleKey.All_ELEMENTS |
          StyleKey.MARGINS |
          StyleKey.FOOTNOTE_AREA );

  public static final StyleKey COUNTER_INCREMENT =
    StyleKeyRegistry.getRegistry().createKey
      ( "counter-increment", false, false, StyleKey.ALWAYS );

  public static final StyleKey COUNTER_RESET =
    StyleKeyRegistry.getRegistry().createKey
      ( "counter-reset", false, false, StyleKey.ALWAYS );

  /**
   * string-set: <name> <value>
   */
  public static final StyleKey STRING_SET =
    StyleKeyRegistry.getRegistry().createKey
      ( "string-set", false, false, StyleKey.ALWAYS );
  /**
   * Defines a new string context. This is equal to the counter-reset property, and allows to apply the counter nesting
   * rules to strings. This does *not* define the string content; so you have to add a string-set property as well.
   * <p/>
   * The string-def property is always evaulated before the string-set property gets processed.
   * <p/>
   * The format for this property is simple: -x-pentaho-css-string-def: <name>
   */
  public static final StyleKey STRING_DEFINE =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-string-def", false, false, StyleKey.ALWAYS );

  /**
   * Alternate text for images or other non-displayable content. This is not the same as the ::alternate pseudo-element
   * that gets inserted if content had been moved away.
   */
  public static final StyleKey ALTERNATE_TEXT =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-alternate-text", false, false, StyleKey.ALWAYS );

  public static final StyleKey CONTENT =
    StyleKeyRegistry.getRegistry().createKey
      ( "content", false, false, StyleKey.ALWAYS );

  private ContentStyleKeys() {
  }
}
