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

import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class WidowCanvasContext implements WidowContext
{
  private StackedObjectPool<WidowCanvasContext> pool;
  private WidowContext parent;

  public WidowCanvasContext()
  {
  }

  public void init(final StackedObjectPool<WidowCanvasContext> pool, final WidowContext parent)
  {
    this.pool = pool;
    this.parent = parent;
  }

  public void startChild(final RenderBox box)
  {

  }

  public void registerFinishedNode(final FinishedRenderNode node)
  {

  }

  public void registerBreakMark(final RenderBox box)
  {

  }

  public void endChild(final RenderBox box)
  {

  }

  public WidowContext commit(final RenderBox box)
  {
    return parent;
  }

  public void subContextCommitted(final RenderBox contextBox)
  {

  }

  public void clearForPooledReuse()
  {
    parent = null;
    pool.free(this);
  }
}
