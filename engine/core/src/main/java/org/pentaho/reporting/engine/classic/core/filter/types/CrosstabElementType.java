/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.AbstractSectionType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * Implementation for crosstab element type.
 *
 * @author Sulaiman Karmali
 */
public class CrosstabElementType extends AbstractSectionType {
  public static final CrosstabElementType INSTANCE = new CrosstabElementType();

  public CrosstabElementType() {
    super( "crosstab-report", true );
  }

  public ReportElement create() {
    return new CrosstabElement();
  }

  @Override
  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return "Crosstab-Report";
  }
}
