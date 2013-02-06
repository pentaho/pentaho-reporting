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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.CrosstabRenderer;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;

/**
 * Manages the mouse selection inside the crosstab subreport
 *
 * @author Thomas Morgner
 */
public class CrosstabRenderComponent extends AbstractRenderComponent
{
  private static final BasicStroke SELECTION_STROKE = new BasicStroke(0.5f);

  private static final SelectionOverlayInformation[] EMPTY_OVERLAYS = new SelectionOverlayInformation[0];
  private BreakPositionsList horizontalEdgePositions;
  private BreakPositionsList verticalEdgePositions;
  private CrosstabRenderer rendererElementRoot;
  private RequestFocusHandler focusHandler;
  private RootBandChangeHandler changeHandler;
  private MouseSelectionHandler selectionHandler;
  private SelectionModelListener selectionModelListener;


  public CrosstabRenderComponent(final ReportDesignerContext designerContext,
                                 final ReportRenderContext renderContext)
  {
    super(designerContext, renderContext);
    this.horizontalEdgePositions = new BreakPositionsList();
    this.verticalEdgePositions = new BreakPositionsList();
    this.changeHandler = new RootBandChangeHandler();

    this.selectionModelListener = new SelectionModelListener();
    renderContext.getSelectionModel().addReportSelectionListener(selectionModelListener);

    this.selectionHandler = new MouseSelectionHandler();
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
  }


  public Dimension getPreferredSize()
  {
    return super.getPreferredSize();
  }

  public Element getElementForLocation(final Point2D normalizedPoint, final boolean onlySelected)
  {
    return null;
  }

  protected RootLevelBand findRootBandForPosition(final Point2D point)
  {
    return null;
  }

  protected BreakPositionsList getHorizontalEdgePositions()
  {
    return horizontalEdgePositions;
  }

  protected BreakPositionsList getVerticalEdgePositions()
  {
    return verticalEdgePositions;
  }

  protected SelectionOverlayInformation[] getOverlayRenderers()
  {
    return EMPTY_OVERLAYS;
  }

  public void installRenderer(final CrosstabRenderer rendererRoot,
                              final LinealModel horizontalLinealModel,
                              final HorizontalPositionsModel horizontalPositionsModel)
  {
    this.rendererElementRoot = rendererRoot;
    super.installLineals(rendererRoot, horizontalLinealModel, horizontalPositionsModel);
  }

  public Element getDefaultElement()
  {
    if (rendererElementRoot == null)
    {
      return null;
    }
    return rendererElementRoot.getCrosstabGroup();
  }

  public CrosstabRenderer getRendererRoot()
  {
    return (CrosstabRenderer)getElementRenderer();
  }

  protected boolean isLocalElement(final ReportElement e)
  {
    return ModelUtility.isDescendant((CrosstabGroup)getDefaultElement(), e);
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
}
