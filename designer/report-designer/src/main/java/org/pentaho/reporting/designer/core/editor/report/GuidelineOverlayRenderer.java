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
import org.pentaho.reporting.designer.core.model.lineal.GuideLine;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public class GuidelineOverlayRenderer implements OverlayRenderer {
  private double scaleFactor;
  private LinealModel verticalLinealModel;
  private LinealModel horizontalLinealModel;

  public GuidelineOverlayRenderer( final LinealModel horizontalLinealModel, final LinealModel verticalLinealModel ) {
    this.horizontalLinealModel = horizontalLinealModel;
    this.verticalLinealModel = verticalLinealModel;
  }

  public void validate( final ReportDocumentContext context, final double zoomFactor, final Point2D sectionOffset ) {
    this.scaleFactor = zoomFactor;
  }

  public void draw( final Graphics2D g2, final Rectangle2D bounds, final ImageObserver obs ) {
    g2.setStroke( new BasicStroke( 0.2f ) );
    final Color guideColor = WorkspaceSettings.getInstance().getGuideColor();
    g2.setColor( guideColor );
    if ( verticalLinealModel != null ) {
      final double topBorder = bounds.getY();
      final GuideLine[] vlines = verticalLinealModel.getGuideLines();
      final int gridwidth = (int) bounds.getWidth();
      for ( int i = 0; i < vlines.length; i++ ) {
        final GuideLine line = vlines[ i ];
        if ( line.isActive() ) {
          final double h = line.getPosition() + topBorder;
          final double linePos = h * scaleFactor;
          g2.drawLine( 0, (int) linePos, gridwidth, (int) ( h * scaleFactor ) );
        }
      }
    }
    if ( horizontalLinealModel != null ) {
      final double leftBorder = bounds.getX();
      final GuideLine[] hlines = horizontalLinealModel.getGuideLines();
      final int gridHeight = (int) bounds.getHeight();
      for ( int i = 0; i < hlines.length; i++ ) {
        final GuideLine line = hlines[ i ];
        if ( line.isActive() ) {
          final double w = line.getPosition() + leftBorder;
          final double linePos = w * scaleFactor;
          g2.drawLine( (int) linePos, 0, (int) linePos, gridHeight );
        }
      }
    }
  }

}
