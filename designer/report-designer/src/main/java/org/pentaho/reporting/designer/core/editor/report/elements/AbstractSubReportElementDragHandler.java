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
