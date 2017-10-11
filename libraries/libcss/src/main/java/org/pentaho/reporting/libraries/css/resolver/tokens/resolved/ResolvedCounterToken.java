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

package org.pentaho.reporting.libraries.css.resolver.tokens.resolved;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CounterToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.types.TextType;

/**
 * Creation-Date: 12.06.2006, 14:38:29
 *
 * @author Thomas Morgner
 */
public class ResolvedCounterToken implements TextType {
  private CounterToken parent;
  private int counterValue;

  public ResolvedCounterToken( final CounterToken parent,
                               final int counterValue ) {
    this.parent = parent;
    this.counterValue = counterValue;
  }

  public CounterToken getParent() {
    return parent;
  }

  public String getText() {
    final CounterToken counterToken = getParent();
    final CounterStyle style = counterToken.getStyle();
    return style.getCounterValue( counterValue );
  }

  public int getCounterValue() {
    return counterValue;
  }
}
