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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.TextAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.EndSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineNodeSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.ReplacedContentSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SpacerSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.StartSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.TextSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisNodeContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisNodeContextPool;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisParagraphBreakState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisTableContext;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;


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
public final class CanvasMinorAxisLayoutStep extends AbstractMinorAxisLayoutStep
{
  private static final Log logger = LogFactory.getLog(CanvasMinorAxisLayoutStep.class);

  private MinorAxisParagraphBreakState lineBreakState;
  private MinorAxisNodeContext nodeContext;
  private MinorAxisNodeContextPool nodeContextPool;

  public CanvasMinorAxisLayoutStep()
  {
    nodeContextPool = new MinorAxisNodeContextPool();
    lineBreakState = new MinorAxisParagraphBreakState();
  }

  public void compute(final LogicalPageBox root)
  {
    getLineBreakState().clear();
    super.compute(root);
  }

  protected boolean startParagraphBox(final RenderBox box)
  {
    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      final ParagraphRenderBox paragraphBox = (ParagraphRenderBox) box;
      if (paragraphBox.isLineBoxUnchanged())
      {
        return false;
      }

      paragraphBox.clearLayout();
      getLineBreakState().init(paragraphBox);
    }
    return true;
  }

  protected void finishParagraphBox(final RenderBox box)
  {
    final MinorAxisParagraphBreakState lineBreakState = getLineBreakState();
    if (lineBreakState.isInsideParagraph())
    {
      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        lineBreakState.deinit();
      }
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    final MinorAxisNodeContext nodeContext = getNodeContext();
    final MinorAxisParagraphBreakState breakState = getLineBreakState();

    if (box.isComplexParagraph())
    {
      final RenderBox lineboxContainer = box.getLineboxContainer();
      RenderNode node = lineboxContainer.getFirstChild();
      while (node != null)
      {
        // all childs of the linebox container must be inline boxes. They
        // represent the lines in the paragraph. Any other element here is
        // a error that must be reported
        if (node.getNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX)
        {
          throw new IllegalStateException("Expected ParagraphPoolBox elements.");
        }

        final ParagraphPoolBox inlineRenderBox = (ParagraphPoolBox) node;
        if (startLine(inlineRenderBox))
        {
          processBoxChilds(inlineRenderBox);
          finishLine(inlineRenderBox, nodeContext, breakState);
        }

        node = node.getNext();
      }
    }
    else
    {
      final ParagraphPoolBox node = box.getPool();

      if (node.getFirstChild() == null)
      {
        return;
      }

      // all childs of the linebox container must be inline boxes. They
      // represent the lines in the paragraph. Any other element here is
      // a error that must be reported
      if (startLine(node))
      {
        processBoxChilds(node);
        finishLine(node, nodeContext, breakState);
      }
    }
  }

  private boolean startLine(final RenderBox inlineRenderBox)
  {
    final MinorAxisParagraphBreakState breakState = getLineBreakState();
    if (breakState.isInsideParagraph() == false)
    {
      return false;
    }

    if (breakState.isSuspended())
    {
      return false;
    }

    breakState.clear();
    breakState.add(StartSequenceElement.INSTANCE, inlineRenderBox);
    return true;
  }

  private void finishLine(final RenderBox inlineRenderBox,
                          final MinorAxisNodeContext nodeContext,
                          final MinorAxisParagraphBreakState breakState)
  {
    if (breakState.isInsideParagraph() == false || breakState.isSuspended())
    {
      throw new IllegalStateException("No active breakstate, finish-line cannot continue.");
    }

    final PageGrid pageGrid = getPageGrid();
    final OutputProcessorMetaData metaData = getMetaData();
    breakState.add(EndSequenceElement.INSTANCE, inlineRenderBox);

    final ParagraphRenderBox paragraph = breakState.getParagraph();

    final ElementAlignment textAlignment = paragraph.getTextAlignment();
    final long textIndent = paragraph.getTextIndent();
    final long firstLineIndent = paragraph.getFirstLineIndent();
    // This aligns all direct childs. Once that is finished, we have to
    // check, whether possibly existing inner-paragraphs are still valid
    // or whether moving them violated any of the inner-pagebreak constraints.
    final TextAlignmentProcessor processor = create(textAlignment);

    final SequenceList sequence = breakState.getSequence();

    final long lineEnd;
    final boolean overflowX = paragraph.getStaticBoxLayoutProperties().isOverflowX();
    if (overflowX)
    {
      lineEnd = nodeContext.getX1() + OVERFLOW_DUMMY_WIDTH;
    }
    else
    {
      lineEnd = nodeContext.getX2();
    }

    long lineStart = Math.min(lineEnd, nodeContext.getX1() + firstLineIndent);
    if (lineEnd - lineStart <= 0)
    {
      final long minimumChunkWidth = paragraph.getPool().getMinimumChunkWidth();
      processor.initialize(metaData, sequence, lineStart, lineStart + minimumChunkWidth, pageGrid, overflowX);
      nodeContext.updateX2(lineStart + minimumChunkWidth);
      logger.warn("Auto-Corrected zero-width first-line on paragraph - " + paragraph.getName());
    }
    else
    {
      processor.initialize(metaData, sequence, lineStart, lineEnd, pageGrid, overflowX);
      nodeContext.updateX2(lineEnd);
    }

    while (processor.hasNext())
    {
      final RenderNode linebox = processor.next();
      if (linebox.getLayoutNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX)
      {
        throw new IllegalStateException("Line must not be null");
      }

      paragraph.addGeneratedChild(linebox);

      if (processor.hasNext())
      {
        lineStart = Math.min(lineEnd, nodeContext.getX1() + textIndent);

        if (lineEnd - lineStart <= 0)
        {
          final long minimumChunkWidth = paragraph.getPool().getMinimumChunkWidth();
          processor.updateLineSize(lineStart, lineStart + minimumChunkWidth);
          nodeContext.updateX2(lineStart + minimumChunkWidth);
          logger.warn("Auto-Corrected zero-width text-line on paragraph continuation - " + paragraph.getName());
        }
        else
        {
          processor.updateLineSize(lineStart, lineEnd);
          nodeContext.updateX2(lineEnd);
        }

      }
    }

    processor.deinitialize();
  }

  protected MinorAxisNodeContext getNodeContext()
  {
    return nodeContext;
  }

  protected MinorAxisParagraphBreakState getLineBreakState()
  {
    return lineBreakState;
  }

  protected boolean startBlockLevelBox(final RenderBox box)
  {
    if (lineBreakState.isInsideParagraph())
    {
      throw new InvalidReportStateException("A block-level element inside a paragraph is not allowed.");
    }

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

    if (startParagraphBox(box) == false)
    {
      return false;
    }

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

      finishParagraphBox(box);
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
    if (lineBreakState.isInsideParagraph())
    {
      // The break-state exists only while we are inside of an paragraph
      // and suspend can only happen on inline elements.
      // A block-element inside a paragraph cannot be (and if it does, it is
      // a bug)
      throw new IllegalStateException("This cannot be.");
    }

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

    if (startParagraphBox(box) == false)
    {
      return false;
    }

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

      finishParagraphBox(box);
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
    if (lineBreakState.isInsideParagraph())
    {
      // The break-state exists only while we are inside of an paragraph
      // and suspend can only happen on inline elements.
      // A block-element inside a paragraph cannot be (and if it does, it is
      // a bug)
      throw new IllegalStateException("This cannot be.");
    }

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

    if (startParagraphBox(box) == false)
    {
      return false;
    }

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

      finishParagraphBox(box);
    }
    finally
    {
      nodeContext = nodeContext.pop();
    }
  }

  protected boolean startInlineLevelBox(final RenderBox box)
  {
    if (lineBreakState.isInsideParagraph() == false)
    {
      throw new InvalidReportStateException("A inline-level box outside of a paragraph box is not allowed.");
    }

    final int nodeType = box.getLayoutNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT)
    {
      lineBreakState.add(ReplacedContentSequenceElement.INSTANCE, box);
      return false;
    }

    lineBreakState.add(StartSequenceElement.INSTANCE, box);
    return true;
  }

  protected void processInlineLevelNode(final RenderNode node)
  {
    if (lineBreakState.isInsideParagraph() == false)
    {
      throw new InvalidReportStateException("A inline-level box outside of a paragraph box is not allowed.");
    }

    final int nodeType = node.getNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      final FinishedRenderNode finNode = (FinishedRenderNode) node;
      node.setCachedWidth(finNode.getLayoutedWidth());
      return;
    }

    if (nodeType == LayoutNodeTypes.TYPE_NODE_TEXT)
    {
      lineBreakState.add(TextSequenceElement.INSTANCE, node);
    }
    else if (nodeType == LayoutNodeTypes.TYPE_NODE_SPACER)
    {
      final StyleSheet styleSheet = node.getStyleSheet();
      if (WhitespaceCollapse.PRESERVE.equals(styleSheet.getStyleProperty(TextStyleKeys.WHITE_SPACE_COLLAPSE)) &&
          styleSheet.getBooleanStyleProperty(TextStyleKeys.TRIM_TEXT_CONTENT) == false)
      {
        // bug-alert: This condition could indicate a workaround for a logic-flaw in the text-processor
        lineBreakState.add(SpacerSequenceElement.INSTANCE, node);
      }
      else if (lineBreakState.isContainsContent())
      {
        lineBreakState.add(SpacerSequenceElement.INSTANCE, node);
      }
    }
    else
    {
      lineBreakState.add(InlineNodeSequenceElement.INSTANCE, node);
    }
  }

  protected void finishInlineLevelBox(final RenderBox box)
  {
    if (lineBreakState.isInsideParagraph() == false)
    {
      throw new InvalidReportStateException("A inline-level box outside of a paragraph box is not allowed.");
    }

    final int nodeType = box.getLayoutNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT)
    {
      return;
    }

    lineBreakState.add(EndSequenceElement.INSTANCE, box);
  }

  // Table-sections or auto-boxes masking as tables (treated as table-sections nonetheless).
  protected boolean startTableLevelBox(final RenderBox box)
  {
    if (lineBreakState.isInsideParagraph())
    {
      // The break-state exists only while we are inside of an paragraph
      // and suspend can only happen on inline elements.
      // A block-element inside a paragraph cannot be (and if it does, it is
      // a bug)
      throw new IllegalStateException("This cannot be.");
    }

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
    if (lineBreakState.isInsideParagraph())
    {
      // The break-state exists only while we are inside of an paragraph
      // and suspend can only happen on inline elements.
      // A block-element inside a paragraph cannot be (and if it does, it is
      // a bug)
      throw new IllegalStateException("This cannot be.");
    }

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
    if (lineBreakState.isInsideParagraph())
    {
      // The break-state exists only while we are inside of an paragraph
      // and suspend can only happen on inline elements.
      // A block-element inside a paragraph cannot be (and if it does, it is
      // a bug)
      throw new IllegalStateException("This cannot be.");
    }

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
    return true;
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
