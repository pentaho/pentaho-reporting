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

public class ResizeBottomDragOperation extends AbstractMouseDragOperation {
  private long snapThreshold;

  public ResizeBottomDragOperation( final List<Element> selectedVisualElements,
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

    final long py = StrictGeomUtility.toInternalValue( normalizedPoint.getY() );

    final long dy = py - originPointY;

    for ( int i = 0; i < selectedVisualElements.length; i++ ) {
      final Element element = selectedVisualElements[ i ];
      if ( element instanceof RootLevelBand ) {
        continue;
      }
      final ElementStyleSheet styleSheet = element.getStyle();
      final double elementMinHeight = styleSheet.getDoubleStyleProperty( ElementStyleKeys.MIN_HEIGHT, 0 );

      // this is where I want the element on a global scale...
      final long targetheight = elementHeight[ i ] + dy;

      final CachedLayoutData data = ModelUtility.getCachedLayoutData( element );
      final long elementY = data.getY();
      final long targetY2 = elementY + targetheight;

      if ( elementMinHeight >= 0 ) {
        // absolute position; resolving is easy here
        final long snapPosition = verticalSnapModel.getNearestSnapPosition( targetY2, element.getObjectID() );
        if ( Math.abs( snapPosition - targetY2 ) > snapThreshold ) {
          final long localHeight = Math.max( 0, targetY2 - elementY );
          final float position = (float) StrictGeomUtility.toExternalValue( localHeight );
          styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( position ) );
        } else {
          final long localHeight = Math.max( 0, snapPosition - elementY );
          final float position = (float) StrictGeomUtility.toExternalValue( localHeight );
          styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( position ) );
        }
      } else {

        final Element parent = element.getParentSection();
        final CachedLayoutData parentData = ModelUtility.getCachedLayoutData( parent );
        final long parentBase;
        if ( isCanvasElement( parent ) ) {
          parentBase = parentData.getHeight();
        } else {
          parentBase = parentData.getWidth();
        }
        if ( parentBase > 0 ) {
          // relative position; resolve the percentage against the height of the parent.
          final long snapPosition = verticalSnapModel.getNearestSnapPosition( targetY2, element.getObjectID() );
          if ( Math.abs( snapPosition - targetY2 ) > snapThreshold ) {
            final long localHeight = Math.max( 0, targetY2 - elementY );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localHeight * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT,
              new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
          } else {
            final long localHeight = Math.max( 0, snapPosition - elementY );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localHeight * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT,
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
