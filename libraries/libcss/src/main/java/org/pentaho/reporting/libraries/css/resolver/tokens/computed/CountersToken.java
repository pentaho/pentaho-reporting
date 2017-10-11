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

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

/**
 * This is a meta-token. It must be completly resolved during the ContentNormalization, and must be replaced by a
 * sequence of 'Counter' tokens.
 *
 * @author Thomas Morgner
 */
public class CountersToken extends ComputedToken {
  private String name;
  private String separator;
  private CounterStyle style;

  public CountersToken( final String name,
                        final String separator,
                        final CounterStyle style ) {
    this.name = name;
    this.separator = separator;
    this.style = style;
  }

  public String getSeparator() {
    return separator;
  }

  public String getName() {
    return name;
  }

  public CounterStyle getStyle() {
    return style;
  }
}
