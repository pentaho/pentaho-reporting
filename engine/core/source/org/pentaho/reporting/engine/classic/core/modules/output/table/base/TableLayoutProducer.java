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

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;

/**
 * Creation-Date: 02.05.2007, 18:31:37
 *
 * @author Thomas Morgner
 */
public class TableLayoutProducer extends IterateStructuralProcessStep
{
  private SheetLayout layout;

  private long pageOffset;
  private boolean headerProcessed;
  private long contentOffset;
  private long effectiveHeaderSize;
  private boolean unalignedPagebands;
  private long pageEndPosition;
  private boolean strictLayout;
  private boolean ellipseAsRectangle;

  public TableLayoutProducer(final OutputProcessorMetaData metaData)
  {
    if (metaData == null)
    {
      throw new NullPointerException();
    }
    this.unalignedPagebands = metaData.isFeatureSupported(OutputProcessorFeature.UNALIGNED_PAGEBANDS);
    this.strictLayout = metaData.isFeatureSupported(AbstractTableOutputProcessor.STRICT_LAYOUT);
    this.ellipseAsRectangle = metaData.isFeatureSupported(AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE);
    this.layout = new SheetLayout (strictLayout, ellipseAsRectangle);
  }

  public SheetLayout getLayout()
  {
    return layout;
  }

  public void update(final LogicalPageBox logicalPage,
                     final boolean iterativeUpdate)
  {
    if (unalignedPagebands == false)
    {
      // The page-header and footer area are aligned/shifted within the logical pagebox so that all areas
      // share a common coordinate system. This also implies, that the whole logical page is aligned content.
      pageOffset = 0;
      effectiveHeaderSize = 0;
      pageEndPosition = logicalPage.getPageEnd();
      //Log.debug ("Content Processing " + pageOffset + " -> " + pageEnd);
      if (startBlockBox(logicalPage))
      {
        if (headerProcessed == false)
        {
          startProcessing(logicalPage.getWatermarkArea());
          final BlockRenderBox headerArea = logicalPage.getHeaderArea();
          startProcessing(headerArea);
          headerProcessed = true;
        }

        processBoxChilds(logicalPage);
        if (iterativeUpdate == false)
        {
          final BlockRenderBox repeatFooterBox = logicalPage.getRepeatFooterArea();
          startProcessing(repeatFooterBox);

          final BlockRenderBox pageFooterBox = logicalPage.getFooterArea();
          startProcessing(pageFooterBox);
        }
      }
      finishBlockBox(logicalPage);
    }
    else
    {
      // The page-header and footer area are not aligned/shifted within the logical pagebox.
      // All areas have their own coordinate system starting at (0,0). We apply a manual shift here
      // so that we dont have to modify the nodes (which invalidates the cache, and therefore is ugly)
      effectiveHeaderSize = 0;
      pageOffset = logicalPage.getPageOffset();
      pageEndPosition = (logicalPage.getPageEnd());
      if (startBlockBox(logicalPage))
      {
        if (headerProcessed == false)
        {
          pageOffset = 0;
          contentOffset = 0;
          effectiveHeaderSize = 0;

          final BlockRenderBox watermarkArea = logicalPage.getWatermarkArea();
          pageEndPosition = watermarkArea.getHeight();
          startProcessing(watermarkArea);

          final BlockRenderBox headerArea = logicalPage.getHeaderArea();
          pageEndPosition = headerArea.getHeight();
          startProcessing(headerArea);
          contentOffset = headerArea.getHeight();
          headerProcessed = true;
        }

        pageOffset = logicalPage.getPageOffset();
        pageEndPosition = logicalPage.getPageEnd();
        effectiveHeaderSize = contentOffset;
        processBoxChilds(logicalPage);

        if (iterativeUpdate == false)
        {
          pageOffset = 0;
          final BlockRenderBox repeatFooterArea = logicalPage.getRepeatFooterArea();
          final long repeatFooterOffset = contentOffset + (logicalPage.getPageEnd() - logicalPage.getPageOffset());
          final long repeatFooterPageEnd = repeatFooterOffset + repeatFooterArea.getHeight();
          effectiveHeaderSize = repeatFooterOffset;
          pageEndPosition = repeatFooterPageEnd;
          startProcessing(repeatFooterArea);

          final BlockRenderBox footerArea = logicalPage.getFooterArea();
          final long footerPageEnd = repeatFooterPageEnd + footerArea.getHeight();
          effectiveHeaderSize = repeatFooterPageEnd;
          pageEndPosition = footerPageEnd;
          startProcessing(footerArea);
        }
      }
      finishBlockBox(logicalPage);
    }
  }

  /**
   * A designtime support method to compute a sheet layout for the given section. A new sheetlayout is created
   * on each call.
   *
   * @param section the section that should be processed.
   * @return the computed sheet layout.
   */
  public SheetLayout createSheetLayout(final RenderBox section)
  {
    this.layout = new SheetLayout(strictLayout, ellipseAsRectangle);

    pageOffset = 0;
    effectiveHeaderSize = 0;
    contentOffset = 0;
    pageEndPosition = section.getHeight();
    startProcessing(section);
    return layout;
  }

  private boolean startBox(final RenderBox box)
  {
    final long height = box.getHeight();
//
//    DebugLog.log ("Processing Box " + pageOffset + " " + effectiveHeaderSize + " " + box.getY() + " " + height);
//    DebugLog.log ("Processing Box " + box);

    if (height > 0)
    {
      if ((box.getY() + height) <= pageOffset)
      {
        return false;
      }
      if (box.getY() >= pageEndPosition)
      {
        return false;
      }
    }
    else
    {
      // zero height boxes are always a bit tricky ..
      if ((box.getY() + height) < pageOffset)
      {
        return false;
      }
      if (box.getY() > pageEndPosition)
      {
        return false;
      }
    }

    if (box.isOpen() == false &&
        box.isFinishedTable() == false &&
        box.isCommited())
    {
      if (layout.add(box, pageOffset, effectiveHeaderSize))
      {
        return false;
      }
      box.setFinishedTable(true);
      return true;
    }

    return true;
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

  protected boolean startTableBox(final TableRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startAutoBox(final RenderBox box)
  {
    return startBox(box);
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    if (box.isOpen() == false &&
        box.isFinishedTable() == false &&
        box.isCommited())
    {
      startBox(box);
      layout.addRenderableContent(box, pageOffset, effectiveHeaderSize);
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    // not needed. Keep this method empty so that the paragraph childs are *not* processed.
  }

  public void pageCompleted()
  {
    layout.pageCompleted();
    headerProcessed = false;
  }
}
