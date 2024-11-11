/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
