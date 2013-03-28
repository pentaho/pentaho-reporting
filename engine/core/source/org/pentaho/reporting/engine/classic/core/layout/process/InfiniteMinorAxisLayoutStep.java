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

import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisNodeContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisNodeContextPool;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisTableContext;


/**
 * This process-step computes the effective layout, but it does not take horizontal pagebreaks into account. (It has to
 * deal with vertical breaks, as they affect the text layout.)
 * <p/>
 * This processing step does not ajust anything on the vertical axis. Vertical alignment is handled in a second step.
 * <p/>
 * Please note: This layout model (unlike the default CSS model) uses the BOX-WIDTH as computed with. This means, the
 * defined width specifies the sum of all borders, paddings and the content area width.
 *
 * @author Thomas Morgner
 * @noinspection PointlessArithmeticExpression
 */
public final class InfiniteMinorAxisLayoutStep extends AbstractMinorAxisLayoutStep
{
  private MinorAxisNodeContext nodeContext;
  private MinorAxisNodeContextPool nodeContextPool;

  public InfiniteMinorAxisLayoutStep()
  {
    nodeContextPool = new MinorAxisNodeContextPool();
  }

  public void compute(final LogicalPageBox root)
  {
    super.compute(root);
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    nodeContext.updateX2(box.getX() + box.getInsetsLeft() + box.getMinimumChunkWidth());
  }

  protected MinorAxisNodeContext getNodeContext()
  {
    return nodeContext;
  }

  protected boolean startBlockLevelBox(final RenderBox box)
  {
    nodeContext = nodeContextPool.createContext(box, nodeContext, true);

    if (checkCacheValid(box))
    {
      return false;
    }

    startTableContext(box);

    final long x = nodeContext.getParentX1();
    final long left = box.getInsetsLeft();
    final long right = box.getInsetsRight();
    final long width = MinorAxisLayoutStepUtil.resolveNodeWidthOnStart(box, nodeContext);

    assert width >= 0;

    nodeContext.setArea(x, left, right, width);

    return true;
  }

  protected void processBlockLevelNode(final RenderNode node)
  {
    assert (node instanceof FinishedRenderNode);

    node.setCachedX(nodeContext.getX1());
    node.setCachedWidth(nodeContext.getContentAreaWidth());
  }

  protected void finishBlockLevelBox(final RenderBox box)
  {
    try
    {
      if (checkCacheValid(box))
      {
        nodeContext.updateParentX2(box.getCachedX2());
        return;
      }

      box.setCachedX(nodeContext.getX());
      box.setContentAreaX1(nodeContext.getX1());
      box.setContentAreaX2(nodeContext.getX2());
      if (finishTableContext(box) == false)
      {
        box.setCachedWidth(MinorAxisLayoutStepUtil.resolveNodeWidthOnFinish(box, nodeContext, isStrictLegacyMode()));
      }
      nodeContext.updateParentX2(box.getCachedX2());
    }
    finally
    {
      nodeContext = nodeContext.pop();
    }
  }

  private long computeCanvasPosition(final RenderNode node)
  {
    final long contentAreaX1 = nodeContext.getParentX1();
    final long bcw = nodeContext.getBlockContextWidth();
    final double posX = node.getNodeLayoutProperties().getPosX();
    final long position = RenderLength.resolveLength(bcw, posX);
    return (contentAreaX1 + position);
  }

  protected boolean startCanvasLevelBox(final RenderBox box)
  {
    nodeContext = nodeContextPool.createContext(box, nodeContext, false);

    if (checkCacheValid(box))
    {
      return false;
    }

    startTableContext(box);

    final long x = computeCanvasPosition(box);
    final long left = box.getInsetsLeft();
    final long right = box.getInsetsRight();
    final long width;
    if (isStrictLegacyMode() && box.useMinimumChunkWidth() == false)
    {
      width = MinorAxisLayoutStepUtil.resolveNodeWidthOnStartForCanvasLegacy(box, nodeContext);
    }
    else
    {
      width = MinorAxisLayoutStepUtil.resolveNodeWidthOnStart(box, nodeContext);
    }

    assert width >= 0;

    nodeContext.setArea(x, left, right, width);

    return true;
  }

