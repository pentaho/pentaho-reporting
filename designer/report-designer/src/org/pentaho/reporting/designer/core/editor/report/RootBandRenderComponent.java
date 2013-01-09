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

package org.pentaho.reporting.designer.core.editor.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.ZoomModel;
import org.pentaho.reporting.designer.core.editor.report.layouting.RootBandRenderer;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionEvent;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionListener;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class RootBandRenderComponent extends AbstractRenderComponent
{
  private class RootBandChangeHandler implements SettingsListener, ReportModelListener
  {
    private RootBandChangeHandler()
    {
      updateGridSettings();
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      final Object element = event.getElement();
      if (element instanceof ReportElement == false)
      {
        return;
      }

      final ReportElement reportElement = (ReportElement) element;
      final Section band = getRendererRoot().getElement();
      if (ModelUtility.isDescendant(band, reportElement))
      {
        rendererRoot.resetBounds();
        RootBandRenderComponent.this.revalidate();
        RootBandRenderComponent.this.repaint();
        return;
      }

      if (reportElement instanceof Section)
      {
        final Section section = (Section) reportElement;
        if (ModelUtility.isDescendant(section, band))
        {
          rendererRoot.resetBounds();
          RootBandRenderComponent.this.revalidate();
          RootBandRenderComponent.this.repaint();
        }
      }
    }

    public void settingsChanged()
    {
      updateGridSettings();

      // this is cheap, just repaint and we will be happy
      RootBandRenderComponent.this.revalidate();
      RootBandRenderComponent.this.repaint();
    }
  }

  private class MouseSelectionHandler extends MouseAdapter implements MouseMotionListener
  {
    private Point2D normalizedSelectionRectangleOrigin;
    private Point selectionRectangleOrigin;
    private Point selectionRectangleTarget;
    private boolean clearSelectionOnDrag;
    private HashSet<Element> newlySelectedElements;

    private MouseSelectionHandler()
    {
      newlySelectedElements = new HashSet<Element>();
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(final MouseEvent e)
    {
      getDesignerContext().setSelectionWaiting(false);
      normalizedSelectionRectangleOrigin = null;
      selectionRectangleOrigin = null;
      selectionRectangleTarget = null;
      newlySelectedElements.clear();
      repaint();
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will
     * continue to be delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     * <p/>
     * Due to platform-dependent Drag&Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered
     * during a native Drag&Drop operation.
     */
    public void mouseDragged(final MouseEvent e)
    {
      if (getDesignerContext().isSelectionWaiting() == false)
      {
        return;
      }
      if (isMouseOperationInProgress())
      {
        return;
      }
      if (normalizedSelectionRectangleOrigin == null)
      {
        return;
      }

      final Point2D normalizedSelectionRectangleTarget = normalize(e.getPoint());
      normalizedSelectionRectangleTarget.setLocation(Math.max(0, normalizedSelectionRectangleTarget.getX()), Math.max(0, normalizedSelectionRectangleTarget
          .getY()));
      final RootBandRenderer rendererRoot = getRendererRoot();
      final ReportRenderContext renderContext = getRenderContext();

      if (clearSelectionOnDrag)
      {
        final ReportSelectionModel selectionModel = renderContext.getSelectionModel();
        selectionModel.clearSelection();
        clearSelectionOnDrag = false;
      }

      selectionRectangleTarget = e.getPoint();

      final DesignerPageDrawable pageDrawable = rendererRoot.getLogicalPageDrawable();
      final double y1 = Math.min(normalizedSelectionRectangleOrigin.getY(), normalizedSelectionRectangleTarget.getY());
      final double x1 = Math.min(normalizedSelectionRectangleOrigin.getX(), normalizedSelectionRectangleTarget.getX());
      final double y2 = Math.max(normalizedSelectionRectangleOrigin.getY(), normalizedSelectionRectangleTarget.getY());
      final double x2 = Math.max(normalizedSelectionRectangleOrigin.getX(), normalizedSelectionRectangleTarget.getX());

      final RenderNode[] allNodes = pageDrawable.getNodesAt(x1, y1, x2 - x1, y2 - y1, null, null);
      final ReportSelectionModel selectionModel = renderContext.getSelectionModel();
      final HashMap<InstanceID, Element> id = rendererRoot.getElementsById();
      final StrictBounds rect1 = StrictGeomUtility.createBounds(x1, y1, x2 - x1, y2 - y1);
      final StrictBounds rect2 = new StrictBounds();

      for (int i = allNodes.length - 1; i >= 0; i -= 1)
      {
        final RenderNode node = allNodes[i];
        final InstanceID instanceId = node.getInstanceId();

        final Element element = id.get(instanceId);
        if (element == null || element instanceof RootLevelBand)
        {
          continue;
        }
        final CachedLayoutData data = ModelUtility.getCachedLayoutData(element);
        rect2.setRect(data.getX(), data.getY(), data.getWidth(), data.getHeight());
        if (StrictBounds.intersects(rect1, rect2))
        {
          if (selectionModel.add(element))
          {
            newlySelectedElements.add(element);
          }
        }
      }

      // second step, check which previously added elements are no longer selected by the rectangle.
      for (Iterator<Element> visualReportElementIterator = newlySelectedElements.iterator(); visualReportElementIterator.hasNext(); )
      {
        final Element element = visualReportElementIterator.next();
        final CachedLayoutData data = ModelUtility.getCachedLayoutData(element);
        rect2.setRect(data.getX(), data.getY(), data.getWidth(), data.getHeight());
        if (StrictBounds.intersects(rect1, rect2) == false)
        {
          selectionModel.remove(element);
          visualReportElementIterator.remove();
        }

      }
      RootBandRenderComponent.this.repaint();
    }

    public Point getSelectionRectangleOrigin()
    {
      return selectionRectangleOrigin;
    }

    public Point getSelectionRectangleTarget()
    {
      return selectionRectangleTarget;
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     */
    public void mouseMoved(final MouseEvent e)
    {
    }


    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(final MouseEvent e)
    {
      if (isMouseOperationPossible())
      {
        return;
      }

      if (getDesignerContext().isSelectionWaiting() == false)
      {
        return;
      }

      newlySelectedElements.clear();
      normalizedSelectionRectangleOrigin = normalize(e.getPoint());
      normalizedSelectionRectangleOrigin.setLocation(Math.max(0,
          normalizedSelectionRectangleOrigin.getX()), Math.max(0, normalizedSelectionRectangleOrigin.getY()));

      selectionRectangleOrigin = e.getPoint();

      if (e.isShiftDown() == false)
      {
        clearSelectionOnDrag = true;
      }

      final ReportRenderContext renderContext = getRenderContext();
      final RootBandRenderer rendererRoot = getRendererRoot();

      final ReportSelectionModel selectionModel = renderContext.getSelectionModel();
      final HashMap<InstanceID, Element> id = rendererRoot.getElementsById();
      final DesignerPageDrawable pageDrawable = rendererRoot.getLogicalPageDrawable();
      final RenderNode[] allNodes = pageDrawable.getNodesAt(normalizedSelectionRectangleOrigin.getX(), normalizedSelectionRectangleOrigin.getY(), null, null);
      for (int i = allNodes.length - 1; i >= 0; i -= 1)
      {
        final RenderNode node = allNodes[i];
        final InstanceID instanceId = node.getInstanceId();

        final Element element = id.get(instanceId);
        if (element == null || element instanceof RootLevelBand)
        {
          continue;
        }

        if (!e.isShiftDown())
        {
          if (!selectionModel.isSelected(element))
          {
            selectionModel.clearSelection();
            selectionModel.add(element);
            return;
          }
        }
      }
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(final MouseEvent e)
    {
      final Point2D point = normalize(e.getPoint());
      if (point.getX() < 0 || point.getY() < 0)
      {
        return; // we do not handle that one ..
      }

      final ReportSelectionModel selectionModel = getRenderContext().getSelectionModel();
      final RootBandRenderer rendererRoot = getRendererRoot();
      final HashMap<InstanceID, Element> id = rendererRoot.getElementsById();
      final DesignerPageDrawable pageDrawable = rendererRoot.getLogicalPageDrawable();
      final RenderNode[] allNodes = pageDrawable.getNodesAt(point.getX(), point.getY(), null, null);
      for (int i = allNodes.length - 1; i >= 0; i -= 1)
      {
        final RenderNode node = allNodes[i];
        final InstanceID instanceId = node.getInstanceId();

        final Element element = id.get(instanceId);
        if (element == null || element instanceof RootLevelBand)
        {
          continue;
        }

        if (e.isShiftDown())
        {
          // toggle selection ..
          if (selectionModel.isSelected(element))
          {
            selectionModel.remove(element);
          }
          else
          {
            selectionModel.add(element);
          }
        }
        else
        {
          if (!selectionModel.isSelected(element))
          {
            selectionModel.clearSelection();
            selectionModel.add(element);
          }
        }

        return;
      }

      // No element found, clear the selection.
      if (e.isShiftDown() == false)
      {
        selectionModel.clearSelection();
      }
    }
  }

  private class SelectionModelListener implements ReportSelectionListener
  {
    private SelectionModelListener()
    {
    }

    public void selectionAdded(final ReportSelectionEvent event)
    {
      final Object element = event.getElement();
      if (element instanceof Element == false)
      {
        return;
      }

      final Element velement = (Element) element;
      ReportElement parentSearch = velement;
      final Section rootBand = getRendererRoot().getElement();
      final ZoomModel zoomModel = getRenderContext().getZoomModel();
      while (parentSearch != null)
      {
        if (parentSearch == rootBand)
        {
          final SelectionOverlayInformation renderer = new SelectionOverlayInformation(velement);
          renderer.validate(zoomModel.getZoomAsPercentage());
          velement.setAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.SELECTION_OVERLAY_INFORMATION, renderer, false);
          RootBandRenderComponent.this.repaint();
          return;
        }
        parentSearch = parentSearch.getParentSection();
      }
    }

    public void selectionRemoved(final ReportSelectionEvent event)
    {
      final Object element = event.getElement();
      if (element instanceof Element)
      {
        final Element e = (Element) element;
        final Object o = e.getAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.SELECTION_OVERLAY_INFORMATION);
        e.setAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.SELECTION_OVERLAY_INFORMATION, null, false);
        if (o != null)
        {
          RootBandRenderComponent.this.repaint();
        }
      }
      RootBandRenderComponent.this.repaint();
    }

    public void leadSelectionChanged(final ReportSelectionEvent event)
    {
      if (event.getModel().getSelectionCount() != 1)
      {
        return;
      }
      final Object raw = event.getElement();
      if (raw instanceof Element == false)
      {
        return;
      }

      Element e = (Element) raw;
      while (e != null && e instanceof RootLevelBand == false)
      {
        e = e.getParent();
      }

      if (e == getRootBand())
      {
        setFocused(true);
        repaint();
        SwingUtilities.invokeLater(new AsyncChangeNotifier());
      }
      else
      {
        setFocused(false);
        repaint();
        SwingUtilities.invokeLater(new AsyncChangeNotifier());
      }
    }
  }

  private class AsyncChangeNotifier implements Runnable
  {
    public void run()
    {
      rendererRoot.fireChangeEvent();
    }
  }

  private class RequestFocusHandler extends MouseAdapter implements PropertyChangeListener
  {

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(final MouseEvent e)
    {
      requestFocusInWindow();
      setFocused(true);
      SwingUtilities.invokeLater(new AsyncChangeNotifier());
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange(final PropertyChangeEvent evt)
    {
      final Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
      final boolean oldFocused = isFocused();
      final boolean focused = (owner == RootBandRenderComponent.this);
      if (oldFocused != focused)
      {
        setFocused(focused);
        repaint();
      }
      SwingUtilities.invokeLater(new AsyncChangeNotifier());
    }
  }

  private static final BasicStroke SELECTION_STROKE = new BasicStroke(0.5f);

  private RootBandRenderer rendererRoot;
  private RootBandChangeHandler changeHandler;
  private SelectionModelListener selectionModelListener;

  private MouseSelectionHandler selectionHandler;
  private RequestFocusHandler focusHandler;
  private SimpleStyleResolver styleResolver;
  private ResolverStyleSheet resolvedStyle;

  public RootBandRenderComponent(final ReportDesignerContext designerContext,
                                 final ReportRenderContext renderContext,
                                 final boolean showTopBorder)
  {
    super(designerContext, renderContext);

    styleResolver = new SimpleStyleResolver(true);
    resolvedStyle = new ResolverStyleSheet();
    setShowTopBorder(showTopBorder);

    this.changeHandler = new RootBandChangeHandler();
    this.selectionModelListener = new SelectionModelListener();

    renderContext.getSelectionModel().addReportSelectionListener(selectionModelListener);

    new DropTarget(this, new BandDndHandler(this));

    selectionHandler = new MouseSelectionHandler();
    addMouseListener(selectionHandler);
    addMouseMotionListener(selectionHandler);

    focusHandler = new RequestFocusHandler();
    addMouseListener(focusHandler);
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", focusHandler); // NON-NLS

    installMouseOperationHandler();

    renderContext.getReportDefinition().addReportModelListener(changeHandler);
  }

  public void dispose()
  {
    super.dispose();

    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", focusHandler); // NON-NLS

    final ReportRenderContext renderContext = getRenderContext();
    renderContext.getReportDefinition().removeReportModelListener(changeHandler);
    renderContext.getSelectionModel().removeReportSelectionListener(selectionModelListener);
  }

  protected void paintSelectionRectangle(final Graphics2D g2)
  {
    final Point origin = selectionHandler.getSelectionRectangleOrigin();
    final Point target = selectionHandler.getSelectionRectangleTarget();

    if (origin == null || target == null)
    {
      return;
    }

    g2.setColor(Color.BLUE);
    g2.setStroke(SELECTION_STROKE);

    final double y1 = Math.min(origin.getY(), target.getY());
    final double x1 = Math.min(origin.getX(), target.getX());
    final double y2 = Math.max(origin.getY(), target.getY());
    final double x2 = Math.max(origin.getX(), target.getX());

    g2.draw(new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1));
  }

  public RootBandRenderer getRendererRoot()
  {
    return (RootBandRenderer) getElementRenderer();
  }

  public void installRenderer(final RootBandRenderer rendererRoot, final LinealModel horizontalLinealModel,
                              final HorizontalPositionsModel horizontalPositionsModel)
  {
    super.installLineals(rendererRoot, horizontalLinealModel, horizontalPositionsModel);
    this.rendererRoot = rendererRoot;
  }

  public Element getElementForLocation(final Point2D point, final boolean onlySelected)
  {
    final RootBandRenderer rendererRoot = getRendererRoot();
    final HashMap<InstanceID, Element> id = rendererRoot.getElementsById();
    final DesignerPageDrawable pageDrawable = rendererRoot.getLogicalPageDrawable();
    final RenderNode[] allNodes = pageDrawable.getNodesAt(point.getX(), point.getY(), null, null);
    for (int i = allNodes.length - 1; i >= 0; i -= 1)
    {
      final RenderNode node = allNodes[i];
      final InstanceID instanceId = node.getInstanceId();

      final Element element = id.get(instanceId);
      if (element == null)
      {
        continue;
      }
      if (ModelUtility.isHideInLayoutGui(element) == true)
      {
        continue;
      }

      styleResolver.resolve(element, resolvedStyle);
      if (resolvedStyle.getBooleanStyleProperty(ElementStyleKeys.VISIBLE) == false)
      {
        continue;
      }

      if (onlySelected == false || getRenderContext().getSelectionModel().isSelected(element))
      {
        return element;
      }
    }
    return null;
  }

  public Band getRootBand()
  {
    return (Band) getRendererRoot().getElement();
  }

  protected BreakPositionsList getHorizontalEdgePositions()
  {
    return getRendererRoot().getHorizontalEdgePositions();
  }

  protected BreakPositionsList getVerticalEdgePositions()
  {
    return getRendererRoot().getVerticalEdgePositions();
  }

  protected RootLevelBand findRootBandForPosition(final Point2D point)
  {
    if (rendererRoot == null)
    {
      return null;
    }
    final Section section = rendererRoot.getElement();
    if (section instanceof RootLevelBand)
    {
      return (RootLevelBand) section;
    }
    return null;
  }

  public Element getDefaultElement()
  {
    if (rendererRoot == null)
    {
      return null;
    }
    return rendererRoot.getElement();
  }

  protected boolean isLocalElement(final ReportElement e)
  {
    return ModelUtility.isDescendant(getRootBand(), e);
  }
}
