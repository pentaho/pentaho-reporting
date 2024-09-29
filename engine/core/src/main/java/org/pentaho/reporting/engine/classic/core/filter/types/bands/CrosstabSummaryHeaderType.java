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


package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import org.pentaho.reporting.engine.classic.core.CrosstabSummaryHeader;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class CrosstabSummaryHeaderType extends AbstractSectionType {
  public static final CrosstabSummaryHeaderType INSTANCE = new CrosstabSummaryHeaderType();

  public CrosstabSummaryHeaderType() {
    super( "crosstab-summary-header", false );
  }

  public ReportElement create() {
    return new CrosstabSummaryHeader();
  }
}
