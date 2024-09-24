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

package org.pentaho.reporting.engine.classic.core.style.css.selector.conditions;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.w3c.css.sac.PositionalCondition;

public class PositionalCSSCondition implements CSSCondition, PositionalCondition {
  private int position;
  private boolean matchByType;
  private boolean matchByName;

  public PositionalCSSCondition( final int position, final boolean matchByType, final boolean matchByName ) {
    this.position = position;
    this.matchByType = matchByType;
    this.matchByName = matchByName;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_POSITIONAL_CONDITION;
  }

  /**
   * Returns the position in the tree.
   * <p>
   * A negative value means from the end of the child node list.
   * <p>
   * The child node list begins at 0.
   */
  public int getPosition() {
    return position;
  }

  /**
   * <code>true</code> if the child node list only shows nodes of the same type of the selector (only elements, only
   * PIS, ...)
   */
  public boolean getTypeNode() {
    return matchByType;
  }

  /**
   * <code>true</code> if the node should have the same node type (for element, same namespaceURI and same localName).
   */
  public boolean getType() {
    return matchByName;
  }

  public String print( final NamespaceCollection namespaces ) {
    // todo: Not supported by SAC/flute yet
    return "";
  }
}
