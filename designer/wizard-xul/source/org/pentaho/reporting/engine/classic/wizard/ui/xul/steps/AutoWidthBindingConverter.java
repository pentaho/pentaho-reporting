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

package org.pentaho.reporting.engine.classic.wizard.ui.xul.steps;

import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.ui.xul.binding.BindingConvertor;

public class AutoWidthBindingConverter extends BindingConvertor<Length, Boolean>
{
  private Length defaultWidth;

  public AutoWidthBindingConverter(final Length defaultWidth)
  {
    super();
    this.defaultWidth = defaultWidth;
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
   */
  @Override
  public Boolean sourceToTarget(final Length value)
  {
    return value == null;
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
   */
  @Override
  public Length targetToSource(final Boolean value)
  {
    return value ? null : defaultWidth;
  }

}
