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
import org.pentaho.reporting.designer.core.editor.report.AbstractReportElementDragHandler;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

import java.util.Locale;

public class BandReportElementDragHandler extends AbstractReportElementDragHandler {
  public BandReportElementDragHandler() {
  }

  protected Element createElement( final ElementMetaData elementMetaData,
                                   final String fieldName,
                                   final ReportDocumentContext context ) throws InstantiationException {
    final ElementType type = elementMetaData.create();
    final Element visualElement = new Band();
    type.configureDesignTimeDefaults( visualElement, Locale.getDefault() );

    final ElementStyleSheet styleSheet = visualElement.getStyle();
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH );
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT );
    return visualElement;
  }
}
