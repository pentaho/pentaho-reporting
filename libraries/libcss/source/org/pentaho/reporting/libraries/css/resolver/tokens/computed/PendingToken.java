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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.tokens.computed;

/**
 * The pending function. This is a lookup to the current pending context.
 * If the pending context is empty, the element is not displayed (as if it
 * had been declared 'display: none'.
 *
 * The elements get removed from the normal flow and get added to the pending
 * flow. Due to the highly volatile nature of that step, no - I repeat - no
 * validation is done to normalize inline and block elements.
 *
 * @author Thomas Morgner
 */
public class PendingToken extends ComputedToken
{
  private String key;

  public PendingToken(final String key)
  {
    this.key = key;
  }

  public String getKey()
  {
    return key;
  }
}
