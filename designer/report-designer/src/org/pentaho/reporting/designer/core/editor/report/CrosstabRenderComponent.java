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

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.CrosstabRenderer;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.engine.classic.core.Band;
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
  private CrosstabRenderer rendererElementRoot;

  public CrosstabRenderComponent(final ReportDesignerContext designerContext,
                                 final ReportRenderContext renderContext)
  {
    super(designerContext, renderContext);
  }

  public void dispose()
  {
    super.dispose();
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

  public Band getRootBand()
  {
    return getRendererRoot().getElement().getParent();
  }

  protected boolean isLocalElement(final ReportElement e)
  {
    return ModelUtility.isDescendant((CrosstabGroup)getDefaultElement(), e);
  }
}
