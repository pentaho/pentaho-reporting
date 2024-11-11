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


package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Prd5172IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testBandedSubReportIsInline() throws Exception {
    SubReport subReport = new SubReport();
    subReport.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_INLINE );

    MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport( subReport );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
  }

  @Test
  public void testInlineSubReportIsInline() throws Exception {
    SubReport subReport = new SubReport();
    subReport.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_INLINE );

    MasterReport report = new MasterReport();
    report.getReportHeader().addElement( subReport );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
  }

  @Test
  public void testMasterReportInline() throws Exception {
    MasterReport report = new MasterReport();
    report.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_INLINE );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
  }
}
