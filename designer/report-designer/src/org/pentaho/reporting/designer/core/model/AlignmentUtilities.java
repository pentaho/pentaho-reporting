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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.drag.MoveDragOperation;
import org.pentaho.reporting.designer.core.editor.report.snapping.EmptySnapModel;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntryBuilder;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class AlignmentUtilities
{
  private ReportRenderContext context;
  private PageDefinition originalPageDefinition;
  private PageDefinition currentPageDefinition;
  private Element[] visualElements;
  private MassElementStyleUndoEntryBuilder builder;

  public AlignmentUtilities(final ReportRenderContext reportRenderContext, final PageDefinition pageDefinition)
  {
    context = reportRenderContext;

    final MasterReport masterReport = reportRenderContext.getMasterReportElement();
    currentPageDefinition = masterReport.getPageDefinition();
    originalPageDefinition = pageDefinition;

    final ArrayList<Element> elementArrayList = new ArrayList<Element>();
    collectAlignableElements(masterReport, elementArrayList);
    visualElements = elementArrayList.toArray(new Element[elementArrayList.size()]);

    builder = new MassElementStyleUndoEntryBuilder(visualElements);
  }

  public void alignRight()
  {
    final double theCurrentPageWidth = currentPageDefinition.getWidth();
    final int theShiftRight = (int) (theCurrentPageWidth - StrictGeomUtility.toExternalValue(computeFarRightPostion()));
    align(theShiftRight, visualElements);
    registerChanges();
  }

  public void alignLeft()
  {
    final int theShiftLeft = (int) (0 - StrictGeomUtility.toExternalValue(computeFarLeftPosition()));
    align(theShiftLeft, visualElements);
    registerChanges();
  }

  public void resizeProportional()
  {
    final float originalPageWidth = originalPageDefinition.getWidth();
    final float currentPageWidth = currentPageDefinition.getWidth();
    final float scaleFactor = currentPageWidth / originalPageWidth;

    for (int i = 0; i < visualElements.length; i++)
    {

      // Resize the element.
      final CachedLayoutData cachedLayoutData = ModelUtility.getCachedLayoutData(visualElements[i]);

      final double elementWidth = StrictGeomUtility.toExternalValue(cachedLayoutData.getWidth());
      final Element theElement = visualElements[i];
      final ElementStyleSheet styleSheet = theElement.getStyle();
      styleSheet.setStyleProperty(ElementStyleKeys.MIN_WIDTH, new Float(elementWidth * scaleFactor));

      // Reposition the element.
      final double origin = StrictGeomUtility.toExternalValue(cachedLayoutData.getX());
      final double destination = scaleFactor * origin;
      final int theShift = (int) (destination - origin);

      final Element[] theElements = new Element[1];
      theElements[0] = theElement;
      align(theShift, theElements);
    }
    registerChanges();
  }

  public void alignCenter()
  {

    final long farLeftPostion = computeFarLeftPosition();
    final long farRightPostion = computeFarRightPostion();
    final long currentPageWidth = StrictGeomUtility.toInternalValue(currentPageDefinition.getWidth());
    final long remainingRightSpace = currentPageWidth - farRightPostion;
    final long normalizedSpace = (farLeftPostion + remainingRightSpace) / 2;

    long requiredShift = normalizedSpace - farLeftPostion;
    if (remainingRightSpace > farLeftPostion)
    {
      // move to the Right
      requiredShift = Math.abs(requiredShift);
    }
    else
    {
      // move to the Left
      requiredShift = 0 - Math.abs(requiredShift);
    }

    final int shiftInPoints = (int) StrictGeomUtility.toExternalValue(requiredShift);
    align(shiftInPoints, visualElements);
    registerChanges();
  }

  private void align(final int theShiftValue, final Element[] elements)
  {
    final MoveDragOperation mop =
        new MoveDragOperation(elements, new Point(), EmptySnapModel.INSTANCE, EmptySnapModel.INSTANCE);
    mop.update(new Point(theShiftValue, 0), 1);
    mop.finish();
  }

  private void registerChanges()
  {
    final MassElementStyleUndoEntry massElementStyleUndoEntry = builder.finish();
    context.getUndo().addChange(Messages.getString("AlignmentUtilities.Undo"), massElementStyleUndoEntry);
  }

  private long computeFarRightPostion()
  {
    long theFarRightPostion = 0;
    for (int i = 0; i < visualElements.length; i++)
    {
      final CachedLayoutData theElementData = ModelUtility.getCachedLayoutData(visualElements[i]);
      final long theCurrentPosition = theElementData.getX() + theElementData.getWidth();
      if (i == 0)
      {
        theFarRightPostion = theCurrentPosition;
      }
      else if (theCurrentPosition > theFarRightPostion)
      {
        theFarRightPostion = theCurrentPosition;
      }
    }
    return theFarRightPostion;
  }

  private long computeFarLeftPosition()
  {

    long theFarLeftPostion = 0;
    for (int i = 0; i < visualElements.length; i++)
    {
      final CachedLayoutData theElementData = ModelUtility.getCachedLayoutData(visualElements[i]);
      final long theCurrentPosition = theElementData.getX();
      if (i == 0)
      {
        theFarLeftPostion = theCurrentPosition;
      }
      else
      {
        theFarLeftPostion = theCurrentPosition < theFarLeftPostion ? theCurrentPosition : theFarLeftPostion;
      }
    }
    return theFarLeftPostion;
  }

  private void collectAlignableElements(final Section section, final List<Element> collectedElements)
  {
    if (section instanceof CrosstabGroup)
    {
      return;
    }

    final int theElementCount = section.getElementCount();
    for (int i = 0; i < theElementCount; i++)
    {
      final ReportElement reportElement = section.getElement(i);
      if (reportElement instanceof Section)
      {
        collectAlignableElements((Section) reportElement, collectedElements);
      }

      final CachedLayoutData cachedLayoutData = ModelUtility.getCachedLayoutData((Element) reportElement);
      final long layoutAge = cachedLayoutData.getLayoutAge();
      if (layoutAge != -1)
      {
        if (reportElement instanceof RootLevelBand)
        {
          continue;
        }

        if (reportElement instanceof Band ||
            reportElement instanceof Section == false)
        {
          collectedElements.add((Element) reportElement);
        }
      }
    }
  }
}
