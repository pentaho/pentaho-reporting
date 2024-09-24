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

package org.pentaho.reporting.libraries.css.keys.list;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 01.12.2005, 19:15:04
 *
 * @author Thomas Morgner
 */
public class ListStyleKeys {
  public static final StyleKey LIST_STYLE_IMAGE =
    StyleKeyRegistry.getRegistry().createKey
      ( "list-style-image", false, false,
        StyleKey.DOM_ELEMENTS | StyleKey.PSEUDO_MARKER );
  public static final StyleKey LIST_STYLE_TYPE =
    StyleKeyRegistry.getRegistry().createKey
      ( "list-style-type", false, false,
        StyleKey.DOM_ELEMENTS | StyleKey.PSEUDO_MARKER );
  public static final StyleKey LIST_STYLE_POSITION =
    StyleKeyRegistry.getRegistry().createKey
      ( "list-style-position", false, false,
        StyleKey.DOM_ELEMENTS | StyleKey.PSEUDO_MARKER );


  private ListStyleKeys() {
  }
}
