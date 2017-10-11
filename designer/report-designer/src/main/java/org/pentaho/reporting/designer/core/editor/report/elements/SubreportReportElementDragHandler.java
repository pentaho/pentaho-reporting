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

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.Locale;

/**
 * Subreport drag handler
 *
 * @author Thomas Morgner
 */
public class SubreportReportElementDragHandler extends AbstractSubReportElementDragHandler {
  public SubreportReportElementDragHandler() {
  }

  protected void postProcessDrop( final Element visualElement,
                                  final Band target,
                                  final ReportElementEditorContext dragContext,
                                  final Point2D point ) {
    final Element rootBand = findRootBand( dragContext, point );
    SwingUtilities.invokeLater( new SubreportConfigureHandler
      ( (SubReport) visualElement, target, dragContext, rootBand == target ) );
  }

  protected Element createElement( final ElementMetaData elementMetaData,
                                   final String fieldName,
                                   final ReportDocumentContext context ) throws InstantiationException {
    // Create a subreport element
    final ElementType type = elementMetaData.create();
    final SubReport visualElement = new SubReport();
    SubreportConfigureHandler.configureDefaults( visualElement );

    type.configureDesignTimeDefaults( visualElement, Locale.getDefault() );

    final ElementStyleSheet styleSheet = visualElement.getStyle();
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH );
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT );
    return visualElement;
  }
}
