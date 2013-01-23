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
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PageableBreakContext;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

/**
 * A helper class that contains generic methods that would distract me from the actual pagination logic.
 */
public class PaginationStepLib
{
  private static final Log logger = LogFactory.getLog(PaginationStepLib.class);

  public static void configureBreakUtility(final PageBreakPositionList breakUtility,
                                            final LogicalPageBox pageBox,
                                            final long[] allCurrentBreaks,
                                            final long reservedHeight,
                                            final long lastBreakLocal)
  {
    final PageBreakPositionList allPreviousBreak = pageBox.getAllVerticalBreaks();
    breakUtility.copyFrom(allPreviousBreak);

    final long pageOffset = pageBox.getPageOffset();
    final long headerHeight = pageBox.getHeaderArea().getHeight();
    // Then add all new breaks (but take the header and footer-size into account) ..
    if (allCurrentBreaks.length == 1)
    {
      breakUtility.addMajorBreak(pageOffset, headerHeight);
      breakUtility.addMajorBreak((lastBreakLocal - reservedHeight) + pageOffset, headerHeight);
    }
    else // more than one physical page; therefore header and footer are each on a separate canvas ..
    {
      breakUtility.addMajorBreak(pageOffset, headerHeight);
      final int breakCount = allCurrentBreaks.length - 1;
      for (int i = 1; i < breakCount; i++)
      {
        final long aBreak = allCurrentBreaks[i];
        breakUtility.addMinorBreak(pageOffset + (aBreak - headerHeight));
      }
      breakUtility.addMajorBreak(pageOffset + (lastBreakLocal - reservedHeight), headerHeight);
    }
  }

  public static void assertProgress(final LogicalPageBox pageBox)
  {
    final RenderNode lastChild = pageBox.getLastChild();
    if (lastChild != null)
    {
      final long lastChildY2 = lastChild.getY() + lastChild.getHeight();
      if (lastChildY2 < pageBox.getHeight())
      {
        //ModelPrinter.print(pageBox);
        throw new IllegalStateException
            ("Assertation failed: Pagination did not proceed: " + lastChildY2 + " < " + pageBox.getHeight());
      }
    }
  }

  public static long restrictPageAreaHeights (final LogicalPageBox pageBox,
                                        final long[] allCurrentBreaks)
  {
    final BlockRenderBox headerArea = pageBox.getHeaderArea();
    final long headerHeight = Math.min(headerArea.getHeight(), allCurrentBreaks[0]);
    headerArea.setHeight(headerHeight);

    final BlockRenderBox footerArea = pageBox.getFooterArea();
    final BlockRenderBox repeatFooterArea = pageBox.getRepeatFooterArea();
    if (allCurrentBreaks.length > 1)
    {
      final long lastBreakLocal = allCurrentBreaks[allCurrentBreaks.length - 1];
      final long lastPageHeight = lastBreakLocal - allCurrentBreaks[allCurrentBreaks.length - 2];
      final long footerHeight = Math.min(footerArea.getHeight(), lastPageHeight);
      footerArea.setHeight(footerHeight);

      final long repeatFooterHeight = Math.min(repeatFooterArea.getHeight(), lastPageHeight);
      repeatFooterArea.setHeight(repeatFooterHeight);
    }

    final long footerHeight = footerArea.getHeight();
    final long repeatFooterHeight = repeatFooterArea.getHeight();
    // Assertion: Make sure that we do not run into a infinite loop..
    return headerHeight + repeatFooterHeight + footerHeight;
  }

  @Deprecated
  public static PageableBreakContext getBreakContext(final RenderBox box,
                                               final boolean createBoxIfNeeded,
                                               final boolean useInitialShift)
  {
    final PageableBreakContext boxContext = box.getBreakContext();
    final RenderBox parentBox = box.getParent();
    if (createBoxIfNeeded)
    {
      if (parentBox != null)
      {
        final PageableBreakContext parentContext = getBreakContext(parentBox, false, false);
        boxContext.updateFromParent(parentContext, useInitialShift);
      }
      else
      {
        // reset ...
        boxContext.reset();
      }
    }
    return boxContext;
  }

