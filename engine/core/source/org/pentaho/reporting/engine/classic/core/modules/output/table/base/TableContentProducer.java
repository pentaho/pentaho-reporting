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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ProcessUtility;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.GenericObjectTable;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

/**
 * After the pagination was able to deriveForAdvance the table-structure (all column and row-breaks are now known), this
 * second step flattens the layout-tree into a two-dimensional table structure.
 *
 * @author Thomas Morgner
 */
@SuppressWarnings("HardCodedStringLiteral")
public class TableContentProducer extends IterateStructuralProcessStep
{
  private static final Log logger = LogFactory.getLog(TableContentProducer.class);

  private SheetLayout sheetLayout;
  private GenericObjectTable<CellMarker> contentBackend;

  private long maximumHeight;
  private long maximumWidth;

  private TableRectangle lookupRectangle;
  private long pageOffset;
  private long pageEnd;
  private String sheetName;
  private int finishedRows;
  private int filledRows;
  private int clearedRows;
  private long contentOffset;
  private long effectiveOffset;
  private boolean unalignedPagebands;
  private boolean headerProcessed;
  private boolean ellipseAsBackground;
  private boolean shapesAsContent;
  private boolean processWatermark;

  private boolean verboseCellMarkers;
  private int verboseCellMarkersThreshold;
  private boolean debugReportLayout;
  private boolean reportCellConflicts;

  private int sectionDepth;
  private int sectionType;
  private OutputProcessorMetaData metaData;

