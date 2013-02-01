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

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ContextMenuUtility;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.ZoomModel;
import org.pentaho.reporting.designer.core.editor.ZoomModelListener;
import org.pentaho.reporting.designer.core.editor.report.drag.CompoundDragOperation;
import org.pentaho.reporting.designer.core.editor.report.drag.MouseDragOperation;
import org.pentaho.reporting.designer.core.editor.report.drag.MoveDragOperation;
import org.pentaho.reporting.designer.core.editor.report.drag.ResizeBottomDragOperation;
import org.pentaho.reporting.designer.core.editor.report.drag.ResizeLeftDragOperation;
import org.pentaho.reporting.designer.core.editor.report.drag.ResizeRightDragOperation;
import org.pentaho.reporting.designer.core.editor.report.drag.ResizeTopDragOperation;
import org.pentaho.reporting.designer.core.editor.report.layouting.AbstractElementRenderer;
import org.pentaho.reporting.designer.core.editor.report.layouting.ElementRenderer;
import org.pentaho.reporting.designer.core.editor.report.snapping.EmptySnapModel;
import org.pentaho.reporting.designer.core.editor.report.snapping.FullSnapModel;
import org.pentaho.reporting.designer.core.editor.report.snapping.SnapToPositionModel;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.GuideLine;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.model.lineal.LinealModelEvent;
import org.pentaho.reporting.designer.core.model.lineal.LinealModelListener;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionEvent;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionListener;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.AttributeEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntryBuilder;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class AbstractRenderComponent extends JComponent
    implements ReportElementEditorContext, CellEditorListener
{
  protected class AsyncChangeNotifier implements Runnable
  {
    public void run()
    {
      ((AbstractElementRenderer)(getElementRenderer())).fireChangeEvent();
    }
  }

  protected class RootBandChangeHandler implements SettingsListener, ReportModelListener
  {
    protected RootBandChangeHandler()
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
        getRendererRoot().resetBounds();
        AbstractRenderComponent.this.revalidate();
        AbstractRenderComponent.this.repaint();
        return;
      }

      if (reportElement instanceof Section)
      {
        final Section section = (Section) reportElement;
        if (ModelUtility.isDescendant(section, band))
        {
          getRendererRoot().resetBounds();
          AbstractRenderComponent.this.revalidate();
          AbstractRenderComponent.this.repaint();
        }
      }
    }

    public void settingsChanged()
    {
      updateGridSettings();

      // this is cheap, just repaint and we will be happy
      AbstractRenderComponent.this.revalidate();
      AbstractRenderComponent.this.repaint();

    }
  }


  protected class MouseSelectionHandler extends MouseAdapter implements MouseMotionListener
  {
    private Point2D normalizedSelectionRectangleOrigin;
    private Point selectionRectangleOrigin;
    private Point selectionRectangleTarget;
    private boolean clearSelectionOnDrag;
    private HashSet<Element> newlySelectedElements;

    protected MouseSelectionHandler()
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
      final AbstractElementRenderer rendererRoot = getRendererRoot();
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

      AbstractRenderComponent.this.repaint();
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
      final AbstractElementRenderer rendererRoot = getRendererRoot();

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
      final AbstractElementRenderer rendererRoot = getRendererRoot();
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

  protected class SelectionModelListener implements ReportSelectionListener
  {
    protected SelectionModelListener()
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
          AbstractRenderComponent.this.repaint();
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
          AbstractRenderComponent.this.repaint();
        }
      }
      AbstractRenderComponent.this.repaint();
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

  protected static final class RepaintHandler implements LinealModelListener, ZoomModelListener, ChangeListener
  {
    private AbstractRenderComponent component;

    private RepaintHandler(final AbstractRenderComponent component)
    {
      this.component = component;
    }

    public void stateChanged(final ChangeEvent e)
    {
      // this is cheap, just repaint and we will be happy
      component.revalidate();
      component.repaint();
    }

    public void modelChanged(final LinealModelEvent event)
    {
      component.revalidate();
      component.repaint();
    }

    public void zoomFactorChanged()
    {
      component.revalidate();
      component.repaint();
      component.stopCellEditing();
    }

  }

  protected class SettingsUpdateHandler implements SettingsListener
  {
    protected SettingsUpdateHandler()
    {
    }

    public void settingsChanged()
    {
      updateGridSettings();

      revalidate();
      repaint();
    }
  }

  protected class KeyboardElementMoveHandler extends KeyAdapter
  {

    public KeyboardElementMoveHandler()
    {
    }

    public void keyReleased(final KeyEvent e)
    {
      if (e.isShiftDown() == false && getDesignerContext().isSelectionWaiting())
      {
        if (currentIndicator == SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE)
        {
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        else if (currentIndicator == SelectionOverlayInformation.InRangeIndicator.MOVE)
        {
          setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
        getDesignerContext().setSelectionWaiting(false);
      }
    }

    public void keyPressed(final KeyEvent keyEvent)
    {
      // move all selected components 1px
      final Element[] visualElements = getRenderContext().getSelectionModel().getSelectedVisualElements();
      if (visualElements.length == 0)
      {
        return;
      }

      // if any element's X or Y is == 0, then do not move anything
      // PRD-1442
      final int keyCode = keyEvent.getKeyCode();
      if (keyCode != KeyEvent.VK_UP && keyCode != KeyEvent.VK_DOWN &&
          keyCode != KeyEvent.VK_LEFT && keyCode != KeyEvent.VK_RIGHT)
      {
        return;
      }

      if (keyEvent.isShiftDown() || keyEvent.isAltDown() || keyEvent.isControlDown())
      {
        return;
      }

      keyEvent.consume();

      for (int i = 0; i < visualElements.length; i++)
      {
        final Element element = visualElements[i];
        if (element instanceof RootLevelBand)
        {
          continue;
        }
        final double elementX = element.getStyle().getDoubleStyleProperty(ElementStyleKeys.POS_X, 0);
        final double elementY = element.getStyle().getDoubleStyleProperty(ElementStyleKeys.POS_Y, 0);
        // check if we can't move, one of the elements in the group is already at the minimum position
        if (keyCode == KeyEvent.VK_UP && elementY == 0)
        {
          return;
        }
        else if (keyCode == KeyEvent.VK_LEFT && elementX == 0)
        {
          return;
        }
      }

      final MassElementStyleUndoEntryBuilder builder = new MassElementStyleUndoEntryBuilder(visualElements);
      final MoveDragOperation mop = new MoveDragOperation(visualElements, new Point(), EmptySnapModel.INSTANCE, EmptySnapModel.INSTANCE);

      if (keyCode == KeyEvent.VK_UP)
      {
        mop.update(new Point(0, -1), 1);
      }
      else if (keyCode == KeyEvent.VK_DOWN)
      {
        mop.update(new Point(0, 1), 1);
      }
      else if (keyCode == KeyEvent.VK_LEFT)
      {
        mop.update(new Point(-1, 0), 1);
      }
      else
      {
        mop.update(new Point(1, 0), 1);
      }
      final MassElementStyleUndoEntry massElementStyleUndoEntry = builder.finish();
      getRenderContext().getUndo().addChange(Messages.getString("AbstractRenderComponent.MoveUndoName"), massElementStyleUndoEntry);
      mop.finish();
    }
  }

  protected class MouseEditorActionHandler extends MouseAdapter
  {
    private MouseEditorActionHandler()
    {
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(final MouseEvent e)
    {
      if (stopCellEditing() == false)
      {
        return;
      }

      if (e.isPopupTrigger())
      {
        final Point2D point = normalize(e.getPoint());
        if (point.getX() < 0 || point.getY() < 0)
        {
          return; // we do not handle that one ..
        }

        showElementPopup(e, point);
        return;
      }

      // ReportElementInlineEditor ...
      if (e.getClickCount() >= 2 && (e.getButton() == MouseEvent.BUTTON1))
      {
        final Point2D point = normalize(e.getPoint());
        if (point.getX() < 0 || point.getY() < 0)
        {
          return; // we do not handle that one ..
        }

        final Element element = getElementForLocation(point, true);
        if (element == null)
        {
          return;
        }

        final String typeName = element.getElementTypeName();
        ReportElementEditor elementEditor = ReportElementEditorRegistry.getInstance().getPlugin(typeName);
        if (elementEditor == null)
        {
          elementEditor = ReportElementEditorRegistry.getInstance().getPlugin(null);
          if (elementEditor == null)
          {
            return;
          }
        }

        final ReportElementInlineEditor inlineEditor = elementEditor.createInlineEditor();
        if (inlineEditor == null)
        {
          return;
        }

        installEditor(inlineEditor, element);
      }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(final MouseEvent e)
    {
      if (stopCellEditing() == false)
      {
        return;
      }

      if (e.isPopupTrigger())
      {
        final Point2D point = normalize(e.getPoint());
        if (point.getX() < 0 || point.getY() < 0)
        {
          return; // we do not handle that one ..
        }

        showElementPopup(e, point);
      }
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(final MouseEvent e)
    {
      if (stopCellEditing() == false)
      {
        return;
      }

      if (e.isPopupTrigger())
      {
        final Point2D point = normalize(e.getPoint());
        if (point.getX() < 0 || point.getY() < 0)
        {
          return; // we do not handle that one ..
        }

        showElementPopup(e, point);
      }
    }

    protected void showElementPopup(final MouseEvent e, final Point2D normalizedPoint)
    {
      Element element = getElementForLocation(normalizedPoint, true);
      if (element == null)
      {
        element = (Element) findRootBandForPosition(normalizedPoint);
      }

      final JPopupMenu pop = ContextMenuUtility.getMenu(getDesignerContext(), element);
      if (pop == null)
      {
        return;
      }
      pop.show(AbstractRenderComponent.this, e.getX(), e.getY());
    }
  }

  private class MouseOperationHandler extends MouseAdapter implements MouseMotionListener
  {
    private SelectionOverlayInformation currentRenderer;
    private Point2D lastPoint;

    private MouseOperationHandler()
    {
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will continue to be delivered to the component
     * where the drag originated until the mouse button is released (regardless of whether the mouse position is within the bounds of the component).
     * <p/>
     * Due to platform-dependent Drag&Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered during a native Drag&Drop operation.
     */
    public void mouseDragged(final MouseEvent e)
    {
      if (lastPoint == null)
      {
        return;
      }

      final Point2D normalizedPoint = normalize(e.getPoint());
      updateElements(normalizedPoint, e.isAltDown(), e.isControlDown());
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     */
    public void mouseMoved(final MouseEvent e)
    {
      final Point point1 = e.getPoint();
      updateCursor(point1);
    }

    private void updateCursor(final Point rawPoint)
    {
      final boolean selectionMode = getDesignerContext().isSelectionWaiting();
      if (selectionMode)
      {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        currentIndicator = SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE;
        return;
      }

      currentIndicator = SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE;
      final Point2D normalizedPoint = normalize(rawPoint);
      if (currentRenderer != null)
      {
        currentIndicator = currentRenderer.getMouseInRangeIndicator(normalizedPoint);
        if (currentIndicator == SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE)
        {
          currentRenderer = null;
          currentIndicator = SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE;
        }
        if (currentIndicator == SelectionOverlayInformation.InRangeIndicator.MOVE)
        {
          currentRenderer = null;
          currentIndicator = SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE;
        }
      }

      final Element[] renderers = getRenderContext().getSelectionModel().getSelectedVisualElements();
      for (int i = 0; i < renderers.length; i++)
      {
        final Element e = renderers[i];
        final Object o = e.getAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, "selection-overlay-information"); // NON-NLS
        if (o instanceof SelectionOverlayInformation == false)
        {
          continue;
        }

        if (isLocalElement(e) == false)
        {
          continue;
        }

        final SelectionOverlayInformation renderer = (SelectionOverlayInformation) o;
        final SelectionOverlayInformation.InRangeIndicator indicator = renderer.getMouseInRangeIndicator(normalizedPoint);
        if (indicator == SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE)
        {
          continue;
        }

        // a resize-handle wins over a ordinary move selection
        if (currentIndicator == SelectionOverlayInformation.InRangeIndicator.MOVE
            || currentIndicator == SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE)
        {
          currentIndicator = indicator;
          currentRenderer = renderer;
        }
        else
        {
          break;
        }
      }

      updateCursorForIndicator();
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(final MouseEvent e)
    {
      lastPoint = normalize(e.getPoint());
      updateCursor(e.getPoint());
      initializeDragOperation(lastPoint, currentIndicator);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(final MouseEvent e)
    {
      if(lastPoint == null)
      {
        return;
      }

      if (lastPoint.equals(normalize(e.getPoint())) == false)
      {
        // only fire a drag operation if we moved the mouse
        finishDragOperation();
      }
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(final MouseEvent e)
    {
      updateCursor(e.getPoint());
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(final MouseEvent e)
    {
      updateCursor(e.getPoint());
    }
  }

  protected class CellEditorRemover implements PropertyChangeListener
  {
    private KeyboardFocusManager focusManager;

    public CellEditorRemover(final KeyboardFocusManager fm)
    {
      this.focusManager = fm;
    }

    public void propertyChange(final PropertyChangeEvent ev)
    {
      if (!isEditing() || isTerminateEditOnFocusLost() == false)
      {
        return;
      }

      Component c = focusManager.getPermanentFocusOwner();
      while (c != null)
      {
        if (c == AbstractRenderComponent.this)
        {
          // focus remains inside the table
          return;
        }
        else if ((c instanceof Window) || (c instanceof Applet && c.getParent() == null))
        {
          if (c == SwingUtilities.getRoot(AbstractRenderComponent.this))
          {
            if (!getCellEditor().stopCellEditing())
            {
              getCellEditor().cancelCellEditing();
            }
          }
          break;
        }
        c = c.getParent();
      }
    }
  }

  private class DragAbortReportModelListener implements ReportModelListener
  {
    private DragAbortReportModelListener()
    {
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      if (event.isNodeAddedEvent() || event.isNodeDeleteEvent())
      {
        finishDragOperation();
      }
    }
  }

  private class SelectionStateHandler implements PropertyChangeListener
  {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    public void propertyChange(final PropertyChangeEvent evt)
    {
      if (getDesignerContext().isSelectionWaiting())
      {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }
      else
      {
        updateCursorForIndicator();
      }
    }
  }

  protected class SelectionRectangleOverlayRenderer implements OverlayRenderer
  {
    public SelectionRectangleOverlayRenderer()
    {
    }

    public void validate(final ReportRenderContext context, final double zoomFactor)
    {
    }

    public void draw(final Graphics2D graphics, final Rectangle2D bounds, final ImageObserver obs)
    {
      paintSelectionRectangle(graphics);
    }
  }

  private CellEditorRemover editorRemover;
  private RepaintHandler repaintHandler;
  private SettingsUpdateHandler settingsUpdateHandler;
  private ReportDesignerContext designerContext;
  private ReportRenderContext renderContext;
  private boolean showLeftBorder;
  private boolean showTopBorder;
  private double gridSize;
  private int gridDivisions;
  private boolean terminateEditOnFocusLost;
  private Component editorComponent;
  private ReportElementInlineEditor inlineEditor;
  private MouseDragOperation operation;
  private MassElementStyleUndoEntryBuilder undoEntryBuilder;
  private FullSnapModel horizontalSnapModel;
  private FullSnapModel verticalSnapModel;
  private LinealModel verticalLinealModel;
  private LinealModel horizontalLinealModel;
  private HorizontalPositionsModel horizontalPositionsModel;
  private ElementRenderer elementRenderer;
//  private Color primaryGridColor;
//  private Color secondaryGridColor;
  private boolean focused;
  private SelectionOverlayInformation.InRangeIndicator currentIndicator;
  private SelectionStateHandler selectionStateHandler;
  private ArrayList<Object> oldValues = new ArrayList<Object>();


  protected AbstractRenderComponent(final ReportDesignerContext designerContext,
                                    final ReportRenderContext renderContext)
  {
    if (renderContext == null)
    {
      throw new NullPointerException();
    }
    if (designerContext == null)
    {
      throw new NullPointerException();
    }

    setDoubleBuffered(true);
    setOpaque(false);
    setFocusable(true);
    setFocusCycleRoot(true);
    setFocusTraversalKeysEnabled(false);
    setLayout(null);

    this.showLeftBorder = true;
    this.showTopBorder = false;
    this.repaintHandler = new RepaintHandler(this);
    this.designerContext = designerContext;
    this.renderContext = renderContext;
    this.settingsUpdateHandler = new SettingsUpdateHandler();
    this.horizontalSnapModel = new FullSnapModel();
    this.verticalSnapModel = new FullSnapModel();
    this.terminateEditOnFocusLost = true;

    gridSize = WorkspaceSettings.getInstance().getGridSize();
    gridDivisions = WorkspaceSettings.getInstance().getGridDivisions();

    WorkspaceSettings.getInstance().addSettingsListener(settingsUpdateHandler);

    new DropTarget(this, new BandDndHandler(this));
    renderContext.getZoomModel().addZoomModelListener(repaintHandler);
    renderContext.getReportDefinition().addReportModelListener(new DragAbortReportModelListener());
    addMouseListener(new MouseEditorActionHandler());
    addKeyListener(new KeyboardElementMoveHandler());

    selectionStateHandler = new SelectionStateHandler();
    designerContext.addPropertyChangeListener(ReportDesignerContext.SELECTION_WAITING_PROPERTY, selectionStateHandler);
  }

  public AbstractElementRenderer getRendererRoot()
  {
    return (AbstractElementRenderer)getElementRenderer();
  }

  public Band getRootBand()
  {
    return (Band)getRendererRoot().getElement();
  }

  public boolean isTerminateEditOnFocusLost()
  {
    return terminateEditOnFocusLost;
  }

  public void setTerminateEditOnFocusLost(final boolean terminateEditOnFocusLost)
  {
    this.terminateEditOnFocusLost = terminateEditOnFocusLost;
  }

  protected abstract boolean isLocalElement(ReportElement e);

  protected void installMouseOperationHandler()
  {
    // must be added *after* the selection handler
    final MouseOperationHandler operationHandler = new MouseOperationHandler();
    addMouseListener(operationHandler);
    addMouseMotionListener(operationHandler);
  }

  protected boolean isFocused()
  {
    return focused;
  }

  protected void setFocused(final boolean focused)
  {
    this.focused = focused;
  }

  public boolean isShowLeftBorder()
  {
    return showLeftBorder;
  }

  public void setShowLeftBorder(final boolean showLeftBorder)
  {
    this.showLeftBorder = showLeftBorder;
  }

  public boolean isShowTopBorder()
  {
    return showTopBorder;
  }

  public void setShowTopBorder(final boolean showTopBorder)
  {
    this.showTopBorder = showTopBorder;
  }

  protected double getLeftBorder()
  {
    if (renderContext == null)
    {
      return 0;
    }
    if (showLeftBorder == false)
    {
      return 0;
    }
    final PageDefinition pageDefinition = renderContext.getPageDefinition();
    final PageFormat pageFormat = pageDefinition.getPageFormat(0);
    final PageFormatFactory pageFormatFactory = PageFormatFactory.getInstance();
    return pageFormatFactory.getLeftBorder(pageFormat.getPaper());
  }

  protected double getTopBorder()
  {
    if (renderContext == null)
    {
      return 0;
    }
    if (showTopBorder == false)
    {
      return 0;
    }
    final PageDefinition pageDefinition = renderContext.getPageDefinition();
    final PageFormat pageFormat = pageDefinition.getPageFormat(0);
    final PageFormatFactory pageFormatFactory = PageFormatFactory.getInstance();
    return pageFormatFactory.getTopBorder(pageFormat.getPaper());
  }

  public Point2D normalize(final Point2D e)
  {
    final double topBorder = getTopBorder();
    final double leftBorder = getLeftBorder();

    final float scaleFactor = getRenderContext().getZoomModel().getZoomAsPercentage();
    final double x = (e.getX() / scaleFactor) - leftBorder;
    final double y = (e.getY() / scaleFactor) - topBorder;

    final Point2D o = getOffset();
    o.setLocation(x, y + o.getY());
    return o;
  }

  protected Point2D getOffset()
  {
    final StrictBounds bounds = elementRenderer.getRootElementBounds();
    return new Point2D.Double(StrictGeomUtility.toExternalValue(bounds.getX()),
        StrictGeomUtility.toExternalValue(bounds.getY()));
  }

  public ReportRenderContext getRenderContext()
  {
    return renderContext;
  }

  public ReportDesignerContext getDesignerContext()
  {
    return designerContext;
  }

  protected void paintComponent(final Graphics g)
  {
    final Graphics2D g2 = (Graphics2D) g.create();

    g2.setColor(new Color(224, 224, 224));
    g2.fillRect(0, 0, getWidth(), getHeight());
    
    final int leftBorder = (int) getLeftBorder();
    final int topBorder = (int) getTopBorder();
    final float scaleFactor = getRenderContext().getZoomModel().getZoomAsPercentage();

    // draw the page area ..
    final PageDefinition pageDefinition = getRenderContext().getPageDefinition();
    final Rectangle2D.Double area = new Rectangle2D.Double(0, 0, pageDefinition.getWidth() * scaleFactor, getHeight());
    g2.translate(leftBorder * scaleFactor, topBorder * scaleFactor);
    g2.clip(area);
    g2.setColor(Color.WHITE);
    g2.fill(area);

    // draw the grid (unscaled, but translated)
    final Point2D offset = getOffset();
    if (offset.getX() != 0)
    {
      final Rectangle2D.Double blackoutArea = new Rectangle2D.Double(0, 0, offset.getX() * scaleFactor, getHeight());
      g2.setColor(Color.LIGHT_GRAY);
      g2.fill(blackoutArea);
    }
    paintGrid(g2);
    paintElementAlignment(g2);
    g2.dispose();

    final Graphics2D logicalPageAreaG2 = (Graphics2D) g.create();
    // draw the renderable content ...
    logicalPageAreaG2.translate(leftBorder * scaleFactor, topBorder * scaleFactor);
    logicalPageAreaG2.clip(area);
    logicalPageAreaG2.scale(scaleFactor, scaleFactor);

    try
    {
      final ElementRenderer rendererRoot = getElementRenderer();
      if (rendererRoot != null)
      {
        rendererRoot.draw(logicalPageAreaG2);
      }
    }
    catch (Exception e)
    {
      // ignore for now..
      UncaughtExceptionsModel.getInstance().addException(e);
    }

    logicalPageAreaG2.dispose();

    final OverlayRenderer[] renderers = new OverlayRenderer[4];
    renderers[0] = new OverlappingElementOverlayRenderer(getDefaultElement());
    renderers[1] = new SelectionOverlayRenderer(getDefaultElement());
    renderers[2] = new GuidelineOverlayRenderer(horizontalLinealModel, verticalLinealModel);
    renderers[3] = new SelectionRectangleOverlayRenderer();

    for (int i = 0; i < renderers.length; i++)
    {
      final OverlayRenderer renderer = renderers[i];
      final Graphics2D selectionG2 = (Graphics2D) g.create();
      selectionG2.translate(0, -offset.getY() * scaleFactor);
      
      renderer.validate(getRenderContext(), scaleFactor);
      renderer.draw(selectionG2, new Rectangle2D.Double(getLeftBorder(), getTopBorder(), getWidth(), getHeight()), this);
      selectionG2.dispose();
    }
  }

  protected abstract void paintSelectionRectangle(final Graphics2D g2);

  protected void paintGrid(final Graphics2D g2d)
  {
    if (WorkspaceSettings.getInstance().isShowGrid())
    {
      final float scaleFactor = getRenderContext().getZoomModel().getZoomAsPercentage();
      final double gridSize = getGridSize() * scaleFactor;
      if (gridSize < 1)
      {
        return;
      }

      final int gridDivisions = Math.max(1, getGridDivisions());

      final Color primaryColor = WorkspaceSettings.getInstance().getGridColor();
      final Color secondaryColor = ColorUtility.convertToBrighter(primaryColor);
      // draw vertical lines
      g2d.setStroke(new BasicStroke(.1f));
      int horizontalLineCount = 0;
      final Line2D.Double line = new Line2D.Double();
      final double gridHeight = getHeight();
      final double gridWidth = getWidth();
      for (double w = gridSize; w < gridWidth; w += gridSize)
      {
        if (horizontalLineCount % gridDivisions == gridDivisions - 1)
        {
          g2d.setColor(primaryColor);
        }
        else
        {
          g2d.setColor(secondaryColor);
        }
        horizontalLineCount++;
        line.setLine(w, 0, w, gridHeight);
        g2d.draw(line);
      }

      // draw horizontal lines
      int verticalLineCount = 0;
      for (double h = gridSize; h < gridHeight; h += gridSize)
      {
        if (verticalLineCount % gridDivisions == gridDivisions - 1)
        {
          g2d.setColor(primaryColor);
        }
        else
        {
          g2d.setColor(secondaryColor);
        }
        verticalLineCount++;
        line.setLine(0, h, gridWidth, h);
        g2d.draw(line);
      }
    }
  }
  
  protected void paintElementAlignment(final Graphics2D g2d)
  {
    if (WorkspaceSettings.getInstance().isShowElementAlignmentHints())
    {
      final float scaleFactor = getRenderContext().getZoomModel().getZoomAsPercentage();
      g2d.setColor(WorkspaceSettings.getInstance().getAlignmentHintColor());
      g2d.setStroke(new BasicStroke(.2f));

      final double gridHeight = getHeight();
      final double gridWidth = getWidth();
      final long[] hPositions;
      if (getHorizontalPositionsModel() == null)
      {
        final BreakPositionsList horizontalPositions = getHorizontalEdgePositions();
        hPositions = horizontalPositions.getKeys();
      }
      else
      {
        hPositions = getHorizontalPositionsModel().getBreaks();
      }
      final Line2D.Double line = new Line2D.Double();
      for (int i = 0; i < hPositions.length; i++)
      {
        final double position = StrictGeomUtility.toExternalValue(hPositions[i]);
        final double x = position * scaleFactor;
        line.setLine(x, 0, x, gridHeight);
        g2d.draw(line);
      }

      final Point2D offset = getOffset();
      final BreakPositionsList verticalPositions = getVerticalEdgePositions();
      final long[] vPositions = verticalPositions.getKeys();
      for (int i = 0; i < vPositions.length; i++)
      {
        final double position = StrictGeomUtility.toExternalValue(vPositions[i]) - offset.getY();
        final double y2 = position * scaleFactor;
        line.setLine(0, y2, gridWidth, y2);
        g2d.draw(line);
      }
    }

  }

  protected void updateGridSettings()
  {
    gridSize = WorkspaceSettings.getInstance().getGridSize();
    gridDivisions = WorkspaceSettings.getInstance().getGridDivisions();
  }

  public double getGridSize()
  {
    return gridSize;
  }

  public int getGridDivisions()
  {
    return gridDivisions;
  }

  public void dispose()
  {
    WorkspaceSettings.getInstance().removeSettingsListener(settingsUpdateHandler);

    if (this.verticalLinealModel != null)
    {
      this.verticalLinealModel.removeLinealModelListener(repaintHandler);
    }
    if (this.horizontalLinealModel != null)
    {
      this.horizontalLinealModel.removeLinealModelListener(repaintHandler);
    }
    if (this.elementRenderer != null)
    {
      this.elementRenderer.removeChangeListener(repaintHandler);
    }

    designerContext.removePropertyChangeListener(ReportDesignerContext.SELECTION_WAITING_PROPERTY, selectionStateHandler);
  }

  protected void removeEditor()
  {
    if (editorRemover != null)
    {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener
          ("permanentFocusOwner", editorRemover); // NON-NLS
      editorRemover = null;
    }
    if (editorComponent == null)
    {
      inlineEditor = null;
      return;
    }

    remove(editorComponent);
    inlineEditor.removeCellEditorListener(this);

    editorComponent = null;
    inlineEditor = null;
  }

  protected ReportElementInlineEditor getCellEditor()
  {
    return inlineEditor;
  }

  protected boolean installEditor(final ReportElementInlineEditor inlineEditor, final Element element)
  {
    if (inlineEditor == null)
    {
      throw new NullPointerException();
    }

    this.inlineEditor = inlineEditor;

    final CachedLayoutData data = ModelUtility.getCachedLayoutData(element);
    if (data == null)
    {
      removeEditor();
      return false;
    }
    final Component editorComponent = inlineEditor.getElementCellEditorComponent(this, element);
    if (editorComponent == null)
    {
      removeEditor();
      return false;
    }

    if (editorRemover == null)
    {
      final KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      editorRemover = new CellEditorRemover(fm);
      fm.addPropertyChangeListener("permanentFocusOwner", editorRemover); // NON-NLS
    }

    this.editorComponent = editorComponent;

    final float zoomFactor = getRenderContext().getZoomModel().getZoomAsPercentage();

    final int x = (int) ((getLeftBorder() + StrictGeomUtility.toExternalValue(data.getX())) * zoomFactor);
    final int y = (int) ((getTopBorder() + StrictGeomUtility.toExternalValue(data.getY())) * zoomFactor);
    final int width = (int) (StrictGeomUtility.toExternalValue(data.getWidth()) * zoomFactor);
    final int height = (int) (StrictGeomUtility.toExternalValue(data.getHeight()) * zoomFactor);
    editorComponent.setBounds(x, y, width, height);
    add(editorComponent);
    editorComponent.validate();
    inlineEditor.addCellEditorListener(this);

    final Element[] visualElements = getRenderContext().getSelectionModel().getSelectedVisualElements();
    if (visualElements.length > 0)
    {
      oldValues = new ArrayList<Object>();
      for (int i = 0; i < visualElements.length; i++)
      {
        final Object attribute = visualElements[i].getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE);
        oldValues.add(attribute);
      }
    }

    return true;
  }

  protected boolean isEditing()
  {
    return inlineEditor != null;
  }

  public void editingStopped(final ChangeEvent e)
  {
    final Element[] visualElements = getRenderContext().getSelectionModel().getSelectedVisualElements();
    if (visualElements.length > 0)
    {
      final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
      for (int i = 0; i < visualElements.length; i++)
      {
        final Object attribute = visualElements[i].getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE);
        undos.add(new AttributeEditUndoEntry(visualElements[i].getObjectID(), AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, oldValues.get(i),
            attribute));
      }
      getRenderContext().getUndo().addChange(Messages.getString("AbstractRenderComponent.InlineEditUndoName"),
          new CompoundUndoEntry((UndoEntry[]) undos.toArray(new UndoEntry[undos.size()])));
    }

    removeEditor();
  }

  public void editingCanceled(final ChangeEvent e)
  {
    operation = null;
    undoEntryBuilder = null;
    removeEditor();
  }

  public JComponent getRepresentationContainer()
  {
    return this;
  }

  public LinealModel getVerticalLinealModel()
  {
    return verticalLinealModel;
  }

  public LinealModel getHorizontalLinealModel()
  {
    return horizontalLinealModel;
  }

  public HorizontalPositionsModel getHorizontalPositionsModel()
  {
    return horizontalPositionsModel;
  }

  protected void updateElements(final Point2D normalizedPoint, final boolean snapToGrid, final boolean snapToElements)
  {
    if (operation != null)
    {
      horizontalSnapModel.setEnableElements(snapToElements || WorkspaceSettings.getInstance().isSnapToElements());
      horizontalSnapModel.setEnableGrid(snapToGrid || WorkspaceSettings.getInstance().isSnapToGrid());
      horizontalSnapModel.setEnableGuides(true);
      verticalSnapModel.setEnableElements(snapToElements || WorkspaceSettings.getInstance().isSnapToElements());
      verticalSnapModel.setEnableGrid(snapToGrid || WorkspaceSettings.getInstance().isSnapToGrid());
      verticalSnapModel.setEnableGuides(true);
      operation.update(normalizedPoint, getRenderContext().getZoomModel().getZoomAsPercentage());
    }
  }

  /**
   * Returns the break positions for inner-band drag-operations (snap to element).
   *
   * @return the edge positions of all elements.
   */
  protected abstract BreakPositionsList getHorizontalEdgePositions();

  /**
   * Returns the break positions for inner-band drag-operations (snap to element).
   *
   * @return the edge positions of all elements.
   */
  protected abstract BreakPositionsList getVerticalEdgePositions();

  protected void initializeDragOperation(final Point2D originPoint,
                                         final SelectionOverlayInformation.InRangeIndicator currentIndicator)
  {
    final Element[] visualElements = getRenderContext().getSelectionModel().getSelectedVisualElements();
    if (visualElements.length == 0)
    {
      return;
    }

    horizontalSnapModel.getGridModel().setGridSize(StrictGeomUtility.toInternalValue(getGridSize()));
    verticalSnapModel.getGridModel().setGridSize(StrictGeomUtility.toInternalValue(getGridSize()));
    horizontalSnapModel.setEnableGrid(WorkspaceSettings.getInstance().isSnapToGrid());
    verticalSnapModel.setEnableGrid(WorkspaceSettings.getInstance().isSnapToGrid());

    final SnapToPositionModel horizontalGuildesPositions = horizontalSnapModel.getGuidesModel();
    horizontalGuildesPositions.clear();
    final GuideLine[] hlines = horizontalLinealModel.getGuideLines();
    for (int i = 0; i < hlines.length; i++)
    {
      final GuideLine guideLine = hlines[i];
      if (guideLine.isActive())
      {
        horizontalGuildesPositions.add(StrictGeomUtility.toInternalValue(guideLine.getPosition()), null);
      }
    }

    final SnapToPositionModel verticalGuidesPositions = verticalSnapModel.getGuidesModel();
    verticalGuidesPositions.clear();
    final GuideLine[] vlines = verticalLinealModel.getGuideLines();
    for (int i = 0; i < vlines.length; i++)
    {
      final GuideLine guideLine = vlines[i];
      if (guideLine.isActive())
      {
        verticalGuidesPositions.add(StrictGeomUtility.toInternalValue(guideLine.getPosition()), null);
      }
    }

    final SnapToPositionModel hElementModel = horizontalSnapModel.getElementModel();
    hElementModel.clear();
    final BreakPositionsList horizontalPositions = getHorizontalEdgePositions();
    final long[] horizontalKeys;
    if (horizontalPositionsModel == null)
    {
      horizontalKeys = horizontalPositions.getKeys();
    }
    else
    {
      horizontalKeys = horizontalPositionsModel.getBreaks();
    }

    for (int i = 0; i < horizontalKeys.length; i++)
    {
      final long key = horizontalKeys[i];
      hElementModel.add(key, horizontalPositions.getOwner(key));
    }

    final SnapToPositionModel vElementModel = verticalSnapModel.getElementModel();
    vElementModel.clear();
    final BreakPositionsList verticalPositions = getVerticalEdgePositions();
    final long[] verticalKeys = verticalPositions.getKeys();
    for (int i = 0; i < verticalKeys.length; i++)
    {
      final long key = verticalKeys[i];
      vElementModel.add(key, verticalPositions.getOwner(key));
    }

    switch (currentIndicator)
    {
      case MOVE:
        operation = new MoveDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel);
        break;
      case BOTTOM_CENTER:
        operation = new ResizeBottomDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel);
        break;
      case MIDDLE_RIGHT:
        operation = new ResizeRightDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel);
        break;
      case MIDDLE_LEFT:
        operation = new ResizeLeftDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel);
        break;
      case TOP_CENTER:
        operation = new ResizeTopDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel);
        break;
      case BOTTOM_LEFT:
      {
        final CompoundDragOperation op = new CompoundDragOperation();
        op.add(new ResizeLeftDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel));
        op.add(new ResizeBottomDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel));
        operation = op;
        break;
      }
      case BOTTOM_RIGHT:
      {
        final CompoundDragOperation op = new CompoundDragOperation();
        op.add(new ResizeRightDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel));
        op.add(new ResizeBottomDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel));
        operation = op;
        break;
      }
      case TOP_LEFT:
      {
        final CompoundDragOperation op = new CompoundDragOperation();
        op.add(new ResizeLeftDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel));
        op.add(new ResizeTopDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel));
        operation = op;
        break;
      }
      case TOP_RIGHT:
      {
        final CompoundDragOperation op = new CompoundDragOperation();
        op.add(new ResizeRightDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel));
        op.add(new ResizeTopDragOperation(visualElements, originPoint, horizontalSnapModel, verticalSnapModel));
        operation = op;
        break;
      }
      default:
    }

    if (operation != null)
    {
      undoEntryBuilder = new MassElementStyleUndoEntryBuilder(visualElements);
    }
  }

  protected void finishDragOperation()
  {
    if (operation != null)
    {
      operation.finish();

      final MassElementStyleUndoEntry undoEntry = undoEntryBuilder.finish();
      getRenderContext().getUndo().addChange(Messages.getString("AbstractRenderComponent.ResizeUndoName"), undoEntry);
    }

    operation = null;
    undoEntryBuilder = null;
    repaint();
  }

  protected boolean isMouseOperationInProgress()
  {
    return operation != null;
  }

  protected boolean isMouseOperationPossible()
  {
    return currentIndicator != null && currentIndicator != SelectionOverlayInformation.InRangeIndicator.NOT_IN_RANGE;
  }

  protected void installLineals(final ElementRenderer elementRenderer, final LinealModel horizontalLinealModel,
                                final HorizontalPositionsModel horizontalPositionsModel)
  {
    this.elementRenderer = elementRenderer;
    final LinealModel verticalLinealModel;
    if (elementRenderer != null)
    {
      verticalLinealModel = elementRenderer.getVerticalLinealModel();
    }
    else
    {
      verticalLinealModel = null;
    }

    if (this.verticalLinealModel != null)
    {
      this.verticalLinealModel.removeLinealModelListener(repaintHandler);
    }
    if (this.horizontalLinealModel != null)
    {
      this.horizontalLinealModel.removeLinealModelListener(repaintHandler);
    }

    this.horizontalPositionsModel = horizontalPositionsModel;
    this.verticalLinealModel = verticalLinealModel;
    this.horizontalLinealModel = horizontalLinealModel;

    if (this.verticalLinealModel != null)
    {
      this.verticalLinealModel.addLinealModelListener(repaintHandler);
    }
    if (this.horizontalLinealModel != null)
    {
      this.horizontalLinealModel.addLinealModelListener(repaintHandler);
    }

    if (this.elementRenderer != null)
    {
      this.elementRenderer.removeChangeListener(repaintHandler);
    }
    this.elementRenderer = elementRenderer;
    if (this.elementRenderer != null)
    {
      this.elementRenderer.addChangeListener(repaintHandler);
    }
  }

  protected RootLevelBand findRootBandForPosition(final Point2D point)
  {
    return null;
  }

  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  protected ElementRenderer getElementRenderer()
  {
    return elementRenderer;
  }

  public Dimension getPreferredSize()
  {
    final ElementRenderer rendererRoot = getElementRenderer();
    if (rendererRoot == null)
    {
      return new Dimension(0, 0);
    }

    final float zoom = getRenderContext().getZoomModel().getZoomAsPercentage();
    try
    {
      final Rectangle2D bounds = rendererRoot.getBounds();
      final int leftBorder;
      if (isShowLeftBorder())
      {
        leftBorder = (int) getLeftBorder();
      }
      else
      {
        leftBorder = 0;
      }

      final int topBorder;
      if (isShowTopBorder())
      {
        topBorder = (int) getTopBorder();
      }
      else
      {
        topBorder = 0;
      }

      return new Dimension((int) (zoom * (leftBorder + bounds.getWidth())), (int) (zoom * (topBorder + bounds.getHeight())));
    }
    catch (Exception e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
      return new Dimension(0, (int) (zoom * rendererRoot.getVisualHeight()));
    }
  }

  public void removeNotify()
  {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener
        ("permanentFocusOwner", editorRemover); // NON-NLS
    editorRemover = null;
    super.removeNotify();
  }

  protected boolean stopCellEditing()
  {
    if (isEditing() == false)
    {
      return true;
    }
    final ReportElementInlineEditor elementInlineEditor = getCellEditor();
    if (elementInlineEditor == null)
    {
      return true;
    }
    return elementInlineEditor.stopCellEditing();
  }

  protected void updateCursorForIndicator()
  {
    if (currentIndicator == null)
    {
      setCursor(Cursor.getDefaultCursor());
      return;
    }
    switch (currentIndicator)
    {
      case NOT_IN_RANGE:
        setCursor(Cursor.getDefaultCursor());
        break;
      case MOVE:
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        break;
      case BOTTOM_CENTER:
        setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        break;
      case BOTTOM_LEFT:
        setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
        break;
      case BOTTOM_RIGHT:
        setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        break;
      case MIDDLE_LEFT:
        setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        break;
      case MIDDLE_RIGHT:
        setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        break;
      case TOP_LEFT:
        setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
        break;
      case TOP_CENTER:
        setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        break;
      case TOP_RIGHT:
        setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
        break;
    }
  }
}
