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


package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;

public class SubReportType extends AbstractSectionType {
  public static final SubReportType INSTANCE = new SubReportType();

  public SubReportType() {
    super( "sub-report", true );
  }

  public ReportElement create() {
    return new SubReport();
  }
}
