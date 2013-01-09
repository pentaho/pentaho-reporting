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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.util.CacheBoxShifter;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * This processing step applies the cache shift to all nodes.
 * <p/>
 * Paginated and processed boxes are based on shifted nodes that have been created/shifted by the PaginationStep. The
 * cached values of the nodes that come after these finished nodes are now invalid, as the cache-positions have been
 * stored *before* the pagination shift was applied.
 * <p/>
 * Instead of simply invalidating the caches (which would be expensive), we patch the caches of these nodes and shift
 * them downwards so that the cache and the recomputation results will be in sync.
 * <p/>
 * Without this correction (or the equivalent cache-invalidation), we would encounter lost content, as the finished node
 * and the cached content would overlap.
 *
 * @author Thomas Morgner
 */
public final class ApplyPageShiftValuesStep extends IterateStructuralProcessStep
{
  private long shift;
  private InstanceID triggerId;
  private boolean found;

  public ApplyPageShiftValuesStep()
  {
  }

  public void compute(final LogicalPageBox logicalPageBox, final long shift, final InstanceID triggerId)
  {
    if (shift <= 0)
    {
      throw new IllegalArgumentException("Why are you calling me with nothing to do?");
    }
    if (triggerId == null)
    {
      throw new IllegalArgumentException("Without a trigger I cannot fire up a shift");
    }
    this.shift = shift;
    this.triggerId = triggerId;
    this.found = false;
    startProcessing(logicalPageBox);
  }

  public boolean startCanvasBox(final CanvasRenderBox box)
  {
    return processBox(box);
  }

  private boolean processBox(final RenderNode box)
  {
    if (found == false && box.getInstanceId() == triggerId)
    {
      found = true;
      CacheBoxShifter.extendHeight(box, shift);
    }
    if (found)
    {
      box.shiftCached(shift);
    }
    return true;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    return processBox(box);
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    processBox(box);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    return processBox(box);
  }

  protected void processOtherNode(final RenderNode node)
  {
    if (found)
    {
      node.shiftCached(shift);
    }
  }

  protected boolean startTableBox(final TableRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startTableCellBox(final TableCellRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startTableColumnGroupBox(final TableColumnGroupNode box)
  {
    return processBox(box);
  }

  protected boolean startTableRowBox(final TableRowRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startTableSectionBox(final TableSectionRenderBox box)
  {
    return processBox(box);
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    return processBox(box);
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  protected boolean startAutoBox(final RenderBox box)
  {
    return processBox(box);
  }
}