  protected void processCanvasLevelNode(final RenderNode node)
  {
    assert (node instanceof FinishedRenderNode);

    node.setCachedX(computeCanvasPosition(node));
    node.setCachedWidth(node.getMaximumBoxWidth());
    nodeContext.updateParentX2(node.getCachedX2());
  }

  protected void finishCanvasLevelBox(final RenderBox box)
  {
    try
    {
      if (checkCacheValid(box))
      {
        nodeContext.updateParentX2(box.getCachedX2());
        return;
      }

      // make sure that the width takes all the borders and paddings into account.
      box.setCachedX(nodeContext.getX());
      box.setContentAreaX1(nodeContext.getX1());
      box.setContentAreaX2(nodeContext.getX2());
      if (finishTableContext(box) == false)
      {
        box.setCachedWidth(MinorAxisLayoutStepUtil.resolveNodeWidthOnFinish(box, nodeContext, isStrictLegacyMode()));
      }
      nodeContext.updateParentX2(box.getCachedX2());
    }
    finally
    {
      nodeContext = nodeContext.pop();
    }
  }

  private long computeRowPosition(final RenderNode node)
  {
    // The y-position of a box depends on the parent.
    final RenderBox parent = node.getParent();

    // A table row is something special. Although it is a block box,
    // it layouts its children from left to right
    if (parent == null)
    {
      // there's no parent ..
      return 0;
    }

    final RenderNode prev = node.getPrev();
    if (prev != null)
    {
      // we have a sibling. Position yourself directly to the right of your sibling ..
      return prev.getCachedX() + prev.getCachedWidth();
    }
    else
    {
      return nodeContext.getParentX1();
    }
  }

  protected boolean startRowLevelBox(final RenderBox box)
  {
    nodeContext = nodeContextPool.createContext(box, nodeContext, false);

    if (checkCacheValid(box))
    {
      return false;
    }

    startTableContext(box);

    final long x = computeRowPosition(box);
    final long left = box.getInsetsLeft();
    final long right = box.getInsetsRight();
    final long width = MinorAxisLayoutStepUtil.resolveNodeWidthOnStart(box, nodeContext);
    assert width >= 0;

    nodeContext.setArea(x, left, right, width);

    return true;
  }

  protected void processRowLevelNode(final RenderNode node)
  {
    assert (node instanceof FinishedRenderNode);

    node.setCachedX(computeRowPosition(node));
    node.setCachedWidth(node.getMaximumBoxWidth());
    nodeContext.updateParentX2(node.getCachedX2());
  }

  protected void finishRowLevelBox(final RenderBox box)
  {
    try
    {
      if (checkCacheValid(box))
      {
        nodeContext.updateParentX2(box.getCachedX2());
        return;
      }

      box.setCachedX(nodeContext.getX());
      box.setContentAreaX1(nodeContext.getX1());
      box.setContentAreaX2(nodeContext.getX2());
      if (finishTableContext(box) == false)
      {
        box.setCachedWidth(MinorAxisLayoutStepUtil.resolveNodeWidthOnFinish(box, nodeContext, isStrictLegacyMode()));
      }
      nodeContext.updateParentX2(box.getCachedX2());
    }
    finally
    {
      nodeContext = nodeContext.pop();
    }
  }

  protected boolean startInlineLevelBox(final RenderBox box)
  {
    return false;
  }