  public TableContentProducer(final SheetLayout sheetLayout,
                              final OutputProcessorMetaData metaData)
  {
    if (metaData == null)
    {
      throw new NullPointerException();
    }
    if (sheetLayout == null)
    {
      throw new NullPointerException();
    }

    this.metaData = metaData;
    this.processWatermark = metaData.isFeatureSupported(OutputProcessorFeature.WATERMARK_SECTION);
    this.unalignedPagebands = metaData.isFeatureSupported(OutputProcessorFeature.UNALIGNED_PAGEBANDS);
    this.shapesAsContent = metaData.isFeatureSupported(AbstractTableOutputProcessor.SHAPES_CONTENT);
    this.ellipseAsBackground = metaData.isFeatureSupported(AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE);
    updateSheetLayout(sheetLayout);

//    DebugLog.log("Table-Size: " +  sheetLayout.getRowCount() + " " + sheetLayout.getColumnCount());
    final Configuration config = metaData.getConfiguration();
    this.debugReportLayout = "true".equals(config.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.base.DebugReportLayout"));
    this.verboseCellMarkers = "true".equals(config.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.base.VerboseCellMarkers"));
    this.verboseCellMarkersThreshold = ParserUtil.parseInt(config.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.base.VerboseCellMarkerThreshold"), 5000);
    this.reportCellConflicts = "true".equals(config.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.base.ReportCellConflicts"));
  }

  public boolean isProcessWatermark()
  {
    return processWatermark;
  }

  public void setProcessWatermark(final boolean processWatermark)
  {
    this.processWatermark = processWatermark;
  }

  protected void updateSheetLayout(final SheetLayout sheetLayout)
  {
    if (sheetLayout == null)
    {
      throw new NullPointerException();
    }

    this.sheetLayout = sheetLayout;
    this.maximumHeight = sheetLayout.getMaxHeight();
    this.maximumWidth = sheetLayout.getMaxWidth();
    this.contentBackend = new GenericObjectTable<CellMarker>(Math.max(1, sheetLayout.getRowCount()), Math.max(1,
        sheetLayout.getColumnCount()));
    this.contentBackend.ensureCapacity(sheetLayout.getRowCount(), sheetLayout.getColumnCount());
  }

  public String getSheetName()
  {
    return sheetName;
  }

  public void compute(final LogicalPageBox logicalPage,
                      final boolean iterativeUpdate)
  {
    // this.iterativeUpdate = iterativeUpdate;
//    ModelPrinter.print(logicalPage);
//    this.performOutput = performOutput;
    this.sheetName = null;
    if (unalignedPagebands == false)
    {
      // The page-header and footer area are aligned/shifted within the logical pagebox so that all areas
      // share a common coordinate system. This also implies, that the whole logical page is aligned content.
      pageOffset = 0;
      pageEnd = logicalPage.getPageEnd() - logicalPage.getPageOffset();
      effectiveOffset = 0;
      //Log.debug ("Content Processing " + pageOffset + " -> " + pageEnd);
      sectionType = CellMarker.TYPE_INVALID;
      if (startBlockBox(logicalPage))
      {
        if (headerProcessed == false)
        {
          sectionType = CellMarker.TYPE_HEADER;
          if (processWatermark)
          {
            startProcessing(logicalPage.getWatermarkArea());
          }
          final BlockRenderBox headerArea = logicalPage.getHeaderArea();
          startProcessing(headerArea);
          headerProcessed = true;
        }

        sectionType = CellMarker.TYPE_NORMALFLOW;
        processBoxChilds(logicalPage);
        if (iterativeUpdate == false)
        {
          sectionType = CellMarker.TYPE_REPEAT_FOOTER;
          final BlockRenderBox repeatFooterBox = logicalPage.getRepeatFooterArea();
          pageEnd += repeatFooterBox.getHeight();
          startProcessing(repeatFooterBox);

          sectionType = CellMarker.TYPE_FOOTER;
          final BlockRenderBox pageFooterBox = logicalPage.getFooterArea();
          pageEnd += pageFooterBox.getHeight();
          startProcessing(pageFooterBox);
        }
      }
      sectionType = CellMarker.TYPE_INVALID;
      finishBlockBox(logicalPage);
      //ModelPrinter.print(logicalPage);
    }
    else
    {
      // The page-header and footer area are not aligned/shifted within the logical pagebox.
      // All areas have their own coordinate system starting at (0,0). We apply a manual shift here
      // so that we dont have to modify the nodes (which invalidates the cache, and therefore is ugly)

      //Log.debug ("Content Processing " + pageOffset + " -> " + pageEnd);
      effectiveOffset = 0;
      pageOffset = 0;
      pageEnd = logicalPage.getPageEnd();
      sectionType = CellMarker.TYPE_INVALID;
      if (startBlockBox(logicalPage))
      {
        if (headerProcessed == false)
        {
          sectionType = CellMarker.TYPE_HEADER;
          contentOffset = 0;

          if (processWatermark)
          {
            final BlockRenderBox watermarkArea = logicalPage.getWatermarkArea();
            pageEnd = watermarkArea.getHeight();
            startProcessing(watermarkArea);
          }

          final BlockRenderBox headerArea = logicalPage.getHeaderArea();
          pageEnd = headerArea.getHeight();
          startProcessing(headerArea);
          contentOffset = headerArea.getHeight();
          headerProcessed = true;
        }

        final BlockRenderBox headerArea = logicalPage.getHeaderArea();
        sectionType = CellMarker.TYPE_NORMALFLOW;
        pageOffset = logicalPage.getPageOffset();
        pageEnd = logicalPage.getPageEnd();
        effectiveOffset = headerArea.getHeight();
        processBoxChilds(logicalPage);

        if (iterativeUpdate == false)
        {
          sectionType = CellMarker.TYPE_REPEAT_FOOTER;
          pageOffset = 0;
          final BlockRenderBox repeatFooterArea = logicalPage.getRepeatFooterArea();
          final long repeatFooterOffset = contentOffset + (logicalPage.getPageEnd() - logicalPage.getPageOffset());
          pageEnd = repeatFooterOffset + repeatFooterArea.getHeight();
          effectiveOffset = repeatFooterOffset;
          startProcessing(repeatFooterArea);

          final BlockRenderBox footerArea = logicalPage.getFooterArea();
          sectionType = CellMarker.TYPE_FOOTER;
          pageOffset = 0;
          final long footerOffset = pageEnd;
          pageEnd = footerOffset + footerArea.getHeight();
          effectiveOffset = footerOffset;
          startProcessing(footerArea);
        }
      }
      sectionType = CellMarker.TYPE_INVALID;
      finishBlockBox(logicalPage);
      //ModelPrinter.print(logicalPage);
    }

    if (iterativeUpdate)
    {
//      DebugLog.log("iterative: Computing commited rows: " + sheetLayout.getRowCount() + " vs. " + contentBackend.getRowCount());
      updateFilledRows();
    }
    else
    {
//      Log.debug("Non-iterative: Assuming all rows are commited: " + sheetLayout.getRowCount() + " vs. " + contentBackend.getRowCount());
//      updateFilledRows();
      filledRows = getRowCount();
    }

    if (iterativeUpdate == false)
    {
      headerProcessed = false;
    }
  }

  protected void computeDesigntimeConflicts(final RenderBox box)
  {
    effectiveOffset = 0;
    pageOffset = 0;
    pageEnd = box.getHeight();
    contentOffset = 0;
    contentBackend.clear();

    startProcessing(box);
    filledRows = getRowCount();
  }

  public RenderBox getContent(final int row, final int column)
  {
    if (verboseCellMarkers == false || row > verboseCellMarkersThreshold)
    {
      if (row < finishedRows)
      {
        return null;
      }
    }

    final CellMarker marker = contentBackend.getObject(row, column);
    if (marker == null)
    {
      return null;
    }
    return marker.getContent();
  }

  public RenderBox getBackground(final int row, final int column)
  {
    if (verboseCellMarkers == false || row > verboseCellMarkersThreshold)
    {
      if (row < finishedRows)
      {
        return null;
      }
    }

    final CellMarker marker = contentBackend.getObject(row, column);
    if (marker instanceof BandMarker)
    {
      final BandMarker bandMarker = (BandMarker) marker;
      return bandMarker.getBandBox();
    }
    return null;
  }

  public int getSectionType(final int row, final int column)
  {
    if (verboseCellMarkers == false || row > verboseCellMarkersThreshold)
    {
      if (row < finishedRows)
      {
        return -1;
      }
    }

    final CellMarker marker = contentBackend.getObject(row, column);
    if (marker == null)
    {
      return -1;
    }
    return marker.getSectionType();
  }

  public long getContentOffset(final int row, final int column)
  {
    if (verboseCellMarkers == false || row > verboseCellMarkersThreshold)
    {
      if (row < finishedRows)
      {
        return 0;
      }
    }

    final CellMarker marker = contentBackend.getObject(row, column);
    if (marker == null)
    {
      return 0;
    }
    return marker.getContentOffset();
  }

  public int getRowCount()
  {
    return Math.max(contentBackend.getRowCount(), sheetLayout.getRowCount());
  }

  public int getColumnCount()
  {
    return Math.max(contentBackend.getColumnCount(), sheetLayout.getColumnCount());
  }

  protected boolean startBox(final RenderBox box)
  {
    sectionDepth += 1;

    if (isProcessed(box))
    {
      return true;
    }

//    if (box.isOpen())
//    {
//      Log.debug("Received open box: " + box);
//    }

    final long y = effectiveOffset + box.getY() - pageOffset;
    final long height = box.getHeight();

    final long pageHeight = effectiveOffset + (pageEnd - pageOffset);

//    Log.debug ("Processing Box " + effectiveOffset + " " + pageHeight + " -> " + y + " " + height);
//    Log.debug ("Processing Box " + box);
//

    if (height > 0)
    {
      if ((y + height) <= effectiveOffset)
      {
        return false;
      }
      if (y >= pageHeight)
      {
        return false;
      }
    }
    else
    {
      // zero height boxes are always a bit tricky ..
      if ((y + height) < effectiveOffset)
      {
        return false;
      }
      if (y > pageHeight)
      {
        return false;
      }
    }

    // Always process everything ..
    final long y1 = Math.max(0, y);
    final long boxX = box.getX();
    final long x1 = Math.max(0, boxX);
    final long y2 = Math.min(y + box.getHeight(), maximumHeight);
    final long x2 = Math.min(boxX + box.getWidth(), maximumWidth);
    lookupRectangle = sheetLayout.getTableBounds(x1, y1, x2 - x1, y2 - y1, lookupRectangle);

    final boolean isContentBox;
    final Boolean contentBoxHint = box.getContentBox();
    if (Boolean.TRUE.equals(contentBoxHint))
    {
      // once a box is marked as content, then there is no need to check further ..
      isContentBox = contentBoxHint.booleanValue();
    }
    else
    {
      if ((box.getNodeType() & LayoutNodeTypes.TYPE_BOX_CONTENT) == LayoutNodeTypes.TYPE_BOX_CONTENT ||
          box.getStaticBoxLayoutProperties().isPlaceholderBox())
      {
        isContentBox = ProcessUtility.isContent(box, ellipseAsBackground, shapesAsContent) ||
            metaData.isExtraContentElement(box.getStyleSheet(), box.getAttributes());
        if (isContentBox)
        {
          box.setContentBox(Boolean.TRUE);
        }
        else
        {
          box.setContentBox(Boolean.FALSE);
        }
        box.setContentAge(box.getChangeTracker());
      }
      else if (box.getFirstChild() == null)
      {
        // empty boxes are never content ...
        isContentBox = false;
      }
      else
      {
        if (contentBoxHint != null && box.getContentAge() == box.getChangeTracker())
        {
          isContentBox = contentBoxHint.booleanValue();
        }
        else
        {
          // once the element has a
          isContentBox = ProcessUtility.isContent(box, ellipseAsBackground, shapesAsContent) ||
              metaData.isExtraContentElement(box.getStyleSheet(), box.getAttributes());
          if (isContentBox)
          {
            box.setContentBox(Boolean.TRUE);
          }
          else
          {
            box.setContentBox(Boolean.FALSE);
          }
          box.setContentAge(box.getChangeTracker());
        }
      }
    }

    if (isContentBox == false)
    {
      collectSheetStyleData(box);

      if (box.isCommited())
      {
        box.setFinishedTable(true);
      }

      if (isProcessed(box))
      {
        final int rectX2 = lookupRectangle.getX2();
        final int rectY2 = lookupRectangle.getY2();
        if (box.isCommited() == false)
        {
          throw new IllegalStateException();
        }
        //Log.debug("Processing box-cell with bounds (" + x1 + ", " + y1 + ")(" + x2 + ", " + y2 + ")");
        //if (rectY2 < finishedRows)
        //{
        //  // this is a repeated encounter, ignore it ..
        //}
        contentBackend.ensureCapacity(rectY2, rectX2);

        final BandMarker bandMarker = new BandMarker(box, sectionType, sectionDepth);
        for (int r = Math.max(lookupRectangle.getY1(), finishedRows); r < rectY2; r++)
        {
          for (int c = lookupRectangle.getX1(); c < rectX2; c++)
          {
            final CellMarker o = contentBackend.getObject(r, c);
            if (isReplaceableBackground(o, bandMarker))
            {
              contentBackend.setObject(r, c, bandMarker);
            }
          }
        }
      }
      return true;
    }

    if (box.isCommited() == false)
    {
      // content-box is not finished yet.
//      if (iterativeUpdate == false)
//      {
//        Log.debug("Still Skipping content-cell with bounds (" + x1 + ", " + y1 + ")(" + x2 + ", " + y2 + ")");
//      }
      return false;
    }

    //Log.debug("Processing content-cell with bounds (" + x1 + ", " + y1 + ")(" + x2 + ", " + y2 + ")");
    collectSheetStyleData(box);

    if (isCellSpaceOccupied(lookupRectangle) == false)
    {
      final int rectX2 = lookupRectangle.getX2();
      final int rectY2 = lookupRectangle.getY2();
      contentBackend.ensureCapacity(rectY2, rectX2);
      final ContentMarker contentMarker = new ContentMarker(box, effectiveOffset - pageOffset, sectionType);
      for (int r = lookupRectangle.getY1(); r < rectY2; r++)
      {
        for (int c = lookupRectangle.getX1(); c < rectX2; c++)
        {
          contentBackend.setObject(r, c, contentMarker);
        }
      }

      // Setting this content-box to finished has to be done in the actual content-generator.
    }
    else
    {
      handleContentConflict(box);
      box.setFinishedTable(true);
    }
    return true;
  }

  protected boolean isProcessed(final RenderBox box)
  {
    return box.isFinishedTable();
  }

  protected boolean isReplaceableBackground(final CellMarker oldMarker, final CellMarker newMarker)
  {
    if (oldMarker == null)
    {
      return true;
    }
    if (oldMarker.getSectionType() == CellMarker.TYPE_INVALID)
    {
      return true;
    }
    if (oldMarker.getSectionDepth() < newMarker.getSectionDepth())
    {
      return true;
    }
    return false;
  }

  protected TableRectangle getLookupRectangle()
  {
    return lookupRectangle;
  }

  protected void handleContentConflict(final RenderBox box)
  {
    if (reportCellConflicts)
    {
      logger.debug("LayoutShift: Offending Content: " + box);
      logger.debug("LayoutShift: Offending Content: " + isProcessed(box));
    }
  }

  protected void collectSheetStyleData(final RenderBox box)
  {
    final String sheetName = (String) box.getStyleSheet().getStyleProperty(BandStyleKeys.COMPUTED_SHEETNAME);
    if (this.sheetName == null && sheetName != null)
    {
      this.sheetName = sheetName;
    }
  }

  private boolean isCellSpaceOccupied(final TableRectangle rect)
  {
    final int x2 = rect.getX2();
    final int y2 = rect.getY2();

    for (int r = rect.getY1(); r < y2; r++)
    {
      if (r < finishedRows)
      {
        logger.debug("Row (" + r + ") already finished");
        return true;
      }
      else
      {
        for (int c = rect.getX1(); c < x2; c++)
        {
          final Object object = contentBackend.getObject(r, c);
          if (object != null && object instanceof BandMarker == false)
          {
            if (reportCellConflicts)
            {
              logger.debug(
                  "Cell (" + c + ", " + r + ") already filled: Content in cell: " + object);
            }
            return true;
          }
        }
      }
    }
    return false;
  }


  public int getFinishedRows()
  {
    return finishedRows;
  }

  public void clearFinishedBoxes()
  {
    final int rowCount = getFilledRows();
    final int columnCount = getColumnCount();
    if (debugReportLayout)
    {
      logger.debug("Request: Clearing rows from " + finishedRows + " to " + rowCount);
    }

    boolean atleastOneRowHasContent = false;
    int lastRowCleared = clearedRows - 1;
    for (int row = finishedRows; row < rowCount; row++)
    {
      boolean lastRowsUndefined = false;
      boolean rowHasContent = false;
      for (int column = 0; column < columnCount; column++)
      {
        final CellMarker o = contentBackend.getObject(row, column);
        if (o == null)
        {
          if (debugReportLayout)
          {
            logger.debug("maybe Cannot clear row: Cell (" + column + ", " + row + ") is undefined.");
          }
          lastRowsUndefined = true;
          continue;
        }
        else if (lastRowsUndefined)
        {
          if (debugReportLayout)
          {
            logger.debug("Cannot clear row: Inner Cell (" + column + ", " + row + ") is undefined.");
          }
          return;
        }
        final boolean b = o.isFinished();
        if (b == false)
        {
          if (debugReportLayout)
          {
            logger.debug(
                "Cannot clear row: Cell (" + column + ", " + row + ") is not finished: " + o);
          }
          return;
        }
        else
        {
          if (rowHasContent == false && o.getContent() != null)
          {
            rowHasContent = true;
          }
        }
      }

      // we can only clear rows when there is at least some content. Otherwise we will also clear the
      // markers for the cell-background on the BandMarker. This sadly eats slightly more memory, but
      // luckily it will only become an issue if your report is a large assortation of bands with not
      // a single element of real content. 
      if (rowHasContent)
      {
        atleastOneRowHasContent = true;
        finishedRows = row + 1;
        clearedRows = row + 1;
        for (int clearRowNr = lastRowCleared + 1; clearRowNr < finishedRows; clearRowNr++)
        {
          if (debugReportLayout)
          {
            logger.debug("#Cleared row: " + clearRowNr + '.');
          }
          if (verboseCellMarkers && filledRows < verboseCellMarkersThreshold)
          {
            for (int column = 0; column < columnCount; column++)
            {
              final Object o = contentBackend.getObject(clearRowNr, column);
              final FinishedMarker finishedMarker = new FinishedMarker(String.valueOf(o));
              contentBackend.setObject(clearRowNr, column, finishedMarker);
            }
          }
          else
          {
            contentBackend.clearRow(clearRowNr);
          }
        }
        lastRowCleared = row;
      }
    }

    if (debugReportLayout)
    {
      logger.debug("Need to clear  row: " + (lastRowCleared + 1) + " - " + filledRows);
    }
    finishedRows = filledRows;

    if (atleastOneRowHasContent)
    {
      for (int clearRowNr = lastRowCleared + 1; clearRowNr < finishedRows; clearRowNr++)
      {
        if (debugReportLayout)
        {
          logger.debug("*Cleared row: " + clearRowNr + '.');
        }
        if (verboseCellMarkers && filledRows < verboseCellMarkersThreshold)
        {
          for (int column = 0; column < columnCount; column++)
          {
            final Object o = contentBackend.getObject(clearRowNr, column);
            final FinishedMarker finishedMarker = new FinishedMarker(String.valueOf(o));
            contentBackend.setObject(clearRowNr, column, finishedMarker);
          }
        }
        else
        {
          contentBackend.clearRow(clearRowNr);
        }
        clearedRows = clearRowNr;
      }
    }
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    // we should not have come that far ..
    return false;
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    return startBox(box);
  }

  public boolean startCanvasBox(final CanvasRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    return startBox(box);
  }

  protected boolean startTableCellBox(final TableCellRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startTableRowBox(final TableRowRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startTableSectionBox(final TableSectionRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startTableColumnGroupBox(final TableColumnGroupNode box)
  {
    return false;
  }

  protected boolean startTableBox(final TableRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startAutoBox(final RenderBox box)
  {
    return startBox(box);
  }

  protected void endBox(final RenderBox box)
  {
    sectionDepth -= 1;
  }

  protected void finishCanvasBox(final CanvasRenderBox box)
  {
    endBox(box);
  }

  protected void finishBlockBox(final BlockRenderBox box)
  {
    endBox(box);
  }

  protected void finishOtherBox(final RenderBox box)
  {
    endBox(box);
  }

  protected void finishRowBox(final RenderBox box)
  {
    endBox(box);
  }

  protected void finishTableCellBox(final TableCellRenderBox box)
  {
    endBox(box);
  }

  protected void finishTableRowBox(final TableRowRenderBox box)
  {
    endBox(box);
  }

  protected void finishTableSectionBox(final TableSectionRenderBox box)
  {
    endBox(box);
  }

  protected void finishTableBox(final TableRenderBox box)
  {
    endBox(box);
  }

  protected void finishAutoBox(final RenderBox box)
  {
    endBox(box);
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    // not needed.
  }

  public SheetLayout getSheetLayout()
  {
    return sheetLayout;
  }

  public int getFilledRows()
  {
    return filledRows;
  }

  private void updateFilledRows()
  {
    final int rowCount = contentBackend.getRowCount();
    final int columnCount = getColumnCount();
    filledRows = finishedRows;
    for (int row = finishedRows; row < rowCount; row++)
    {
      boolean lastRowsUndefined = false;
      for (int column = 0; column < columnCount; column++)
      {
        final CellMarker o = contentBackend.getObject(row, column);
        if (o == null)
        {
          if (debugReportLayout)
          {
            logger.debug("Row: Cell (" + column + ", " + row + ") is undefined.");
          }
          lastRowsUndefined = true;
          continue;
        }
        else if (lastRowsUndefined)
        {
          if (debugReportLayout)
          {
            logger.debug("Row: Inner Cell (" + column + ", " + row + ") is undefined.");
          }
          return;
        }
        if (o.isCommited() == false)
        {
          if (debugReportLayout)
          {
            logger.debug("Row: Cell (" + column + ", " + row + ") is not commited.");
          }
          return;
        }
      }

      if (debugReportLayout)
      {
        logger.debug("Processable Row: " + filledRows + ".");
      }
      filledRows = row + 1;
    }

    if (debugReportLayout)
    {
      logger.debug("Processable Rows: " + finishedRows + ' ' + filledRows + '.');
    }
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    startBox(box);
    endBox(box);
  }

  public long getContentRowCount()
  {
    return contentBackend.getRowCount();
  }
}
