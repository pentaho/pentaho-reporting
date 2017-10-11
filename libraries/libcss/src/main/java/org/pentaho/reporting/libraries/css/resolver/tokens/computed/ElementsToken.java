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

/**
 * The elemnts function. This is a lookup to the current pending context. The elements function grabs the last value
 * from the pending context and drops all previous elements. If the pending context is empty, it preserves its content.
 * <p/>
 * The elements get removed from the normal flow and get added to the pending flow. Due to the highly volatile nature of
 * that step, no - I repeat - no validation is done to normalize inline and block elements.
 *
 * @author Thomas Morgner
 */
public class ElementsToken extends ComputedToken {
  private String key;

  public ElementsToken( final String key ) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
