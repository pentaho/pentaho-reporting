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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.process.util.BlockWidowOrphanContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.CanvasWidowOrphanContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PassThroughWidowOrphanContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.WidowOrphanContext;

public class CountWidowOrphanOptInBoxesStep extends IterateSimpleStructureProcessStep
{
  private static final Log logger = LogFactory.getLog(CountWidowOrphanOptInBoxesStep.class);
  private int totalCount;
  private WidowOrphanContext context;

  private WidowOrphanContext create(final RenderBox box)
  {
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
      final int widows = properties.getWidows();
      final int orphans = properties.getOrphans();
      if (widows == 0 && orphans == 0 && properties.isAvoidPagebreakInside() == false)
      {
        final PassThroughWidowOrphanContext retval = new PassThroughWidowOrphanContext();
        retval.init(null, context);
        return retval;
      }
      final BlockWidowOrphanContext blockWidowOrphanContext = new BlockWidowOrphanContext();
      blockWidowOrphanContext.init(null, context, box, widows, orphans);
      return blockWidowOrphanContext;
    }
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_ROW) == LayoutNodeTypes.MASK_BOX_ROW)
    {
      // todo: Make this a row-context later ..
    }

    final CanvasWidowOrphanContext retval = new CanvasWidowOrphanContext();
    retval.init(null, context);
    return retval;
  }

  public CountWidowOrphanOptInBoxesStep()
  {
  }

  public int countChildren(final RenderNode box)
  {
    totalCount = 0;
    if (box instanceof LogicalPageBox)
    {
      final LogicalPageBox box1 = (LogicalPageBox) box;
      processBoxChilds(box1);
    }
    else
    {
      startProcessing(box);
    }
    return totalCount;
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  protected boolean startBox(final RenderBox box)
  {
    final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
    if (properties.isWidowOrphanOptOut() == false)
    {
      context.startChild(box);
    }

    context = create(box);
    return true;
  }

  protected void processOtherNode(final RenderNode node)
  {
    if (node instanceof FinishedRenderNode)
    {
      // feed information about the collapsed nodes and their sizes.
    }
  }

  protected void finishBox(final RenderBox box)
  {
    context = context.commit(box);
    final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
    if (properties.isWidowOrphanOptOut() == false)
    {
      context.endChild(box);
    }
  }
}
