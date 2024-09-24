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

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Section;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public class SelectionOverlayRenderer implements OverlayRenderer {
  private Section rootElement;
  private ReportDocumentContext context;
  private double zoomFactor;
  private double offset;

  public SelectionOverlayRenderer( final Element defaultElement ) {
    if ( defaultElement instanceof Section ) {
      this.rootElement = (Section) defaultElement;
    }
  }

  public void validate( final ReportDocumentContext context, final double zoomFactor, final Point2D sectionOffset ) {
    this.offset = sectionOffset.getY();
    this.context = context;
    this.zoomFactor = zoomFactor;
  }

  public void draw( final Graphics2D graphics, final Rectangle2D bounds, final ImageObserver obs ) {
    if ( context == null || rootElement == null ) {
      return;
    }

    graphics.translate( bounds.getX(), -( offset * zoomFactor ) );

    for ( final Element visualElement : context.getSelectionModel().getSelectedElementsOfType( Element.class ) ) {
      if ( ModelUtility.isDescendant( rootElement, visualElement ) == false ) {
        continue;
      }
      final Object o = visualElement
        .getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.SELECTION_OVERLAY_INFORMATION );
      if ( o instanceof SelectionOverlayInformation == false ) {
        continue;
      }

      final SelectionOverlayInformation information = (SelectionOverlayInformation) o;
      information.validate( zoomFactor );
      information.draw( graphics, obs );
    }
  }
}
