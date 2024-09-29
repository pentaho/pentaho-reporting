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

public class ResizeRightDragOperation extends AbstractMouseDragOperation {
  private long snapThreshold;

  public ResizeRightDragOperation( final List<Element> selectedVisualElements,
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
    final long px = StrictGeomUtility.toInternalValue( normalizedPoint.getX() );
    final long dx = px - originPointX;

    for ( int i = 0; i < selectedVisualElements.length; i++ ) {
      final Element element = selectedVisualElements[ i ];
      if ( element instanceof RootLevelBand ) {
        continue;
      }
      final ElementStyleSheet styleSheet = element.getStyle();
      final double elementMinWidth = styleSheet.getDoubleStyleProperty( ElementStyleKeys.MIN_WIDTH, 0 );

      // this is where I want the element on a global scale...
      final long targetWidth = elementWidth[ i ] + dx;
      final CachedLayoutData data = ModelUtility.getCachedLayoutData( element );
      final long elementX = data.getX();
      final long targetX2 = elementX + targetWidth;

      if ( elementMinWidth >= 0 ) {
        // absolute position; resolving is easy here
        final long snapPosition = horizontalSnapModel.getNearestSnapPosition( targetX2, element.getObjectID() );
        if ( Math.abs( snapPosition - targetX2 ) > snapThreshold ) {
          final long localWidth = Math.max( 0, targetX2 - elementX );
          final float position = (float) StrictGeomUtility.toExternalValue( localWidth );
          styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( position ) );
        } else {
          final long localWidth = Math.max( 0, snapPosition - elementX );
          final float position = (float) StrictGeomUtility.toExternalValue( localWidth );
          styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( position ) );
        }
      } else {
        final Element parent = element.getParentSection();
        final CachedLayoutData parentData = ModelUtility.getCachedLayoutData( parent );

        final long parentBase = parentData.getWidth();
        if ( parentBase > 0 ) {
          // relative position; resolve the percentage against the height of the parent.
          final long snapPosition = horizontalSnapModel.getNearestSnapPosition( targetX2, element.getObjectID() );
          if ( Math.abs( snapPosition - targetX2 ) > snapThreshold ) {
            final long localWidth = Math.max( 0, targetX2 - elementX );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localWidth * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH,
              new Float( StrictGeomUtility.toExternalValue( -percentage ) ) );
          } else {
            final long localWidth = Math.max( 0, snapPosition - elementX );
            // strict geometry: all values are multiplied by 1000
            // percentages in the engine are represented by floats betwen 0 and 100.
            final long percentage = StrictGeomUtility.toInternalValue( localWidth * 100 / parentBase );
            styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH,
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
