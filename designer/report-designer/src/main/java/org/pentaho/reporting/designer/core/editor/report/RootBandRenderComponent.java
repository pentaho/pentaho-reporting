/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.ElementRenderer;
import org.pentaho.reporting.designer.core.editor.report.layouting.RootBandRenderer;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class RootBandRenderComponent extends AbstractRenderComponent {
  private RootBandRenderer elementRenderer;

  public RootBandRenderComponent( final ReportDesignerContext designerContext,
                                  final ReportDocumentContext renderContext,
                                  final boolean showTopBorder ) {
    super( designerContext, renderContext );
    setShowTopBorder( showTopBorder );
  }

  public Element getDefaultElement() {
    if ( elementRenderer == null ) {
      return null;
    }
    return elementRenderer.getElement();
  }

  public RootBandRenderer getRendererRoot() {
    return (RootBandRenderer) getElementRenderer();
  }

  public void installRenderer( final RootBandRenderer rendererRoot, final LinealModel horizontalLinealModel,
                               final HorizontalPositionsModel horizontalPositionsModel ) {
    this.elementRenderer = rendererRoot;
    super.installLineals( horizontalLinealModel, horizontalPositionsModel );
  }

  protected boolean isLocalElement( final ReportElement e ) {
    if ( e == getRootBand() ) {
      return false;
    }
    return ModelUtility.isDescendant( getRootBand(), e );
  }

  public ElementRenderer getElementRenderer() {
    return elementRenderer;
  }
}
