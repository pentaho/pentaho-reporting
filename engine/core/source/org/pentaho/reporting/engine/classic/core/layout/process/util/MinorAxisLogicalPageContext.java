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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;

public class MinorAxisLogicalPageContext extends MinorAxisNodeContext
{
  private long pageWidth;

  public MinorAxisLogicalPageContext(final LogicalPageBox logicalPageBox)
  {
    super(null);
    pageWidth = logicalPageBox.getPageWidth();
  }

  public MinorAxisNodeContext pop()
  {
    return null;
  }

  public long getResolvedPreferredSize()
  {
    return pageWidth;
  }

  public long getBlockContextWidth()
  {
    return pageWidth;
  }
}
