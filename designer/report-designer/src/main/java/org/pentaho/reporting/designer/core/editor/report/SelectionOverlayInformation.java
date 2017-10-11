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

import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.DrawSelectionType;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

/**
 * The selection overlay has a coordinate system that matches the origin (0,0) of the band. However, internally it holds
 * all coordinates as *non*-normalized coordinates, so the zoom-factor is already calculated in.
 *
 * @author Thomas Morgner
 */
public class SelectionOverlayInformation {
  public enum InRangeIndicator {
    NOT_IN_RANGE,
    MOVE,
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    MIDDLE_LEFT,
    MIDDLE_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT,
  }

  private static final Color SELECTION_COLOR = Color.BLUE;


  private double zoomFactor;
  private Element selectedElement;
  private Rectangle2D.Double elementBounds;
  private Rectangle2D.Double nearRangeElementBounds;
  private long layoutAge;
  private static final BasicStroke DOTTED_STROKE = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1,
    new float[] { 2, 2 }, 1 );
  private static final int SELECTION_RANGE = 3;
  private CachedLayoutData selectedElementData;


  public SelectionOverlayInformation( final Element selectedElement ) {
    this.selectedElement = selectedElement;
    this.selectedElementData = ModelUtility.getCachedLayoutData( selectedElement );
    elementBounds = new Rectangle2D.Double();
    nearRangeElementBounds = new Rectangle2D.Double();
    layoutAge = -1;
  }

  public Element getSelectedElement() {
    return selectedElement;
  }

  public double getZoomFactor() {
    return zoomFactor;
  }

  public void validate( final double zoomFactor ) {

    final double oldZoom = this.zoomFactor;
    if ( oldZoom != zoomFactor || this.layoutAge != selectedElementData.getLayoutAge() ) {
      this.zoomFactor = zoomFactor;
      this.layoutAge = selectedElementData.getLayoutAge();

      final double x = StrictGeomUtility.toExternalValue( selectedElementData.getX() );
      final double y = StrictGeomUtility.toExternalValue( selectedElementData.getY() );
      final double width = StrictGeomUtility.toExternalValue( selectedElementData.getWidth() );
      final double height = StrictGeomUtility.toExternalValue( selectedElementData.getHeight() );
      elementBounds.setFrame( x * zoomFactor, y * zoomFactor, width * zoomFactor, height * zoomFactor );
      nearRangeElementBounds.setFrame
        ( elementBounds.getX() - SELECTION_RANGE, elementBounds.getY() - SELECTION_RANGE,
          elementBounds.getWidth() + 2 * SELECTION_RANGE, elementBounds.getHeight() + 2 * SELECTION_RANGE );

    }
  }

  public InRangeIndicator getMouseInRangeIndicator( final Point2D normalizedPoint ) {
    final double x = normalizedPoint.getX() * zoomFactor;
    final double y = normalizedPoint.getY() * zoomFactor;

    // See if we are close ... if not we should return fast
    if ( nearRangeElementBounds.contains( x, y ) == false ) {
      return InRangeIndicator.NOT_IN_RANGE;
    }

    // We are close ... see if we are on an important edge
    if ( isNear( x, elementBounds.getX() ) ) {
      if ( isNear( y, elementBounds.getY() ) ) {
        return InRangeIndicator.TOP_LEFT;
      } else if ( isNear( y, elementBounds.getY() + ( elementBounds.getHeight() / 2 ) ) ) {
        return InRangeIndicator.MIDDLE_LEFT;
      } else if ( isNear( y, elementBounds.getY() + elementBounds.getHeight() ) ) {
        return InRangeIndicator.BOTTOM_LEFT;
      }
    } else if ( isNear( x, elementBounds.getX() + ( elementBounds.getWidth() / 2 ) ) ) {
      if ( isNear( y, elementBounds.getY() ) ) {
        return InRangeIndicator.TOP_CENTER;
      } else if ( isNear( y, elementBounds.getY() + elementBounds.getHeight() ) ) {
        return InRangeIndicator.BOTTOM_CENTER;
      }
    } else if ( isNear( x, elementBounds.getX() + elementBounds.getWidth() ) ) {
      if ( isNear( y, elementBounds.getY() ) ) {
        return InRangeIndicator.TOP_RIGHT;
      } else if ( isNear( y, elementBounds.getY() + ( elementBounds.getHeight() / 2 ) ) ) {
        return InRangeIndicator.MIDDLE_RIGHT;
      } else if ( isNear( y, elementBounds.getY() + elementBounds.getHeight() ) ) {
        return InRangeIndicator.BOTTOM_RIGHT;
      }
    }

    // We are not on an important edge ... see if the point is in range
    // of the element at all.
    // NOTE: this check should not be performed unless we already know
    // that we are in the near range.
    if ( elementContainsPoint( x, y ) == false ) {
      return InRangeIndicator.NOT_IN_RANGE;
    }
    return InRangeIndicator.MOVE;
  }

  /**
   * Performs a contains test that accounts for the fact that some elements have no height or width. In that case, we
   * will check the near bounds.
   * <p/>
   * NOTE: this method should not be used until after the nearRangeElementBounds has been checked.
   *
   * @param x the x-value of the point being checked
   * @param y the y-value of the point being checked
   * @return <code>true</code> if the point is contained in the bounds of the element, <code>false</code> otherwise.
   */
  private boolean elementContainsPoint( final double x, final double y ) {
    if ( elementBounds.contains( x, y ) ) {
      return true;
    } else if ( elementBounds.getHeight() < 1.0 || elementBounds.getWidth() < 1.0 ) {
      // The following line does not need to be executed because this method should not be called
      // unless the nearRangeElementBounds check has not been executed.
      // return nearRangeElementBounds.contains(x, y);
      return true;
    }
    return false;
  }

  private boolean isNear( final double location, final double center ) {
    return Math.abs( location - center ) < SELECTION_RANGE;
  }

  public void draw( final Graphics2D g2, final ImageObserver obs ) {
    g2.setStroke( new BasicStroke( 1 ) );

    if ( WorkspaceSettings.getInstance().isAlwaysDrawElementFrames() ) {
      g2.setColor( Color.LIGHT_GRAY );
      g2.draw( elementBounds );
    }

    if ( selectedElement instanceof RootLevelBand ) {
      return;
    }

    final DrawSelectionType type = WorkspaceSettings.getInstance().getDrawSelectionType();
    if ( type == DrawSelectionType.CLAMP ) {

      g2.setColor( SELECTION_COLOR );
      drawClampRectangle( g2, elementBounds );
    } else if ( type == DrawSelectionType.OUTLINE ) {
      g2.setColor( Color.GRAY );
      g2.setStroke( DOTTED_STROKE );
      g2.draw( elementBounds );

      final Image img = IconLoader.getInstance().getSelectionEdge().getImage();
      final int halfWidth = img.getWidth( null ) / 2;
      final int halfHeight = img.getHeight( null ) / 2;

      final int leftEdge = -halfWidth + (int) elementBounds.getX();
      final int rightEdge = -halfWidth + (int) ( elementBounds.getX() + elementBounds.getWidth() );
      final int bottomEdge = -halfHeight + (int) ( elementBounds.getY() + elementBounds.getHeight() );
      final int centerHeight = -halfHeight + (int) ( elementBounds.getY() + ( elementBounds.getHeight() / 2 ) );
      final int centerWidth = -halfWidth + (int) ( elementBounds.getX() + ( elementBounds.getWidth() / 2 ) );
      final int topEdge = -halfHeight + (int) elementBounds.getY();

      g2.drawImage( img, leftEdge, topEdge, obs );
      g2.drawImage( img, centerWidth, topEdge, obs );
      g2.drawImage( img, rightEdge, topEdge, obs );


      g2.drawImage( img, leftEdge, centerHeight, obs );
      g2.drawImage( img, rightEdge, centerHeight, obs );

      g2.drawImage( img, leftEdge, bottomEdge, obs );
      g2.drawImage( img, centerWidth, bottomEdge, obs );
      g2.drawImage( img, rightEdge, bottomEdge, obs );

    }
  }


  private void drawClampRectangle( final Graphics2D g2d, final Rectangle2D rect ) {
    // top
    final double x = rect.getX();
    final int x1 = (int) x;
    final int y1 = (int) rect.getY();
    g2d.drawLine( x1, y1, x1 + SELECTION_RANGE, y1 );
    g2d.drawLine( x1, y1, x1, y1 + SELECTION_RANGE );

    final double width = rect.getWidth();
    final double centerX = x + width / 2;
    g2d.drawLine( (int) centerX - 2, y1, (int) centerX + 2, y1 );
    g2d.drawLine( (int) centerX, y1 + 1, (int) centerX, y1 + 2 );

    final double x2 = x + width;
    g2d.drawLine( (int) ( x2 - SELECTION_RANGE ), y1, (int) x2, y1 );
    g2d.drawLine( (int) x2, y1, (int) x2, y1 + SELECTION_RANGE );

    // middle
    final double centerY = rect.getY() + rect.getHeight() / 2;
    g2d.drawLine( x1, (int) centerY - 2, x1, (int) centerY + 2 );
    g2d.drawLine( x1 + 1, (int) centerY, x1 + 2, (int) centerY );

    g2d.drawLine( (int) x2, (int) centerY - 2, (int) x2, (int) centerY + 2 );
    g2d.drawLine( (int) ( x2 - 2 ), (int) centerY, (int) x2 - 1, (int) centerY );

    // low
    final double y2 = rect.getY() + rect.getHeight();
    g2d.drawLine( x1, (int) ( y2 - SELECTION_RANGE ), x1, (int) y2 );
    g2d.drawLine( x1, (int) y2, x1 + SELECTION_RANGE, (int) y2 );

    g2d.drawLine( (int) centerX - 2, (int) y2, (int) centerX + 2, (int) y2 );
    g2d.drawLine( (int) centerX, (int) y2 - 2, (int) centerX, (int) y2 - 1 );

    g2d.drawLine( (int) ( x2 - SELECTION_RANGE ), (int) y2, (int) x2, (int) y2 );
    g2d.drawLine( (int) x2, (int) ( y2 - SELECTION_RANGE ), (int) x2, (int) y2 );
  }


  public String toString() {
    return "org.pentaho.reporting.designer.core.editor.report.SelectionOverlayInformation{" + // NON-NLS
      "zoomFactor=" + zoomFactor + // NON-NLS
      ", selectedElement=" + selectedElement + // NON-NLS
      ", elementBounds=" + elementBounds + // NON-NLS
      ", layoutAge=" + layoutAge + // NON-NLS
      '}'; // NON-NLS
  }
}
