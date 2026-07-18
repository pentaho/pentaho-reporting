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



package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportFooter;

public class ReportFooterType extends AbstractSectionType {
  public static final ReportFooterType INSTANCE = new ReportFooterType();

  public ReportFooterType() {
    super( "report-footer", true );
  }

  public ReportElement create() {
    return new ReportFooter();
  }
}
