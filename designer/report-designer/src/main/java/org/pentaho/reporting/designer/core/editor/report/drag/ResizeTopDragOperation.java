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

public class ResizeTopDragOperation extends AbstractMouseDragOperation {
  private long snapThreshold;

  public ResizeTopDragOperation( final List<Element> selectedVisualElements,
                                 final Point2D originPoint,
                                 final SnapPositionsModel horizontalSnapModel,
                                 final SnapPositionsModel verticalSnapModel ) {
    super( selectedVisualElements, originPoint, horizontalSnapModel, verticalSnapModel );
    snapThreshold = WorkspaceSettings.getInstance().getSnapThreshold();
  }

  public void update( final Point2D normalizedPoint, final double zoomFactor ) {
    final SnapPositionsModel verticalSnapModel = getVerticalSnapModel();
    final Element[] selectedVisualElements = getSelectedVisualElements();
    final long originPointY = getOriginPointY();
    final long[] elementHeight = getElementHeight();
    final long[] elementY = getElementY();

    final long py = StrictGeomUtility.toInternalValue( normalizedPoint.getY() );
    final long dy = py - originPointY;

    for ( int i = 0; i < selectedVisualElements.length; i++ ) {
      final Element element = selectedVisualElements[ i ];
      if ( element instanceof RootLevelBand ) {
        continue;
      }
      final ElementStyleSheet styleSheet = element.getStyle();
      final double definedElementY = styleSheet.getDoubleStyleProperty( ElementStyleKeys.POS_Y, 0 );

      // this is where I want the element on a global scale...
      final long targetPositionY = elementY[ i ] + dy;
      final Element parent = element.getParentSection();
      final CachedLayoutData parentData = ModelUtility.getCachedLayoutData( parent );
      final long layoutedParentY = parentData.getY();

      if ( targetPositionY < layoutedParentY ) {
        continue;
      }
      // this is what we used to apply to POS_Y
      final long computedPositionY;
      if ( definedElementY >= 0 ) {
        // absolute position; resolving is easy here
        final long snapPosition = verticalSnapModel.getNearestSnapPosition( targetPositionY, element.getObjectID() );
        if ( Math.abs( snapPosition - targetPositionY ) > snapThreshold ) {
          computedPositionY = targetPositionY;
          final long localYPosition = Math.max( 0, targetPositionY - layoutedParentY );
          final float position = (float) StrictGeomUtility.toExternalValue( localYPosition );
          styleSheet.setStyleProperty( ElementStyleKeys.POS_Y, new Float( position ) );
        } else {
          computedPositionY = snapPosition;
          final long localYPosition = Math.max( 0, snapPosition - layoutedParentY );
          final float position = (float) StrictGeomUtility.toExternalValue( localYPosition );
          styleSheet.setStyleProperty( ElementStyleKeys.POS_Y, new Float( position ) );
        }
      } else {
        final long parentBase;
        if ( isCanvasElement( parent ) ) {
          parentBase = parentData.getHeight();
        } else {
          parentBase = parentData.getWidth();
        }
        if ( parentBase > 0 ) {
          // relative position; resolve the percentage against the width of the parent.
          final long snapPosition = verticalSnapModel.getNearestSnapPosition( targetPositionY, element.getObjectID() );
          if ( Math.abs( snapPosition - targetPositionY ) > snapThreshold ) {
            computedPositionY = targetPositionY;
            final long localYPosition = Math.max( 0, targetPositionY - layoutedParentY );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localYPosition * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_Y,
              new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
          } else {
            computedPositionY = snapPosition;
            final long localYPosition = Math.max( 0, snapPosition - layoutedParentY );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localYPosition * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_Y,
              new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
          }
        } else {
          // we cannot handle this element.
          continue;
        }
      }

      final double elementMinHeight = styleSheet.getDoubleStyleProperty( ElementStyleKeys.MIN_HEIGHT, 0 );
      final long targetY2 = elementY[ i ] + elementHeight[ i ];
      if ( elementMinHeight >= 0 ) {
        final long localHeight = Math.max( 0, targetY2 - computedPositionY );
        final float position = (float) StrictGeomUtility.toExternalValue( localHeight );
        styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( position ) );
      } else {
        final long parentBase = parentData.getHeight();
        if ( parentBase > 0 ) {
          final long localHeight = Math.max( 0, targetY2 - computedPositionY );
          // strict geometry: all values are multiplied by 1000
          // percentages in the engine are represented by floats betwen 0 and 100.
          final long percentage = StrictGeomUtility.toInternalValue( localHeight * 100 / parentBase );
          styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT,
            new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
        }
      }

      element.notifyNodePropertiesChanged();
    }
  }

  public void finish() {

  }
}
