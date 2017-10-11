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

package org.pentaho.reporting.libraries.base.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Class which holds a static reference to a set of empty objects. This is created for performance reasons. Using this
 * class will prevent creating duplicated "empty" object.
 *
 * @author David Kincade
 */
public final class Empty {

  /**
   * No reason to create an instance of this class.
   */
  private Empty() {
  }

  /**
   * The empty string.
   */
  public static final String STRING = "";

  /**
   * An empty array of Strings.
   */
  public static final String[] STRING_ARRAY = new String[ 0 ];

  /**
   * An empty Map. (Collections.EMPTY_MAP is not available until JDK 1.4)
   *
   * @deprecated this is a redeclaration of the Collections.EMPTY_MAP field and should be killed.
   */
  @SuppressWarnings( "PublicStaticCollectionField" )
  public static final Map MAP = Collections.EMPTY_MAP;

  /**
   * An empty List.
   *
   * @noinspection PublicStaticCollectionField
   * @deprecated this is a redeclaration of the Collections.EMPTY_LIST field and should be killed.
   */
  public static final List LIST = Collections.EMPTY_LIST;
}
