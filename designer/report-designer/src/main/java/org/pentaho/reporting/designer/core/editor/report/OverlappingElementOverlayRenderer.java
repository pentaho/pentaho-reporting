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

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public class OverlappingElementOverlayRenderer implements OverlayRenderer {
  private Element rootElement;
  private double zoomFactor;
  private Rectangle2D elementBounds;
  private double offset;

  public OverlappingElementOverlayRenderer( final Element defaultElement ) {
    this.elementBounds = new Rectangle2D.Double();
    this.rootElement = defaultElement;
  }

  public void validate( final ReportDocumentContext context, final double zoomFactor, final Point2D sectionOffset ) {
    this.zoomFactor = zoomFactor;
    this.offset = sectionOffset.getY();
  }

  public void draw( final Graphics2D graphics, final Rectangle2D bounds, final ImageObserver obs ) {
    if ( WorkspaceSettings.getInstance().isShowOverlappingElements() == false ) {
      return;
    }

    graphics.translate( 0, -( offset * zoomFactor ) );

    draw( rootElement, graphics );
  }

  private void draw( final Element element, final Graphics2D graphics2D ) {
    final CachedLayoutData layoutData = ModelUtility.getCachedLayoutData( element );
    if ( layoutData.getLayoutAge() > -1 && layoutData.isConflictsInTableMode() ) {
      final double x = StrictGeomUtility.toExternalValue( layoutData.getX() );
      final double y = StrictGeomUtility.toExternalValue( layoutData.getY() );
      final double width = StrictGeomUtility.toExternalValue( layoutData.getWidth() );
      final double height = StrictGeomUtility.toExternalValue( layoutData.getHeight() );
      elementBounds.setFrame( x * zoomFactor, y * zoomFactor, width * zoomFactor, height * zoomFactor );

      final Color overlapErrorColor = WorkspaceSettings.getInstance().getOverlapErrorColor();
      final Color highLight =
        new Color( overlapErrorColor.getRed(), overlapErrorColor.getGreen(), overlapErrorColor.getBlue(), 64 );

      graphics2D.setPaint( highLight );
      graphics2D.fill( elementBounds );
    }

    if ( element instanceof SubReport && element != rootElement ) {
      return;
    }
    if ( element instanceof Section ) {
      final Section section = (Section) element;
      final int count = section.getElementCount();
      for ( int i = 0; i < count; i++ ) {
        final ReportElement e = section.getElement( i );
        if ( e instanceof Element ) {
          draw( (Element) e, graphics2D );
        }
      }
    }
  }
}
