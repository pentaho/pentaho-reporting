/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.Locale;

/**
 * Crosstab drag handler
 *
 * @author Sulaiman Karmali
 */
public class CrosstabReportElementDragHandler extends AbstractSubReportElementDragHandler {

  public CrosstabReportElementDragHandler() {
    super();
  }

  protected void postProcessDrop( final Element visualElement,
                                  final Band target,
                                  final ReportElementEditorContext dragContext,
                                  final Point2D point ) {
    final Element rootBand = findRootBand( dragContext, point );
    SwingUtilities.invokeLater( new CrosstabConfigureHandler
      ( (CrosstabElement) visualElement, target, dragContext, rootBand == target ) );
  }

  protected Element createElement( final ElementMetaData elementMetaData,
                                   final String fieldName,
                                   final ReportDocumentContext context ) throws InstantiationException {
    // Create a crosstab element
    final ElementType type = elementMetaData.create();
    final CrosstabElement visualElement = new CrosstabElement();
    visualElement.setElementType( type );
    visualElement.setRootGroup( new CrosstabGroup() );

    CrosstabConfigureHandler.configureDefaults( visualElement );

    type.configureDesignTimeDefaults( visualElement, Locale.getDefault() );

    final ElementStyleSheet styleSheet = visualElement.getStyle();
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH );
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT );

    return visualElement;
  }
}
