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
import org.pentaho.reporting.libraries.css.resolver.tokens.computed.CountersToken;
import org.pentaho.reporting.libraries.css.resolver.tokens.types.TextType;


/**
 * Creation-Date: 12.06.2006, 14:38:29
 *
 * @author Thomas Morgner
 */
public class ResolvedCountersToken implements TextType {
  private CountersToken parent;
  private int[] counterValues;

  public ResolvedCountersToken( final CountersToken parent,
                                final int[] counterValues ) {
    this.parent = parent;
    this.counterValues = counterValues;
  }

  public CountersToken getParent() {
    return parent;
  }

  public String getText() {
    final CountersToken counterToken = getParent();
    final CounterStyle style = counterToken.getStyle();
    final String separator = counterToken.getSeparator();
    final StringBuffer buffer = new StringBuffer();

    for ( int i = 0; i < counterValues.length; i++ ) {
      if ( i != 0 ) {
        buffer.append( separator );
      }
      final int value = counterValues[ i ];
      buffer.append( style.getCounterValue( value ) );
    }

    return buffer.toString();
  }

  public int[] getCounterValue() {
    return (int[]) counterValues.clone();
  }
}
