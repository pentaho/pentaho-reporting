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

package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.editor.report.AbstractReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;

import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;


/**
 * Base drag handler to handle various sub-reports like crosstabs
 *
 * @author Sulaiman Karmali
 */
public abstract class AbstractSubReportElementDragHandler extends AbstractReportElementDragHandler {
  public AbstractSubReportElementDragHandler() {
  }

  protected boolean isFilteredDropZone( final DropTargetEvent event,
                                        final ReportElementEditorContext dragContext,
                                        final ElementMetaData elementMetaData,
                                        final Point2D point ) {
    final Element rootBand = findRootBand( dragContext, point );
    final ElementMetaData metaData = rootBand.getElementType().getMetaData();
    final ElementMetaData.TypeClassification reportElementType = metaData.getReportElementType();
    if ( reportElementType == ElementMetaData.TypeClassification.HEADER ||
      reportElementType == ElementMetaData.TypeClassification.FOOTER ) {
      return true;
    }
    return false;
  }

  protected Element findRootBand( final ReportElementEditorContext dragContext,
                                  final Point2D point ) {
    Element element = dragContext.getElementForLocation( point, false );
    while ( element != null && ( ( element instanceof RootLevelBand ) == false ) ) {
      element = element.getParent();
    }

    if ( element != null ) {
      return element;
    }

    return dragContext.getDefaultElement();
  }

}