  // Table-sections or auto-boxes masking as tables (treated as table-sections nonetheless).
  protected boolean startTableLevelBox(final RenderBox box)
  {
    nodeContext = nodeContextPool.createContext(box, nodeContext, true);

    if (checkCacheValid(box))
    {
      return false;
    }

    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_COL_GROUP)
    {
      startTableColGroup((TableColumnGroupNode) box);
    }
    else if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_COL)
    {
      startTableCol((TableColumnNode) box);
    }
    else
    {
      startTableSectionOrRow(box);
    }
    return true;
  }

  private void startTableSectionOrRow(final RenderBox box)
  {
    final MinorAxisTableContext tableContext = getTableContext();
    final long x = nodeContext.getParentX1();

    final long width;
    if (tableContext.isStructureValidated())
    {
      width = tableContext.getTable().getColumnModel().getCachedSize();
    }
    else
    {
      width = MinorAxisLayoutStepUtil.resolveNodeWidthOnStart(box, nodeContext);
    }
    nodeContext.setArea(x, 0, 0, width);
  }

  protected void processTableLevelNode(final RenderNode node)
  {
    assert (node instanceof FinishedRenderNode);

    node.setCachedX(nodeContext.getX1());
    node.setCachedWidth(nodeContext.getContentAreaWidth());
  }

  protected void finishTableLevelBox(final RenderBox box)
  {
    try
    {
      if (checkCacheValid(box))
      {
        nodeContext.updateParentX2(box.getCachedX2());
        return;
      }

      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_COL_GROUP)
      {
        finishTableColGroup((TableColumnGroupNode) box);
      }
      else if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_COL)
      {
        finishTableCol((TableColumnNode) box);
      }
      else
      {
        box.setCachedX(nodeContext.getX());
        box.setContentAreaX1(nodeContext.getX1());
        box.setContentAreaX2(nodeContext.getX2());
        box.setCachedWidth(resolveTableWidthOnFinish(box));
        nodeContext.updateParentX2(box.getCachedX2());
      }
    }
    finally
    {
      nodeContext = nodeContext.pop();
    }
  }

  protected boolean startTableSectionLevelBox(final RenderBox box)
  {
    nodeContext = nodeContextPool.createContext(box, nodeContext, true);

    if (checkCacheValid(box))
    {
      return false;
    }

    startTableSectionOrRow(box);
    return true;
  }

  protected void processTableSectionLevelNode(final RenderNode node)
  {
    assert (node instanceof FinishedRenderNode);

    node.setCachedX(nodeContext.getX1());
    node.setCachedWidth(nodeContext.getContentAreaWidth());
  }

  protected void finishTableSectionLevelBox(final RenderBox box)
  {
    try
    {
      if (checkCacheValid(box))
      {
        nodeContext.updateParentX2(box.getCachedX2());
        return;
      }

      box.setCachedX(nodeContext.getX());
      box.setContentAreaX1(nodeContext.getX1());
      box.setContentAreaX2(nodeContext.getX2());
      box.setCachedWidth(resolveTableWidthOnFinish(box));

      nodeContext.updateParentX2(box.getCachedX2());
    }
    finally
    {
      nodeContext = nodeContext.pop();
    }
  }

  private long resolveTableWidthOnFinish(final RenderBox box)
  {
    final MinorAxisTableContext tableContext = getTableContext();
    if (tableContext.isStructureValidated())
    {
      return tableContext.getTable().getColumnModel().getCachedSize();
    }
    else
    {
      return MinorAxisLayoutStepUtil.resolveNodeWidthOnFinish(box, nodeContext, isStrictLegacyMode());
    }
  }

  protected boolean startTableRowLevelBox(final RenderBox box)
  {
    nodeContext = nodeContextPool.createContext(box, nodeContext, false);

    if (checkCacheValid(box))
    {
      return false;
    }

    if (box.getNodeType() != LayoutNodeTypes.TYPE_BOX_TABLE_CELL)
    {
      startTableSectionOrRow(box);
      return true;
    }

    final MinorAxisTableContext tableContext = getTableContext();
    final TableCellRenderBox tableCellRenderBox = (TableCellRenderBox) box;

    // This is slightly different for table cells ...
    final int columnIndex = tableCellRenderBox.getColumnIndex();
    final TableColumnModel columnModel = tableContext.getColumnModel();

    // cell-size does not include border spacing
    final long startOfRowX = nodeContext.getParentX1();

    final long x = startOfRowX + columnModel.getCellPosition(columnIndex);
    final long insetsLeft = Math.max(box.getInsetsLeft(), columnModel.getBorderSpacing() / 2);
    final long insetsRight = Math.max(box.getInsetsRight(), columnModel.getBorderSpacing() / 2);
    final long width = computeCellWidth(tableCellRenderBox);
    nodeContext.setArea(x, insetsLeft, insetsRight, width);
    return true;
  }

  protected void processTableRowLevelNode(final RenderNode node)
  {
    assert (node instanceof FinishedRenderNode);

    node.setCachedX(nodeContext.getX1());
    node.setCachedWidth(nodeContext.getContentAreaWidth());
  }

  protected void finishTableRowLevelBox(final RenderBox box)
  {
    try
    {
      if (checkCacheValid(box))
      {
        nodeContext.updateParentX2(box.getCachedX2());
        return;
      }

      box.setCachedX(nodeContext.getX());
      box.setContentAreaX1(nodeContext.getX1());
      box.setContentAreaX2(nodeContext.getX2());

      if (box.getNodeType() != LayoutNodeTypes.TYPE_BOX_TABLE_CELL)
      {
        // break-marker boxes etc.
        box.setCachedWidth(resolveTableWidthOnFinish(box));
        nodeContext.updateParentX2(box.getCachedX2());
      }
      else
      {
        box.setCachedWidth(MinorAxisLayoutStepUtil.resolveNodeWidthOnFinish(box, nodeContext, isStrictLegacyMode()));

        final TableCellRenderBox cell = (TableCellRenderBox) box;
        final MinorAxisTableContext tableContext = getTableContext();
        final TableRenderBox table = tableContext.getTable();
        if (tableContext.isStructureValidated() == false)
        {
          table.getColumnModel().updateCellSize(cell.getColumnIndex(), cell.getColSpan(), box.getCachedWidth() - box.getInsets());
        }
        nodeContext.updateParentX2(box.getCachedX2());
      }
    }
    finally
    {
      nodeContext = nodeContext.pop();
    }
  }

  protected boolean startTableCellLevelBox(final RenderBox box)
  {
    return startBlockLevelBox(box);
  }

  protected void processTableCellLevelNode(final RenderNode node)
  {
    processBlockLevelNode(node);
  }

  protected void finishTableCellLevelBox(final RenderBox box)
  {
    finishBlockLevelBox(box);
  }

  protected boolean startTableColGroupLevelBox(final RenderBox box)
  {
    nodeContext = nodeContextPool.createContext(box, nodeContext, false);

    if (checkCacheValid(box))
    {
      return false;
    }

    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_COL)
    {
      startTableCol((TableColumnNode) box);
    }
    return false;
  }

  protected void finishTableColGroupLevelBox(final RenderBox box)
  {
    try
    {
      if (checkCacheValid(box))
      {
        return;
      }

      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_COL)
      {
        finishTableCol((TableColumnNode) box);
      }
    }
    finally
    {
      nodeContext = nodeContext.pop();
    }
  }

  private void startTableCol(final TableColumnNode box)
  {
    // todo: Support col- and col-group elements
  }

  private void finishTableCol(final TableColumnNode box)
  {
    // todo: Support col- and col-group elements
  }

  private void startTableColGroup(final TableColumnGroupNode box)
  {
    // todo: Support col- and col-group elements
  }

  private void finishTableColGroup(final TableColumnGroupNode box)
  {
    // todo: Support col- and col-group elements
  }
}
