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

import java.awt.geom.Point2D;
import java.util.HashMap;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.RootBandRenderer;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class RootBandRenderComponent extends AbstractRenderComponent
{
  private RootBandRenderer rendererElementRoot;

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
  }

  public void dispose()
  {
    super.dispose();
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

  protected RootLevelBand findRootBandForPosition(final Point2D point)
  {
    if (rendererElementRoot == null)
    {
      return null;
    }
    final Section section = rendererElementRoot.getElement();
    if (section instanceof RootLevelBand)
    {
      return (RootLevelBand) section;
    }
    return null;
  }

  public Element getDefaultElement()
  {
    if (rendererElementRoot == null)
    {
      return null;
    }
    return rendererElementRoot.getElement();
  }


  public RootBandRenderer getRendererRoot()
  {
    return (RootBandRenderer)getElementRenderer();
  }

  public void installRenderer(final RootBandRenderer rendererRoot, final LinealModel horizontalLinealModel,
                              final HorizontalPositionsModel horizontalPositionsModel)
  {
    super.installLineals(rendererRoot, horizontalLinealModel, horizontalPositionsModel);
    this.rendererElementRoot = rendererRoot;
  }


  protected boolean isLocalElement(final ReportElement e)
  {
    return ModelUtility.isDescendant(getRootBand(), e);
  }
}
