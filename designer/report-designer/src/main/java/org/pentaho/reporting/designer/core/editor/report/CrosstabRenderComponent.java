/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.CrosstabRenderer;
import org.pentaho.reporting.designer.core.editor.report.layouting.ElementRenderer;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;

/**
 * Manages the mouse selection inside the crosstab subreport
 *
 * @author Sulaiman Karmali
 */
public class CrosstabRenderComponent extends AbstractRenderComponent {
  private CrosstabRenderer elementRenderer;

  public CrosstabRenderComponent( final ReportDesignerContext designerContext,
                                  final ReportDocumentContext renderContext ) {
    super( designerContext, renderContext );
  }

  public void installRenderer( final CrosstabRenderer rendererRoot,
                               final LinealModel horizontalLinealModel,
                               final HorizontalPositionsModel horizontalPositionsModel ) {
    this.elementRenderer = rendererRoot;
    super.installLineals( horizontalLinealModel, horizontalPositionsModel );
  }

  public Element getDefaultElement() {
    if ( elementRenderer == null ) {
      return null;
    }
    return elementRenderer.getCrosstabGroup();
  }

  public CrosstabRenderer getRendererRoot() {
    return (CrosstabRenderer) getElementRenderer();
  }

  public Band getRootBand() {
    return getRendererRoot().getElement().getParent();
  }

  protected boolean isLocalElement( final ReportElement e ) {
    return ModelUtility.isDescendant( (CrosstabGroup) getDefaultElement(), e );
  }

  public ElementRenderer getElementRenderer() {
    return elementRenderer;
  }
}
