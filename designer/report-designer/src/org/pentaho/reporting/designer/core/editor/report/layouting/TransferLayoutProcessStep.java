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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import java.util.HashMap;
import java.util.Map;

import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;

/**
 * Computes the mapping between elements and their layouted position.
 *
 * @author Thomas Morgner
 */
public class TransferLayoutProcessStep extends IterateStructuralProcessStep
{
  private HashMap<InstanceID, Element> elementsById;
  private long age;
  private BreakPositionsList verticalEdgePositions;
  private BreakPositionsList horizontalEdgePositions;
  private Section rootLevelBandReportElement;
  private Map<InstanceID, Object> conflicts;

  public TransferLayoutProcessStep()
  {
  }

  public void init(final BreakPositionsList verticalEdgePositions,
                   final BreakPositionsList horizontalEdgePositions,
                   final Section rootLevelBandReportElement)
  {
    this.verticalEdgePositions = verticalEdgePositions;
    this.horizontalEdgePositions = horizontalEdgePositions;
    this.rootLevelBandReportElement = rootLevelBandReportElement;
  }

  public void performTransfer(final LogicalPageBox logicalPageBox,
                              final HashMap<InstanceID, Element> elementHashMap,
                              final Map<InstanceID, Object> conflicts)
  {
    //noinspection AssignmentToCollectionOrArrayFieldFromParameter
    this.elementsById = elementHashMap;
    this.conflicts = conflicts;
    this.age = rootLevelBandReportElement.getChangeTracker();
    try
    {
      this.elementsById.clear();
      elementsById.put(rootLevelBandReportElement.getObjectID(), rootLevelBandReportElement);
      collectElements(rootLevelBandReportElement);

      if (rootLevelBandReportElement instanceof RootLevelBand)
      {
        final RootLevelBand rl = (RootLevelBand) rootLevelBandReportElement;
        final int count = rl.getSubReportCount();
        for (int i = 0; i < count; i++)
        {
          final SubReport report = rl.getSubReport(i);
          elementsById.put(report.getObjectID(), report);
        }
      }

      startProcessing(logicalPageBox);
    }
    finally
    {
      this.elementsById = null;
      this.conflicts = null;
    }
  }

  private void collectElements(final Section sectionReportElement)
  {
    final int count = sectionReportElement.getElementCount();
    for (int i = 0; i < count; i++)
    {
      final Element reportElement = sectionReportElement.getElement(i);
      final InstanceID id = reportElement.getObjectID();
      elementsById.put(id, reportElement);

      if (reportElement instanceof SubReport)
      {
        continue;
      }
      if (reportElement instanceof Section)
      {
        collectElements((Section) reportElement);
      }
    }
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    return startBox(box);
  }

  private boolean startBox(final RenderBox box)
  {
    final InstanceID id = box.getNodeLayoutProperties().getInstanceId();
    final Element element = elementsById.get(id);
    if (element == null)
    {
      return true;
    }
    final CachedLayoutData data = ModelUtility.getCachedLayoutData(element);
    if (data.getLayoutAge() == age)
    {
      return true;
    }

    data.setX(box.getX());
    data.setY(box.getY());
    data.setWidth(box.getWidth());
    data.setHeight(box.getHeight());
    final BoxDefinition boxDefinition = box.getBoxDefinition();
    data.setPaddingX(boxDefinition.getPaddingLeft() + boxDefinition.getBorder().getLeft().getWidth());
    data.setPaddingY(boxDefinition.getPaddingTop() + boxDefinition.getBorder().getTop().getWidth());
    data.setLayoutAge(age);
    data.setElementType(box.getNodeType());
    data.setConflictsInTableMode(conflicts.containsKey(id));

    horizontalEdgePositions.add(data.getX(), id);
    horizontalEdgePositions.add(data.getX() + data.getWidth(), id);
    verticalEdgePositions.add(data.getY(), id);
    verticalEdgePositions.add(data.getY() + data.getHeight(), id);
    return true;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    return startBox(box);
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (ModelUtility.isHideInLayoutGui(box))
    {
      return false;
    }

    final InstanceID id = box.getNodeLayoutProperties().getInstanceId();
    final Element element = elementsById.get(id);
    if (element == null)
    {
      return true;
    }

    final CachedLayoutData data = ModelUtility.getCachedLayoutData(element);
    if (data.getLayoutAge() == age)
    {
      data.addAdditionalBounds(new StrictBounds(box.getX(), box.getY(), box.getWidth(), box.getHeight()));
      return true;
    }

    horizontalEdgePositions.add(box.getX(), box.getInstanceId());
    horizontalEdgePositions.add(box.getX() + box.getWidth(), box.getInstanceId());
    verticalEdgePositions.add(box.getY(), box.getInstanceId());
    verticalEdgePositions.add(box.getY() + box.getHeight(), box.getInstanceId());

    data.setX(box.getX());
    data.setY(box.getY());
    data.setWidth(box.getWidth());
    data.setHeight(box.getHeight());
    final BoxDefinition boxDefinition = box.getBoxDefinition();
    data.setPaddingX(boxDefinition.getPaddingLeft() + boxDefinition.getBorder().getLeft().getWidth());
    data.setPaddingY(boxDefinition.getPaddingTop() + boxDefinition.getBorder().getTop().getWidth());
    data.clearAdditionalBounds();
    data.setLayoutAge(age);
    data.setElementType(box.getNodeType());
    return true;
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    return startBox(box);
  }

  protected boolean startRowBox(final RenderBox box)
  {
    return startBox(box);
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  protected void processRenderableContent(final RenderableReplacedContentBox box)
  {
    startBox(box);
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
}
