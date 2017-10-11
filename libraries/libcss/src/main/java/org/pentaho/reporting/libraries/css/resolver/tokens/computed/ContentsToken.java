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

package org.pentaho.reporting.libraries.css.resolver.tokens.computed;

import org.pentaho.reporting.libraries.css.resolver.tokens.ContentToken;

/**
 * This is a simple placeholder to mark the location where the DOM content should be inserted.
 * <p/>
 * On 'string(..)' functions, this is the place holder where the PCDATA of that element is copied into the string.
 * <p/>
 * Todo: Maybe we should allow to copy the whole contents, as we would for the move-to function.
 */
public class ContentsToken extends ComputedToken {
  public static final ContentToken CONTENTS = new ContentsToken();

  private ContentsToken() {
  }
}
