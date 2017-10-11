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

package org.pentaho.reporting.libraries.css.keys.canvas;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * All kind of StyleKeys needed for compatiblity with the old display model. This should be moved into the reporting
 * engine itself.
 *
 * @author Thomas Morgner
 */
public class CanvasStyleKeys {
  public static final StyleKey POSITION_X =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-reporting-x-position", true, false, StyleKey.DOM_ELEMENTS );

  public static final StyleKey POSITION_Y =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-reporting-y-position", true, true, StyleKey.DOM_ELEMENTS );

  private CanvasStyleKeys() {
  }

}
