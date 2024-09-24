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

package org.pentaho.reporting.libraries.css.keys.hyperlinks;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Creation-Date: 24.11.2005, 17:25:02
 *
 * @author Thomas Morgner
 */
public class HyperlinkStyleKeys {
  public static final StyleKey TARGET_NAME =
    StyleKeyRegistry.getRegistry().createKey
      ( "target-name", false, false, StyleKey.All_ELEMENTS );
  public static final StyleKey TARGET_NEW =
    StyleKeyRegistry.getRegistry().createKey
      ( "target-new", false, false, StyleKey.All_ELEMENTS );
  public static final StyleKey TARGET_POSITION =
    StyleKeyRegistry.getRegistry().createKey
      ( "target-position", false, false, StyleKey.All_ELEMENTS );

  //// This stuff is output specific and therefore must be declared in the processor itself
  //// Such style-keys will be implemented in the Classic-Engine itself
  //  /**
  //   * This is a libLayout extension to allow a document independent
  //   * link specification. It is up to the output target to support that.
  //   * <p/>
  //   * Example style definition:  a { -x-pentaho-css-href-target: attr("href"); }
  //   */
  //  public static final StyleKey HREF_TARGET =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-href-target", true, false, StyleKey.All_ELEMENTS);
  //  /**
  //   * This is a libLayout extension to allow a document independent
  //   * anchor specifications. It is up to the output target to support that.
  //   * <p/>
  //   * Example style definition:  a { x-href-anchor: attr("name"); }
  //   */
  //  public static final StyleKey ANCHOR =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-href-anchor", true, false, StyleKey.All_ELEMENTS);

  private HyperlinkStyleKeys() {
  }
}
