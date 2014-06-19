/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.process.valign;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;


/**
 * A generic align context for images and other nodes. (Renderable-Content should have been aligned by the parent.
 *
 * @author Thomas Morgner
 */
public final class NodeAlignContext extends AlignContext
{
  private long shift;

  public NodeAlignContext(final RenderNode node)
  {
    super(node);
  }

  public boolean isSimpleNode()
  {
    return true;
  }

  public long getBaselineDistance(final int baseline)
  {
    return 0;
  }

  public void shift(final long delta)
  {
    this.shift += delta;
  }

  public long getAfterEdge()
  {
    return shift;
  }

  public long getBeforeEdge()
  {
    return shift;
  }
}