  public static void assertBlockPosition(final RenderBox box, final long shift)
  {
    if (box.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION)
    {
      // no point in testing table-sections, as the header will be an out-of-order band.
      return;
    }

    final boolean error;
    final long expectedYPos;
    if (box.getPrev() != null)
    {
      error = true;
      expectedYPos = box.getPrev().getY() + box.getPrev().getHeight();
    }
    else
    {
      if (box.getParent() != null)
      {
        error = false;
        expectedYPos = box.getParent().getY();
        final Object parentVAlignment = box.getParent().getStyleSheet().getStyleProperty(ElementStyleKeys.VALIGNMENT);
        if (parentVAlignment != null &&
            ElementAlignment.TOP.equals(parentVAlignment) == false)
        {
          return;
        }
      }
      else
      {
        error = true;
        expectedYPos = 0;
      }
    }

    final long realY = box.getY() + shift;
    if (realY != expectedYPos)
    {
      final long additionalShift = expectedYPos - realY;
      final long realShift = shift + additionalShift;
      if (error)
      {
        ModelPrinter.INSTANCE.print(box);
        ModelPrinter.INSTANCE.print(ModelPrinter.getRoot(box));
        throw new InvalidReportStateException("Assert: Shift is not as expected: realY=" + realY +
            " != expectation=" + expectedYPos + "; Shift=" + shift + "; AdditionalShift=" + additionalShift +
            "; RealShift=" + realShift);
      }
      else
      {
        logger.debug("Assert: Shift is not as expected: realY=" + realY +
            " != expectation=" + expectedYPos + "; Shift=" + shift + "; AdditionalShift=" + additionalShift +
            "; RealShift=" + realShift + " (False positive if block box has valign != TOP");
      }
    }
  }

  /**
   * Computes the height that will be required on this page to display at least some parts of the box.
   *
   * @param box the box for which the height is computed
   * @return the height in micro-points.
   */
  public static long computeNonBreakableBoxHeight(final RenderBox box)
  {
    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
    if (sblp.isAvoidPagebreakInside() && box.isPinned() == false)
    {
      return box.getHeight();
    }

    if (box.isPinned())
    {
      return 0;
    }

    final int nodeType = box.getLayoutNodeType();
    if ((nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT && sblp.isAvoidPagebreakInside()) ||
        (nodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE ||
        (nodeType & LayoutNodeTypes.MASK_BOX_ROW) == LayoutNodeTypes.MASK_BOX_ROW ||
        nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW)
    {
      // inline boxes are never broken down (at least we avoid it as if the breakinside is set.
      // same for renderable replaced content
      return box.getHeight();
    }

    if ((nodeType & LayoutNodeTypes.MASK_BOX_BLOCK) != LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      // Canvas boxes have no notion of lines, and therefore they cannot have orphans and widows.
      return 0;
    }

    final int orphans = sblp.getOrphans();
    final int widows = sblp.getWidows();
    if (orphans == 0 && widows == 0)
    {
      // Widows and orphans will be ignored if both of them are zero.
      return 0;
    }

    int counter = 0;
    RenderNode child = box.getFirstChild();
    while (child != null && counter < orphans)
    {
      counter += 1;
      child = child.getNext();
    }

    final long orphanHeight;
    if (child == null)
    {
      orphanHeight = 0;
    }
    else
    {
      orphanHeight = box.getY() - (child.getY() + child.getHeight());
    }

    counter = 0;
    child = box.getLastChild();
    while (child != null && counter < orphans)
    {
      counter += 1;
      child = child.getPrev();
    }

    final long widowHeight;
    if (child == null)
    {
      widowHeight = 0;
    }
    else
    {
      widowHeight = (box.getY() + box.getHeight()) - (child.getY());
    }

    // todo: Compute the height the orphans and widows consume.
    return Math.max(orphanHeight, widowHeight);
  }

}
