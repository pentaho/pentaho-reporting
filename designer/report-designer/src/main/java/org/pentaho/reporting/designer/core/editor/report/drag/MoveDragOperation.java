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

package org.pentaho.reporting.designer.core.editor.report.drag;

import org.pentaho.reporting.designer.core.editor.report.snapping.SnapPositionsModel;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.geom.Point2D;
import java.util.List;

public class MoveDragOperation extends AbstractMouseDragOperation {
  private long snapThreshold;

  public MoveDragOperation( final List<Element> selectedVisualElements,
                            final Point2D originPoint,
                            final SnapPositionsModel horizontalSnapModel,
                            final SnapPositionsModel verticalSnapModel ) {
    super( selectedVisualElements, originPoint, horizontalSnapModel, verticalSnapModel );
    snapThreshold = WorkspaceSettings.getInstance().getSnapThreshold();
  }

  public void update( final Point2D normalizedPoint, final double zoomFactor ) {
    final SnapPositionsModel horizontalSnapModel = getHorizontalSnapModel();
    final SnapPositionsModel verticalSnapModel = getVerticalSnapModel();
    final Element[] selectedVisualElements = getSelectedVisualElements();
    final long originPointX = getOriginPointX();
    final long originPointY = getOriginPointY();
    final long[] elementXCoords = getElementX();
    final long[] elementYCoords = getElementY();
    final long[] elementWidth = getElementWidth();
    final long[] elementHeight = getElementHeight();

    final long px = StrictGeomUtility.toInternalValue( normalizedPoint.getX() );
    final long py = StrictGeomUtility.toInternalValue( normalizedPoint.getY() );

    final long dx = px - originPointX;
    final long dy = py - originPointY;

    for ( int i = 0; i < selectedVisualElements.length; i++ ) {
      final Element element = selectedVisualElements[ i ];
      if ( element instanceof RootLevelBand ) {
        continue;
      }
      final ElementStyleSheet styleSheet = element.getStyle();
      final double elementX = styleSheet.getDoubleStyleProperty( ElementStyleKeys.POS_X, 0 );
      final double elementY = styleSheet.getDoubleStyleProperty( ElementStyleKeys.POS_Y, 0 );

      final Element parent = element.getParentSection();
      if ( parent == null ) {
        throw new IllegalStateException(
          "Parent has been removed, but the drag operation was not finished: " + element );
      }
      final CachedLayoutData data = ModelUtility.getCachedLayoutData( parent );
      final long layoutedParentX = data.getX() + data.getPaddingX();
      final long layoutedParentY = data.getY() + data.getPaddingY();

      // this is where I want the element on a global scale...
      final long targetPositionX = elementXCoords[ i ] + dx;
      final long targetPositionY = elementYCoords[ i ] + dy;

      if ( elementX >= 0 ) {
        // absolute position; resolving is easy here
        final long snapPosition = horizontalSnapModel.getNearestSnapPosition
          ( targetPositionX, element.getObjectID() );
        if ( Math.abs( snapPosition - targetPositionX ) > snapThreshold ) {
          final long targetPositionX2 = targetPositionX + elementWidth[ i ];
          final long snapPosition2 = horizontalSnapModel.getNearestSnapPosition
            ( targetPositionX2, element.getObjectID() );
          if ( Math.abs( snapPosition2 - targetPositionX2 ) < snapThreshold ) {
            // snapping to the right border..
            final long snapX = snapPosition2 - elementWidth[ i ];
            final long localXPosition = Math.max( 0, snapX - layoutedParentX );
            final float position = (float) StrictGeomUtility.toExternalValue( localXPosition );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_X, new Float( position ) );
          } else {
            // not snapping ...
            final long localXPosition = Math.max( 0, targetPositionX - layoutedParentX );
            final float position = (float) StrictGeomUtility.toExternalValue( localXPosition );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_X, new Float( position ) );
          }
        } else {
          // snapping to the left border..
          final long localXPosition = Math.max( 0, snapPosition - layoutedParentX );
          final float position = (float) StrictGeomUtility.toExternalValue( localXPosition );
          styleSheet.setStyleProperty( ElementStyleKeys.POS_X, new Float( position ) );
        }
      } else {
        final long parentBase = data.getWidth();
        if ( parentBase > 0 ) {
          // relative position; resolve the percentage against the width of the parent.
          final long snapPosition = horizontalSnapModel.getNearestSnapPosition( targetPositionX,
            element.getObjectID() );
          if ( Math.abs( snapPosition - targetPositionX ) > snapThreshold ) {
            final long targetPositionX2 = targetPositionX + elementWidth[ i ];
            final long snapPosition2 = horizontalSnapModel.getNearestSnapPosition( targetPositionX2,
              element.getObjectID() );
            if ( Math.abs( snapPosition2 - targetPositionX2 ) < snapThreshold ) {
              // snapping to the right
              final long snapX = snapPosition2 - elementWidth[ i ];
              final long localXPosition = Math.max( 0, snapX - layoutedParentX );
              final long percentage = StrictGeomUtility.toInternalValue( localXPosition * 100 / parentBase );
              styleSheet.setStyleProperty( ElementStyleKeys.POS_X,
                new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
            } else {
              // not snapping
              final long localXPosition = Math.max( 0, targetPositionX - layoutedParentX );
              // strict geometry: all values are multiplied by 1000
              // percentages in the engine are represented by floats betwen 0 and 100.
              final long percentage = StrictGeomUtility.toInternalValue( localXPosition * 100 / parentBase );
              styleSheet.setStyleProperty( ElementStyleKeys.POS_X,
                new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
            }
          } else {
            // snapping to the left
            final long localXPosition = Math.max( 0, snapPosition - layoutedParentX );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localXPosition * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_X,
              new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
          }
        }
      }

      if ( elementY >= 0 ) {
        // absolute position; resolving is easy here
        final long snapPosition = verticalSnapModel.getNearestSnapPosition( targetPositionY, element.getObjectID() );
        if ( Math.abs( snapPosition - targetPositionY ) > snapThreshold ) {
          final long targetPositionY2 = targetPositionY + elementHeight[ i ];
          final long snapPosition2 = horizontalSnapModel.getNearestSnapPosition( targetPositionY2,
            element.getObjectID() );
          if ( Math.abs( snapPosition2 - targetPositionY2 ) < snapThreshold ) {
            // snap to the bottom
            final long snapY = snapPosition2 - elementHeight[ i ];
            final long localYPosition = Math.max( 0, snapY - layoutedParentY );
            final float position = (float) StrictGeomUtility.toExternalValue( localYPosition );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_Y, new Float( position ) );
          } else {
            // not snapping
            final long localYPosition = Math.max( 0, targetPositionY - layoutedParentY );
            final float position = (float) StrictGeomUtility.toExternalValue( localYPosition );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_Y, new Float( position ) );
          }
        } else {
          // snap to the top
          final long localYPosition = Math.max( 0, snapPosition - layoutedParentY );
          final float position = (float) StrictGeomUtility.toExternalValue( localYPosition );
          styleSheet.setStyleProperty( ElementStyleKeys.POS_Y, new Float( position ) );
        }
      } else {

        final long parentBase;
        if ( isCanvasElement( parent ) ) {
          parentBase = data.getHeight();
        } else {
          parentBase = data.getWidth();
        }
        if ( parentBase > 0 ) {
          // relative position; resolve the percentage against the height of the parent.
          final long snapPosition = verticalSnapModel.getNearestSnapPosition( targetPositionY, element.getObjectID() );
          if ( Math.abs( snapPosition - targetPositionY ) > snapThreshold ) {
            final long targetPositionY2 = targetPositionY + elementHeight[ i ];
            final long snapPosition2 = horizontalSnapModel.getNearestSnapPosition( targetPositionY2,
              element.getObjectID() );
            if ( Math.abs( snapPosition2 - targetPositionY2 ) < snapThreshold ) {
              // snap to the bottom
              final long snapY = snapPosition2 - elementHeight[ i ];
              final long localYPosition = Math.max( 0, snapY - layoutedParentY );
              final long percentage = StrictGeomUtility.toInternalValue( localYPosition * 100 / parentBase );
              styleSheet.setStyleProperty( ElementStyleKeys.POS_Y,
                new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
            } else {
              // not snapping at all
              final long localYPosition = Math.max( 0, targetPositionY - layoutedParentY );
              // strict geometry: all values are multiplied by 1000
              // percentages in the engine are represented by floats betwen 0 and 100.
              final long percentage = StrictGeomUtility.toInternalValue( localYPosition * 100 / parentBase );
              styleSheet.setStyleProperty( ElementStyleKeys.POS_Y,
                new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
            }
          } else {
            // snap to the top 
            final long localYPosition = Math.max( 0, snapPosition - layoutedParentY );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localYPosition * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_Y,
              new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
          }
        }
      }

      element.notifyNodePropertiesChanged();
    }
  }

  public void finish() {

  }

}

