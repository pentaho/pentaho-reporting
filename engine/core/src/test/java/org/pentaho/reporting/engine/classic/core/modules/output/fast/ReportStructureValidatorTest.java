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

package org.pentaho.reporting.engine.classic.core.modules.output.fast;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;

public class ReportStructureValidatorTest {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testShallowInlineSubReportDetection() {

    SubReport srInner = new SubReport();

    SubReport sr = new SubReport();
    sr.getReportHeader().addElement( srInner );

    MasterReport report = new MasterReport();
    report.getReportHeader().addElement( sr );

    ReportStructureValidator v = new ReportStructureValidator();
    Assert.assertFalse( v.isValidForFastProcessing( report ) );
  }

  @Test
  public void testDeepInlineSubReportDetection() {

    SubReport srInner = new SubReport();

    SubReport sr = new SubReport();
    sr.getReportHeader().addElement( srInner );

    MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport( sr );

    ReportStructureValidator v = new ReportStructureValidator();
    Assert.assertFalse( v.isValidForFastProcessing( report ) );
  }

  @Test
  public void testDeepSubBandInlineSubReportDetection() {

    SubReport srInner = new SubReport();

    Band band = new Band();
    band.addElement( srInner );

    SubReport sr = new SubReport();
    sr.getReportHeader().addElement( band );

    MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport( sr );

    ReportStructureValidator v = new ReportStructureValidator();
    Assert.assertFalse( v.isValidForFastProcessing( report ) );
  }

  @Test
  public void testNoneInlineSubReportDetection() {

    SubReport srInner = new SubReport();

    SubReport sr = new SubReport();
    sr.getReportHeader().addSubReport( srInner );

    MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport( sr );

    ReportStructureValidator v = new ReportStructureValidator();
    Assert.assertTrue( v.isValidForFastProcessing( report ) );
  }

}
