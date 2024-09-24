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

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class MasterReportType extends AbstractSectionType {
  public static final MasterReportType INSTANCE = new MasterReportType();

  public MasterReportType() {
    super( "master-report", true );
  }

  public ReportElement create() {
    return new MasterReport();
  }
}
