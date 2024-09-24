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
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.NegativeCondition;

public class NegativeCSSCondition implements NegativeCondition, CSSCondition {
  private CSSCondition condition;

  public NegativeCSSCondition( final CSSCondition condition ) {
    if ( condition == null ) {
      throw new NullPointerException();
    }
    this.condition = condition;
  }

  public Condition getCondition() {
    return condition;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_NEGATIVE_CONDITION;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final NegativeCSSCondition that = (NegativeCSSCondition) o;

    if ( !condition.equals( that.condition ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return condition.hashCode();
  }

  public String print( final NamespaceCollection namespaces ) {
    return "not(" + condition.print( namespaces ) + ")";
  }
}
