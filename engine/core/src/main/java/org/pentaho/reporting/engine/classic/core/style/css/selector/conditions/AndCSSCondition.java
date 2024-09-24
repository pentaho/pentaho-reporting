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
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;

/**
 * Creation-Date: 24.11.2005, 19:45:12
 *
 * @author Thomas Morgner
 */
public final class AndCSSCondition implements CombinatorCondition, CSSCondition {
  private CSSCondition firstCondition;
  private CSSCondition secondCondition;

  public AndCSSCondition( final CSSCondition firstCondition, final CSSCondition secondCondition ) {
    if ( firstCondition == null ) {
      throw new NullPointerException();
    }
    if ( secondCondition == null ) {
      throw new NullPointerException();
    }
    this.firstCondition = firstCondition;
    this.secondCondition = secondCondition;
  }

  /**
   * Returns the first condition.
   */
  public Condition getFirstCondition() {
    return firstCondition;
  }

  /**
   * Returns the second condition.
   */
  public Condition getSecondCondition() {
    return secondCondition;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return Condition.SAC_AND_CONDITION;
  }

  public String print( final NamespaceCollection namespaces ) {
    return firstCondition.print( namespaces ) + secondCondition.print( namespaces );
  }
}
