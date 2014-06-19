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

package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.function.Expression;

/**
 * Simple bean-like class for holding all the information about an attribute change.
*
* @author Thomas Morgner.
*/
public class StyleExpressionChange implements Change
{
  private StyleKey styleKey;
  private Expression oldValue;
  private Expression newValue;

  public StyleExpressionChange(final StyleKey styleKey, final Expression oldValue, final Expression newValue)
  {
    this.styleKey = styleKey;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public StyleKey getStyleKey()
  {
    return styleKey;
  }

  public Object getOldValue()
  {
    return oldValue;
  }

  public Expression getOldExpression()
  {
    return oldValue;
  }

  public Object getNewValue()
  {
    return newValue;
  }

  public Expression getNewExpression()
  {
    return newValue;
  }
}
