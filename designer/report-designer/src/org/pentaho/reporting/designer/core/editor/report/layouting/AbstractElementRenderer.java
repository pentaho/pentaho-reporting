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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.DesignerPageDrawable;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.designer.core.util.Unit;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportFooterType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportHeaderType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableLayoutProducer;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * @author Thomas Morgner
 */
public abstract class AbstractElementRenderer implements ElementRenderer
{
  private class VisualHeightUpdateListener implements ReportModelListener
  {
    private VisualHeightUpdateListener()
    {
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      if (event.getElement() != element)
      {
        if (event.getParameter() instanceof AttributeChange)
        {
          final AttributeChange attributeChange = (AttributeChange) event.getParameter();
          if (ReportDesignerBoot.DESIGNER_NAMESPACE.equals(attributeChange.getNamespace()) &&
              ReportDesignerBoot.VISUAL_HEIGHT.equals(attributeChange.getName()))
          {
            fireChangeEvent();
          }
        }
      }
    }
  }

  private Section element;
  private ReportRenderContext reportRenderContext;
  private EventListenerList listenerList;
  private Rectangle2D computedBounds;
  private long lastLayoutedChangeState;
  private LogicalPageBox pageBox;
  private BreakPositionsList verticalEdgePositions;
  private BreakPositionsList horizontalEdgePositions;
  private TransferLayoutProcessStep transferLayoutProcessor;
  private DesignerPageDrawable logicalPageDrawable;
  private ResourceManager resourceManager;
  private HashMap<InstanceID, Element> elementsById;

