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
