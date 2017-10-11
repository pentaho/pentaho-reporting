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

package org.pentaho.reporting.libraries.css.model;

import java.util.ArrayList;

/**
 * Creation-Date: 23.11.2005, 11:00:04
 *
 * @author Thomas Morgner
 */
public class CSSMediaRule extends CSSDeclarationRule {
  private ArrayList rules;

  public CSSMediaRule( final StyleSheet parentStyle,
                       final StyleRule parentRule ) {
    super( parentStyle, parentRule );
    this.rules = new ArrayList();
  }

  public void addRule( final StyleRule rule ) {
    rules.add( rule );
  }

  public void insertRule( final int index, final StyleRule rule ) {
    rules.add( index, rule );
  }

  public void deleteRule( final int index ) {
    rules.remove( index );
  }

  public int getRuleCount() {
    return rules.size();
  }

  public StyleRule getRule( int index ) {
    return (StyleRule) rules.get( index );
  }
}
