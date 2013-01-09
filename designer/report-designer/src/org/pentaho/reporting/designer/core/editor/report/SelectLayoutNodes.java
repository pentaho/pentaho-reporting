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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.report;

import java.util.HashSet;

import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public class SelectLayoutNodes extends IterateSimpleStructureProcessStep
{
  private HashSet<InstanceID> ids;
  private StrictBounds bounds;

  public SelectLayoutNodes()
  {
  }

  public StrictBounds select(final HashSet<InstanceID> ids,
                             final LogicalPageBox box,
                             final Section section)
  {
    if (ids == null)
    {
      throw new NullPointerException();
    }
    this.ids = ids;
    this.bounds = null;
    startProcessing(box);
    if (this.bounds == null)
    {
      DebugLog.log("Failed to collect bounds for " + section);
      return new StrictBounds();
    }
    return this.bounds;
  }

  private boolean isValidDrawTarget(RenderNode node)
  {
    while (node != null)
    {
      if (ids.contains(node.getInstanceId()))
      {
        return true;
      }
      node = node.getParent();
    }
    return false;
  }

  protected void processOtherNode(final RenderNode node)
  {
    if (isValidDrawTarget(node))
    {
      if (bounds == null)
      {
        bounds = new StrictBounds(node.getX(), node.getY(), node.getWidth(), node.getHeight());
      }
      else
      {
        bounds.add(node.getX(), node.getY(), node.getWidth(), node.getHeight());
      }
    }
  }

  protected boolean startBox(final RenderBox box)
  {
    if (isValidDrawTarget(box))
    {
      if (bounds == null)
      {
        bounds = new StrictBounds(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      }
      else
      {
        bounds.add(box.getX(), box.getY(), box.getWidth(), box.getHeight());
      }
    }
    return true;
  }
}
