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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.RootBandRenderer;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
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
  private AbstractRenderComponent.SelectionModelListener selectionModelListener;

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
