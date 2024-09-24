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

package org.pentaho.reporting.libraries.css.keys.internal;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * @author Thomas Morgner
 */
public class InternalStyleKeys {
  /**
   * To which Layouter pseudo-class does this element belong to. A pseudo-class membership is defined by an expression.
   */
  public static final StyleKey PSEUDOCLASS =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-pseudoclass", false, false, StyleKey.All_ELEMENTS );

  /**
   * Which language does the content have? This is an ISO code like 'en' maybe enriched with an country code 'en_US' and
   * variant 'en_US_native'
   */
  public static final StyleKey LANG =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-css-language", true, true, StyleKey.All_ELEMENTS );

  /**
   * A internal key holding the computed content. The value for the key is defined during the resolving and by all
   * means: Do not even think about touching that key without proper purification of your soul! Clueless messing around
   * with this property will kill you.
   */
  public static final StyleKey INTERNAL_CONTENT =
    StyleKeyRegistry.getRegistry().createKey
      ( "-x-pentaho-internal-content", true, false, StyleKey.All_ELEMENTS );

  private InternalStyleKeys() {
  }
}