  protected AbstractElementRenderer(final Section element,
                                    final ReportRenderContext reportRenderContext)
  {
    if (element == null)
    {
      throw new NullPointerException();
    }
    if (reportRenderContext == null)
    {
      throw new NullPointerException();
    }

    this.elementsById = new HashMap<InstanceID, Element>();
    this.element = element;
    this.reportRenderContext = reportRenderContext;


    this.listenerList = new EventListenerList();
    this.verticalEdgePositions = new BreakPositionsList();
    this.horizontalEdgePositions = new BreakPositionsList();
    this.transferLayoutProcessor = new TransferLayoutProcessStep();
    this.transferLayoutProcessor.init(verticalEdgePositions, horizontalEdgePositions, element);
    
    this.resourceManager = reportRenderContext.getResourceManager();

    reportRenderContext.getReportDefinition().addReportModelListener(new VisualHeightUpdateListener());

    final Object d = element.getAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.VISUAL_HEIGHT);
    if (d instanceof Double == false)
    {
      if (element.getElementType() instanceof ReportHeaderType)
      {
        setVisualHeight(Unit.INCH.getDotsPerUnit() * 1.5);
      }
      else if (element.getElementType() instanceof ReportFooterType)
      {
        setVisualHeight(Unit.INCH.getDotsPerUnit() * 1.5);
      }
      else if (element.getElementType() instanceof ItemBandType)
      {
        setVisualHeight(Unit.INCH.getDotsPerUnit() * 1.5);
      }
      else
      {
        setVisualHeight(Unit.INCH.getDotsPerUnit());
      }
    }
  }

  public ReportRenderContext getReportRenderContext()
  {
    return reportRenderContext;
  }

  public Section getElement()
  {
    return element;
  }

  public ElementType getElementType()
  {
    return element.getElementType();
  }

  public InstanceID getRepresentationId()
  {
    return element.getObjectID();
  }

  public void addChangeListener(final ChangeListener changeListener)
  {
    listenerList.add(ChangeListener.class, changeListener);
  }

  public void removeChangeListener(final ChangeListener changeListener)
  {
    listenerList.remove(ChangeListener.class, changeListener);
  }

  public void fireChangeEvent()
  {
    final ChangeEvent ce = new ChangeEvent(this);
    final ChangeListener[] changeListeners = listenerList.getListeners(ChangeListener.class);
    for (int i = 0; i < changeListeners.length; i++)
    {
      final ChangeListener listener = changeListeners[i];
      listener.stateChanged(ce);
    }
  }

  public double getVisualHeight()
  {
    final Object d = element.getAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.VISUAL_HEIGHT);
    if (d instanceof Double)
    {
      return (Double) d;
    }
    return 0;
  }

  public void setVisualHeight(final double visualHeight)
  {
    if (visualHeight < 0)
    {
      throw new IllegalArgumentException();
    }
    final double oldHeight = getVisualHeight();
    if (visualHeight != oldHeight)
    {
      this.element.setAttribute
          (ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.VISUAL_HEIGHT, visualHeight, false);
      fireChangeEvent();
    }
  }

  public boolean isHideInLayout()
  {
    return ModelUtility.isHideInLayoutGui(element);
  }

  public LinealModel getVerticalLinealModel()
  {
    return ModelUtility.getVerticalLinealModel(element);
  }

  public double getLayoutHeight()
  {
    if (computedBounds == null || lastLayoutedChangeState != element.getChangeTracker())
    {
      lastLayoutedChangeState = element.getChangeTracker();
      computedBounds = performLayouting();
    }
    return Math.max(computedBounds.getHeight(), getVisualHeight());
  }

  public void resetBounds()
  {
    // Set computedBounds to null to allow performLayouting() to recalculate them.
    computedBounds = null;
  }


  public Rectangle2D getBounds()
  {
    if (computedBounds == null || lastLayoutedChangeState != element.getChangeTracker())
    {
      lastLayoutedChangeState = element.getChangeTracker();
      computedBounds = performLayouting();
    }
    return new Rectangle2D.Double(0, 0, computedBounds.getWidth(), Math.max(computedBounds.getHeight(), getVisualHeight()));
  }

  public double getComputedHeight()
  {
    return computedBounds.getHeight();
  }

  public StrictBounds getRootElementBounds()
  {
    if (logicalPageDrawable == null)
    {
      return new StrictBounds();
    }
    return (StrictBounds) logicalPageDrawable.getRootElementBounds().clone();
  }

  protected LogicalPageBox performReportLayout() throws ReportProcessingException, ContentProcessingException
  {
    return reportRenderContext.getReportLayouter().layout();
  }

  protected OutputProcessorMetaData getOutputProcessorMetaData()
  {
    return reportRenderContext.getReportLayouter().getOutputProcessorMetaData();
  }

  protected Rectangle2D performLayouting()
  {
    try
    {
      pageBox = performReportLayout();
    }
    catch (final Exception e)
    {
      //noinspection ThrowableInstanceNeverThrown
      UncaughtExceptionsModel.getInstance().addException(new ReportProcessingException
          ("Fatal Layouter Error: This report cannot be processed due to a unrecoverable error in the reporting-engine. " +
              "Please file a bug-report.", e));
      pageBox = null;
    }

    verticalEdgePositions.clear();
    horizontalEdgePositions.clear();

    try
    {
      final Rectangle2D computedBounds;
      if (pageBox == null)
      {
        elementsById.clear();
        logicalPageDrawable = null;
        computedBounds = new Rectangle2D.Double();
      }
      else
      {
        final OutputProcessorMetaData outputProcessorMetaData = getOutputProcessorMetaData();
        final TableLayoutProducer tableLayoutProducer = new TableLayoutProducer(outputProcessorMetaData);
        // we need to work on a copy here, as the layout computation marks boxes as finished to keep track
        // of the progress.
        final SheetLayout layout = tableLayoutProducer.createSheetLayout
            ((RenderBox) pageBox.getContentArea().derive(true));
        final DesignerTableContentProducer tableContentProducer =
            new DesignerTableContentProducer(layout, outputProcessorMetaData);
        
        final Map<InstanceID, Object> conflicts = tableContentProducer.computeConflicts(pageBox);
        transferLayoutProcessor.performTransfer(pageBox, elementsById, conflicts);

        logicalPageDrawable = new DesignerPageDrawable(pageBox, outputProcessorMetaData, resourceManager, element);
        final StrictBounds bounds = logicalPageDrawable.getRootElementBounds();
        computedBounds = StrictGeomUtility.createAWTRectangle(0, 0, pageBox.getWidth(), bounds.getHeight());
        if (getVisualHeight() < computedBounds.getHeight())
        {
          setVisualHeight(computedBounds.getHeight());
        }
      }

      fireChangeEvent();
      return computedBounds;
    }
    catch (Exception e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
      elementsById.clear();
      logicalPageDrawable = null;
      fireChangeEvent();
      return new Rectangle2D.Double();
    }
  }

  public DesignerPageDrawable getLogicalPageDrawable()
  {
    if (logicalPageDrawable == null)
    {
      performLayouting();
    }
    return logicalPageDrawable;
  }

  public HashMap<InstanceID, Element> getElementsById()
  {
    return elementsById;
  }

  public boolean draw(final Graphics2D graphics2D)
  {
    // this also computes the pagebox.
    final Rectangle2D bounds1 = getBounds();
    if (pageBox == null || logicalPageDrawable == null)
    {
      return false;
    }
    final Graphics2D graphics = (Graphics2D) graphics2D.create();

    graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    logicalPageDrawable.draw(graphics, bounds1);

    graphics.dispose();
    return true;
  }

  public BreakPositionsList getHorizontalEdgePositions()
  {
    return horizontalEdgePositions;
  }

  public long[] getHorizontalEdgePositionKeys()
  {
    return horizontalEdgePositions.getKeys();
  }

  public BreakPositionsList getVerticalEdgePositions()
  {
    return verticalEdgePositions;
  }

}
