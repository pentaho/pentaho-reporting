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
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractMouseDragOperation implements MouseDragOperation {
  private Element[] selectedVisualElements;
  private long[] elementX;
  private long[] elementY;
  private long[] elementWidth;
  private long[] elementHeight;

  private SnapPositionsModel horizontalSnapModel;
  private SnapPositionsModel verticalSnapModel;
  private long originPointX;
  private long originPointY;

  protected AbstractMouseDragOperation( final List<Element> selectedVisualElements,
                                        final Point2D originPoint,
                                        final SnapPositionsModel horizontalSnapModel,
                                        final SnapPositionsModel verticalSnapModel ) {
    final ArrayList<Element> nonDescendants = new ArrayList<Element>( selectedVisualElements.size() );
    for ( int i = 0; i < selectedVisualElements.size(); i++ ) {
      final Element element = selectedVisualElements.get( i );
      if ( isDescendant( element, selectedVisualElements ) == false ) {
        nonDescendants.add( element );
      }
    }

    this.selectedVisualElements = nonDescendants.toArray( new Element[ nonDescendants.size() ] );

    this.originPointX = StrictGeomUtility.toInternalValue( originPoint.getX() );
    this.originPointY = StrictGeomUtility.toInternalValue( originPoint.getY() );
    this.horizontalSnapModel = horizontalSnapModel;
    this.verticalSnapModel = verticalSnapModel;
    this.elementX = new long[ this.selectedVisualElements.length ];
    this.elementY = new long[ this.selectedVisualElements.length ];
    this.elementWidth = new long[ this.selectedVisualElements.length ];
    this.elementHeight = new long[ this.selectedVisualElements.length ];

    for ( int i = 0; i < this.selectedVisualElements.length; i++ ) {
      final Element element = this.selectedVisualElements[ i ];
      final CachedLayoutData data = ModelUtility.getCachedLayoutData( element );
      elementX[ i ] = data.getX();
      elementY[ i ] = data.getY();
      elementWidth[ i ] = data.getWidth();
      elementHeight[ i ] = data.getHeight();
    }
  }


  /**
   * Tests, whether the array of elements contains a parent of the given element.
   *
   * @param element
   * @param elements
   * @return
   */
  protected boolean isDescendant( final Element element, final List<Element> elements ) {
    final HashSet<Element> parents = new HashSet<Element>();
    Element parent = element.getParentSection();
    while ( parent != null ) {
      parents.add( parent );
      parent = parent.getParent();
    }

    for ( Element visualReportElement : elements ) {
      if ( element == visualReportElement ) {
        continue;
      }
      if ( visualReportElement instanceof AbstractReportDefinition ) {
        return false;
      }
      if ( visualReportElement instanceof RootLevelBand ) {
        return true;
      }
      if ( parents.contains( visualReportElement ) ) {
        return true;
      }
    }
    return false;
  }

  public Element[] getSelectedVisualElements() {
    return selectedVisualElements;
  }

  public long[] getElementX() {
    return elementX;
  }

  public long[] getElementY() {
    return elementY;
  }

  public long[] getElementWidth() {
    return elementWidth;
  }

  public long[] getElementHeight() {
    return elementHeight;
  }

  public SnapPositionsModel getHorizontalSnapModel() {
    return horizontalSnapModel;
  }

  public SnapPositionsModel getVerticalSnapModel() {
    return verticalSnapModel;
  }

  public long getOriginPointX() {
    return originPointX;
  }

  public long getOriginPointY() {
    return originPointY;
  }


  protected boolean isCanvasElement( final Element reportElement ) {
    final ElementStyleSheet styleSheet = reportElement.getStyle();
    final Object o = styleSheet.getStyleProperty( BandStyleKeys.LAYOUT );

    if ( BandStyleKeys.LAYOUT_CANVAS.equals( o ) ) {
      return true;
    }
    if ( o != null ) {
      return false;
    }
    return true;
  }

}
