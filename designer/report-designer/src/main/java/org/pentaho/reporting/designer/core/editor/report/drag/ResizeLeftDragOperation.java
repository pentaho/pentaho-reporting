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

/**
 * This is a combination of move and resize. At first we move and then we add the moved distance to the element's
 * height/width.
 *
 * @author Thomas Morgner
 */
public class ResizeLeftDragOperation extends AbstractMouseDragOperation {
  private long snapThreshold;

  public ResizeLeftDragOperation( final List<Element> selectedVisualElements,
                                  final Point2D originPoint,
                                  final SnapPositionsModel horizontalSnapModel,
                                  final SnapPositionsModel verticalSnapModel ) {
    super( selectedVisualElements, originPoint, horizontalSnapModel, verticalSnapModel );
    snapThreshold = WorkspaceSettings.getInstance().getSnapThreshold();
  }

  public void update( final Point2D normalizedPoint, final double zoomFactor ) {
    final SnapPositionsModel horizontalSnapModel = getHorizontalSnapModel();
    final Element[] selectedVisualElements = getSelectedVisualElements();
    final long originPointX = getOriginPointX();
    final long[] elementWidth = getElementWidth();
    final long[] elementX = getElementX();

    final long px = StrictGeomUtility.toInternalValue( normalizedPoint.getX() );
    final long dx = px - originPointX;

    for ( int i = 0; i < selectedVisualElements.length; i++ ) {
      final Element element = selectedVisualElements[ i ];
      if ( element instanceof RootLevelBand ) {
        continue;
      }
      final ElementStyleSheet styleSheet = element.getStyle();
      final double definedElementX = styleSheet.getDoubleStyleProperty( ElementStyleKeys.POS_X, 0 );

      // this is where I want the element on a global scale...
      final long targetPositionX = elementX[ i ] + dx;
      final Element parent = element.getParentSection();
      final CachedLayoutData parentData = ModelUtility.getCachedLayoutData( parent );
      final long layoutedParentX = parentData.getX();

      if ( targetPositionX < layoutedParentX ) {
        continue;
      }
      // this is what we used to apply to POS_X
      final long computedPositionX;
      if ( definedElementX >= 0 ) {
        // absolute position; resolving is easy here
        final long snapPosition = horizontalSnapModel.getNearestSnapPosition( targetPositionX, element.getObjectID() );
        if ( Math.abs( snapPosition - targetPositionX ) > snapThreshold ) {
          computedPositionX = targetPositionX;
          final long localXPosition = Math.max( 0, targetPositionX - layoutedParentX );
          final float position = (float) StrictGeomUtility.toExternalValue( localXPosition );
          styleSheet.setStyleProperty( ElementStyleKeys.POS_X, new Float( position ) );
        } else {
          computedPositionX = snapPosition;
          final long localXPosition = Math.max( 0, snapPosition - layoutedParentX );
          final float position = (float) StrictGeomUtility.toExternalValue( localXPosition );
          styleSheet.setStyleProperty( ElementStyleKeys.POS_X, new Float( position ) );
        }
      } else {
        final long parentBase = parentData.getWidth();
        if ( parentBase > 0 ) {
          // relative position; resolve the percentage against the width of the parent.
          final long snapPosition =
            horizontalSnapModel.getNearestSnapPosition( targetPositionX, element.getObjectID() );
          if ( Math.abs( snapPosition - targetPositionX ) > snapThreshold ) {
            computedPositionX = targetPositionX;
            final long localXPosition = Math.max( 0, targetPositionX - layoutedParentX );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localXPosition * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_X,
              new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
          } else {
            computedPositionX = snapPosition;
            final long localXPosition = Math.max( 0, snapPosition - layoutedParentX );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localXPosition * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.POS_X,
              new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
          }
        } else {
          // we cannot handle this element.
          continue;
        }
      }

      final double elementMinWidth = styleSheet.getDoubleStyleProperty( ElementStyleKeys.MIN_WIDTH, 0 );
      final long targetX2 = elementX[ i ] + elementWidth[ i ];
      if ( elementMinWidth >= 0 ) {
        final long localWidth = Math.max( 0, targetX2 - computedPositionX );
        final float position = (float) StrictGeomUtility.toExternalValue( localWidth );
        styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( position ) );
      } else {
        final long parentBase = parentData.getWidth();
        if ( parentBase > 0 ) {
          final long localWidth = Math.max( 0, targetX2 - computedPositionX );
          // strict geometry: all values are multiplied by 1000
          // percentages in the engine are represented by floats betwen 0 and 100.
          final long percentage = StrictGeomUtility.toInternalValue( localWidth * 100 / parentBase );
          styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH,
            new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
        }
      }

      element.notifyNodePropertiesChanged();
    }
  }

  public void finish() {

  }
}
