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


package org.pentaho.reporting.engine.classic.core.designtime.compat;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

public class LayoutCompatibility_3_9_Converter extends AbstractCompatibilityConverter {
  public LayoutCompatibility_3_9_Converter() {
  }

  public int getTargetVersion() {
    return ClassicEngineBoot.computeVersionId( 3, 9, 0 );
  }

  public void inspectElement( final ReportElement element ) {
    element.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, null );
    element.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, null );
    element.getStyle().setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, null );
    if ( element.getMetaData().getReportElementType() == ElementMetaData.TypeClassification.CONTROL
        || element.getMetaData().getReportElementType() == ElementMetaData.TypeClassification.SUBREPORT ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, null );
    }

    if ( element.getMetaData().getReportElementType() == ElementMetaData.TypeClassification.CONTROL ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.MAX_WIDTH, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.WIDTH, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, null );

      element.setStyleExpression( ElementStyleKeys.MIN_WIDTH, null );
      element.setStyleExpression( ElementStyleKeys.MIN_HEIGHT, null );
      element.setStyleExpression( ElementStyleKeys.MAX_WIDTH, null );
      element.setStyleExpression( ElementStyleKeys.MAX_HEIGHT, null );
      element.setStyleExpression( ElementStyleKeys.WIDTH, null );
      element.setStyleExpression( ElementStyleKeys.HEIGHT, null );
    }
  }
}
